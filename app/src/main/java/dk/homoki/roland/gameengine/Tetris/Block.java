package dk.homoki.roland.gameengine.Tetris;

public class Block
{
    public Color color;
    int x;
    int y;

    public enum Color {CYAN, YELLOW, PURPLE, ORANGE, BLUE, GREEN, RED}

    public Block(int x, int y, Color color)
    {
        this.x = x;
        this.y = y;
        this.color = color;
    }
}
