package dk.homoki.roland.gameengine;

public class KeyEventPool extends Pool<MyKeyEvent>
{
    @Override
    protected MyKeyEvent newItem()
    {
        return new MyKeyEvent();
    }
}
