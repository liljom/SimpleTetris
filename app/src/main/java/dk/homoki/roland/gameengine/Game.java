package dk.homoki.roland.gameengine;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class Game extends Activity implements Runnable, View.OnKeyListener, SensorEventListener
{
    private Thread mainLoopThread;
    private State state = State.Paused;
    private List<State> statesChanges = new ArrayList<>();
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Screen screen;
    private Canvas canvas;
    private Bitmap offscreenSurface;
    private boolean pressedKeys[] = new boolean[256];
    private KeyEventPool keyEventPool = new KeyEventPool();
    private List<MyKeyEvent> keyEvents = new ArrayList<>();
    private List<MyKeyEvent> keyEventBuffer = new ArrayList<>();
    private TouchHandler touchHandler;
    private List<TouchEvent> touchEvents = new ArrayList<>();
    private List<TouchEvent> touchEventBuffer = new ArrayList<>();
    private TouchEventPool touchEventPool = new TouchEventPool();
    //private float[] accelerometer = new float[3];
    private float[] accelerometer = {0.0f, 0.0f, 0.0f};
    private SoundPool soundPool;
    private int framesPerSecond = -1;
    private Paint paint = new Paint();
    private SharedPreferences sharedPref;
    private boolean goOn;
    public float highScore;

    public abstract Screen createStartScreen();

    protected void onCreate(Bundle instanceState)
    {
        super.onCreate(instanceState);
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //Get the SurfaceView object i.e. dedicated drawing surface
        surfaceView = new SurfaceView(this);
        setContentView(surfaceView);
        surfaceHolder = surfaceView.getHolder();
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        this.soundPool = new SoundPool(20, AudioManager.STREAM_MUSIC, 0);
        screen = createStartScreen();

        surfaceView.setFocusableInTouchMode(true);
        surfaceView.requestFocus();
        surfaceView.setOnKeyListener(this);
        touchHandler = new MultiTouchHandler(surfaceView, touchEventBuffer, touchEventPool);
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() != 0)
        {
            Sensor accelerometer = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        // retrieve previous high score
        sharedPref = getPreferences(Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_high_score_default);
        String hs = sharedPref.getString(Integer.toString(R.string.saved_high_score), defaultValue);
        highScore = Float.parseFloat(hs);
    }

    public void saveNewHighScore(float newHighScore)
    {
        highScore = newHighScore;
        SharedPreferences.Editor editor = sharedPref.edit();
        String hs = Float.toString(newHighScore);
        editor.putString(Integer.toString(R.string.saved_high_score), hs);
        editor.apply();
    }

    public void setOffscreenSurface(int width, int height)
    {
        if (offscreenSurface != null) offscreenSurface.recycle();
        offscreenSurface = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        canvas = new Canvas(offscreenSurface);
    }
    public int getOffscreenWidth() {return offscreenSurface.getWidth();}
    public int getOffscreenHeight() {return offscreenSurface.getHeight();}

    public void setScreen(Screen newScreen)
    {
        if (this.screen != null) this.screen.dispose();
        this.screen = newScreen;
    }

    public Typeface loadFont(String fileName)
    {
        Typeface font = Typeface.createFromAsset(getAssets(), fileName);
        if (font == null)
        {
            throw new RuntimeException("Could not load font from asset: " + fileName);
        }
        return font;
    }

    public void drawText(Typeface font, String text, int x, int y, int color, int size)
    {
        paint.setTypeface(font);
        paint.setTextSize(size);
        paint.setColor(color);
        canvas.drawText(text, x, y + size, paint);
    }

    public Bitmap loadBitMap(String fileName)
    {
        InputStream in = null;
        Bitmap bitmap;
        try
        {
            in = getAssets().open(fileName);
            bitmap = BitmapFactory.decodeStream(in);
            if (bitmap == null)
                throw new RuntimeException("Could not get a bitmap from the file " + fileName);
            return bitmap;
        }
        catch(IOException e)
        {
            throw new RuntimeException("Could not load the file " + fileName);
        }
        finally
        {
            if (in != null){
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    Log.d("closing inputstream", "Shit");
                }
            }
        }
    }

    public Music loadMusic(String fileName)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(fileName);
            return new Music(assetFileDescriptor);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load music file: " + fileName + " BAD ERROR!");
        }
    }

    public Sound loadSound(String fileName)
    {
        try
        {
            AssetFileDescriptor assetFileDescriptor = getAssets().openFd(fileName); // Fd = filedescripter
            int soundId = soundPool.load(assetFileDescriptor, 0);
            Sound sound = new Sound(soundPool, soundId);
            return sound;
        }
        catch (IOException e)
        {
            throw new RuntimeException("Could not load sound file: " + fileName + " BAD ERROR!");
        }
    }

    public void clearFramebuffer(int color)
    {
        if (canvas != null) canvas.drawColor(color);
    }

    public int getFrameBufferWidth()
    {
        return surfaceView.getWidth();
    }

    public int getFramebufferHeight()
    {
        return surfaceView.getHeight();
    }

    public void drawBitmap(Bitmap bitmap, int x, int y)
    {
        if (canvas != null) canvas.drawBitmap(bitmap, x, y, null);
    }

    Rect src = new Rect();
    Rect dst = new Rect();


    /**
     * Draw parts of a bitmap
     *
     * @param bitmap    object to be drawn
     * @param x         coordinate on screen
     * @param y
     * @param srcX      coordinate on picture (bitmap)
     * @param srcY
     * @param srcWidth  size of part of the picture
     * @param srcHeight
     */
    public void drawBitmap(Bitmap bitmap, int x, int y, int srcX, int srcY, int srcWidth, int srcHeight)
    {
        if (canvas == null) return;
        src.left = srcX;
        src.top = srcY;
        src.right = srcX + srcWidth;
        src.bottom = srcY + srcHeight;

        dst.left = x;
        dst.top = y;
        dst.right = x + srcWidth;
        dst.bottom = y + srcHeight;

        canvas.drawBitmap(bitmap, src, dst, null);
    }

    //@Override
    public boolean onKey(View v, int keyCode, KeyEvent event)
    {
        if(event.getAction() == KeyEvent.ACTION_DOWN)
        {
            pressedKeys[keyCode] = true;
        }
        else if(event.getAction() == KeyEvent.ACTION_UP)
        {
            pressedKeys[keyCode] = false;
        }

        return false;
    }

    public boolean isKeyPressed(int keyCode)
    {
        return pressedKeys[keyCode];
    }

    public boolean isTouchDown(int pointer)
    {
        return touchHandler.isTouchDown(pointer);
    }

    public int getTouchX(int pointer)
    {
        float ratioX = (float)offscreenSurface.getWidth() / (float)surfaceView.getWidth();
        int x = touchHandler.getTouchX(pointer);
        x = (int) (x * ratioX);
        return x;
    }

    public int getTouchY(int pointer)
    {
        float ratioY = (float)offscreenSurface.getHeight() / (float)surfaceView.getHeight();
        int y = touchHandler.getTouchY(pointer);
        y = (int) (y * ratioY);
        return y;
    }

    private void fillEvents()
    {
        synchronized (keyEventBuffer)
        {
            int stop = keyEventBuffer.size();
            for (int i = 0; i < stop; i++)
            {
                keyEvents.add(keyEventBuffer.get(i));
            }
            keyEventBuffer.clear();
        }
        synchronized (touchEventBuffer)
        {
            int stop = touchEventBuffer.size();
            for (int i = 0; i < stop; i++)
            {
                touchEvents.add(touchEventBuffer.get(i));
            }
            touchEventBuffer.clear();
        }
    }

    private void freeEvents()
    {
        synchronized (keyEvents)
        {
            int stop = keyEvents.size();
            for (int i = 0; i < stop; i++)
            {
                keyEventPool.free(keyEvents.get(i));
            }
            keyEvents.clear();
        }
        synchronized (touchEvents)
        {
            int stop = touchEvents.size();
            for (int i = 0; i < stop; i++)
            {
                touchEventPool.free(touchEvents.get(i));
            }
            touchEvents.clear();
        }
    }

    public List<MyKeyEvent> getKeyEvents()
    {
        return keyEvents;
    }
    public List<TouchEvent> getTouchEvents()
    {
        return touchEvents;
    }

    public float[] getAccelerometer()
    {
        return accelerometer;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy){}

    public void onSensorChanged(SensorEvent event)
    {
        System.arraycopy(event.values, 0, accelerometer, 0, 3);
    }

    //This is the main method for the game loop
    public void run()
    {
        int frames = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (goOn)
        {
            synchronized (statesChanges)
            {
                for (int i = 0; i < statesChanges.size(); i++)
                {
                    state = statesChanges.get(i);
                    if (state == State.Disposed)
                    {
                        if(screen != null) screen.dispose();
                        Log.d("game", "State is Disposed");
                    }
                    else if (state == State.Paused)
                    {
                        if(screen != null) screen.pause();
                        Log.d("game", "State is Paused");
                    }
                    else if (state == State.Resumed)
                    {
                        if(screen != null) screen.resume();
                        state = State.Running;
                        Log.d("game", "State is Running");
                    }
                }
                statesChanges.clear();
            }
            if (state == State.Running)
            {
                if (!surfaceHolder.getSurface().isValid()) continue;
                if (surfaceView.getWidth() > surfaceView.getHeight())
                {
                    setOffscreenSurface(480, 320);
                }
                else
                {
                    setOffscreenSurface(320, 480);
                }
                Canvas physicalCanvas = surfaceHolder.lockCanvas();
                // here we should do some drawing on the screen
                // canvas.drawColor(Color.YELLOW); just a screen test

                fillEvents();
                currentTime = System.nanoTime();
                if (screen != null)
                {
                    screen.update((currentTime - lastTime)/1000000000.0f);
                }
                lastTime = currentTime;
                freeEvents();

                src.left = 0;
                src.top = 0;
                src.right = offscreenSurface.getWidth() - 1;
                src.bottom = offscreenSurface.getHeight() - 1;
                dst.left = 0;
                dst.top = 0;
                dst.right = surfaceView.getWidth() - 1;
                dst.bottom = surfaceView.getHeight() - 1;

                physicalCanvas.drawBitmap(offscreenSurface, src, dst, null);

                surfaceHolder.unlockCanvasAndPost(physicalCanvas);
            }
            frames++;
            if (System.nanoTime() - lastTime > 1000000000)
            {
                framesPerSecond = frames;
                frames = 0;
                lastTime = System.nanoTime();
            }
        }
    }

    public void onPause()
    {
        super.onPause();
        synchronized (statesChanges)
        {
            if (isFinishing())
            {
                statesChanges.add(statesChanges.size(), State.Disposed);
                ((SensorManager)getSystemService(Context.SENSOR_SERVICE)).unregisterListener(this);
            }
            else
            {
                statesChanges.add(statesChanges.size(), State.Paused);
            }
        }
        try
        {
            goOn = false;
            mainLoopThread.join();
        }
        catch (InterruptedException e)
        {
            System.err.println(e);
        }
        if (isFinishing())
        {
            soundPool.release();
        }
    }

    public void onResume()
    {
        super.onResume();
        goOn = true;
        mainLoopThread = new Thread(this);
        mainLoopThread.start();
        synchronized (statesChanges)
        {
            statesChanges.add(statesChanges.size(), State.Resumed);
        }
    }

    public void onRestart()
    {
        super.onRestart();
    }
    public void onStart()
    {
        super.onStart();

    }
    public void onStop()
    {
        super.onStop();
    }

    public void endTouch()
    {
        touchHandler.setTouchX(0, 0);
        touchHandler.setTouchY(0, 0);
    }
}