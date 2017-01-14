package dk.homoki.roland.gameengine.Tetris;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.List;

import dk.homoki.roland.gameengine.CollisionListener;
import dk.homoki.roland.gameengine.Game;
import dk.homoki.roland.gameengine.Screen;
import dk.homoki.roland.gameengine.Sound;
import dk.homoki.roland.gameengine.TouchEvent;

public class GameScreen extends Screen
{
    enum State
    {
        Paused,
        Running,
        GameOver
    }
    State state = State.Running;

    Bitmap background, resume, gameOver;
    World world;
    WorldRenderer renderer;
    Typeface font;
    Sound bounceSound, blockSound;

    public GameScreen(Game game)
    {
        super(game);
        background = game.loadBitMap("background.png");
        resume = game.loadBitMap("resume.png");
        gameOver = game.loadBitMap("gameover.png");
        font = game.loadFont("font.ttf");
        bounceSound = game.loadSound("bounce.wav");
        blockSound = game.loadSound("blocksplosion.wav");
        world = new World(new CollisionListener()
        {
            public void gameOver(){blockSound.play(1);}
        });
        renderer = new WorldRenderer(game, world);
    }

    @Override
    public void update(float deltaTime)
    {
        if (world.gameOver)
        {
            state = State.GameOver;
        }

        if (state == State.Paused && game.getTouchEvents().size() > 0)
        {
            state = State.Running;
            resume();
        }
        if (state == State.GameOver)
        {
            List<TouchEvent> events = game.getTouchEvents();
            int stop = events.size();
            for (int i = 0; i < stop; i++)
            {
                if (events.get(i).type == TouchEvent.TouchEventType.Up)
                {
                    game.setScreen(new MainMenuScreen(game));
                    return;
                }
            }
            dispose();
        }
        // PAUSE
        if (state == State.Running && game.getTouchY(0) < 36 && game.getTouchX(0) > 320-36)
        {
            state = State.Paused;
            System.out.println("button PAUSE pressed");
            pause();
        }
        // MOVE LEFT
        if (state == State.Running && game.getTouchX(0) > 10 && game.getTouchX(0) < 50 &&
                game.getTouchY(0) > 280 && game.getTouchY(0) < 320)
        {
            //System.out.println("button LEFT pressed");
            world.moveTile("left");
            game.endTouch();
        }
        // MOVE RIGHT
        if (state == State.Running && game.getTouchX(0) > 270 && game.getTouchX(0) < 310 &&
                game.getTouchY(0) > 280 && game.getTouchY(0) < 320)
        {
            //System.out.println("button RIGHT pressed");
            world.moveTile("right");
            game.endTouch();
        }
        // ROTATE
        if (state == State.Running && game.getTouchX(0) > 270 && game.getTouchX(0) < 310 &&
                game.getTouchY(0) > 210 && game.getTouchY(0) < 250)
        {
            //System.out.println("button ROTATE pressed");
            world.rotate();
            game.endTouch();
        }
        // MOVE DOWN
        if (state == State.Running && game.getTouchX(0) > 10 && game.getTouchX(0) < 50 &&
                game.getTouchY(0) > 210 && game.getTouchY(0) < 250)
        {
            //System.out.println("button DOWN pressed");
            world.moveTile("down");
            world.moveTile("down");
            world.moveTile("down");
            game.endTouch();
        }

        game.drawBitmap(background, 0, 0);

        // RUNNING
        if (state == State.Running)
        {
            world.update(deltaTime);
        }
        game.drawText(font, "ENOCH", 20, 11, Color.GREEN, 11);
        //game.drawText(font, "Points: " + Integer.toString(world.points), 20, 11, Color.GREEN, 11);
        renderer.render();

        if (state == State.Paused)
        {
            game.drawBitmap(resume, 160 - resume.getWidth()/2, 240 - resume.getHeight()/2);
        }
        if (state == State.GameOver)
        {
            game.drawBitmap(gameOver, 160 - gameOver.getWidth()/2, 240 - gameOver.getHeight()/2);
        }
    }

    @Override
    public void pause()
    {
        if (state == State.Running)
        {
            state = State.Paused;
        }
    }

    @Override
    public void resume()
    {
    }

    @Override
    public void dispose()
    {
    }
}
