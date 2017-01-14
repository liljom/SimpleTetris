package dk.homoki.roland.gameengine;

public class TouchEvent
{
    public enum TouchEventType
    {
        Down,
        Up,
        Dragged
    }
    public TouchEventType type;
    public int x;
    public int y;
    public int pointer;
}
