package dk.homoki.roland.gameengine;

public class SimpleGame extends Game
{

    @Override
    public Screen createStartScreen()
    {
        return new SimpleScreen(this);
    }
}
