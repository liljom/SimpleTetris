package dk.homoki.roland.gameengine;

public interface TouchHandler
{
    public boolean isTouchDown(int pointer);
    public int getTouchX(int pointer);
    public int getTouchY(int pointer);
    public void setTouchX(int pointer, int x);
    public void setTouchY(int pointer, int Y);
}
