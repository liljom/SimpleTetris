package dk.homoki.roland.gameengine;

public class TouchEventPool extends Pool<TouchEvent>
{
    protected TouchEvent newItem()
    {
        return new TouchEvent();
    }
}
