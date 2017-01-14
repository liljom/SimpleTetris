package dk.homoki.roland.gameengine.Tetris;

import dk.homoki.roland.gameengine.Game;
import dk.homoki.roland.gameengine.Screen;

public class Tetris extends Game
{
    @Override
    public Screen createStartScreen()
    {
        return new MainMenuScreen(this);
    }

    public void onPause()
    {
        super.onPause();
    }

    public void onResume()
    {
        super.onResume();
    }
}
