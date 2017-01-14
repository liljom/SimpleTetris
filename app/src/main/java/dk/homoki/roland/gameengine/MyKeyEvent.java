package dk.homoki.roland.gameengine;

public class MyKeyEvent
{
    public enum KeyEventType
    {
        Down,
        Up
    }
    public KeyEventType type;
    public int keyCode;
    public  char character;
}
