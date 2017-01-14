package dk.homoki.roland.gameengine.Tetris;

import android.graphics.Bitmap;
import dk.homoki.roland.gameengine.Game;
import dk.homoki.roland.gameengine.Screen;

public class MainMenuScreen extends Screen
{
    Bitmap mainMenu, playButton;
    float passedTime = 0;
    long startTime = System.nanoTime();

    public MainMenuScreen(Game game)
    {
        super(game);
        mainMenu = game.loadBitMap("splash.jpg");
        playButton = game.loadBitMap("start.png");
    }

    @Override
    public void update(float deltaTime)
    {
        if (game.isTouchDown(0))
        {
            game.setScreen(new GameScreen(game));
            return;
        }
        passedTime = passedTime + deltaTime;
        game.drawBitmap(mainMenu, 0, 0);
        if ((passedTime - (int)passedTime) > 0.5f)
        {
            game.drawBitmap(playButton, 160 - playButton.getWidth()/2, 240);
        }
    }

    @Override
    public void pause()
    {

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