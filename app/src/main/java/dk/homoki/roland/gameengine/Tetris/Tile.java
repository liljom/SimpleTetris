package dk.homoki.roland.gameengine.Tetris;

import java.util.Random;

public class Tile {

    Block[] blocks;
    Shape shape;
    private static int defaultX = 5;
    private static int defaultY = 2;

    public enum Shape {I, O, T, L, J, S, Z}

    public Tile() {
        setBlocks();
    }

    private void setBlocks() {
        blocks = new Block[4];
        Shape[] shapes = Shape.values();
        Random rand = new Random();
        int r = rand.nextInt(7);
        shape = Shape.values()[r];
        switch (shapes[r])
        {
            case I:
            {
                blocks[0] = new Block(defaultX, defaultY, Block.Color.CYAN);
                blocks[1] = new Block(defaultX, defaultY + 2, Block.Color.CYAN);
                blocks[2] = new Block(defaultX, defaultY + 1, Block.Color.CYAN);
                blocks[3] = new Block(defaultX, defaultY - 1, Block.Color.CYAN);
                break;
            }
            case O:
            {
                blocks[0] = new Block(defaultX,     defaultY, Block.Color.YELLOW);
                blocks[1] = new Block(defaultX + 1, defaultY, Block.Color.YELLOW);
                blocks[2] = new Block(defaultX,     defaultY - 1, Block.Color.YELLOW);
                blocks[3] = new Block(defaultX + 1, defaultY - 1, Block.Color.YELLOW);
                break;
            }
            case T:
            {
                blocks[0] = new Block(defaultX,     defaultY, Block.Color.PURPLE);
                blocks[1] = new Block(defaultX - 1, defaultY, Block.Color.PURPLE);
                blocks[2] = new Block(defaultX + 1, defaultY, Block.Color.PURPLE);
                blocks[3] = new Block(defaultX,     defaultY - 1, Block.Color.PURPLE);
                break;
            }
            case L:
            {
                blocks[0] = new Block(defaultX,     defaultY, Block.Color.ORANGE);
                blocks[1] = new Block(defaultX - 1, defaultY, Block.Color.ORANGE);
                blocks[2] = new Block(defaultX - 1, defaultY - 1, Block.Color.ORANGE);
                blocks[3] = new Block(defaultX + 1, defaultY, Block.Color.ORANGE);
                break;
            }
            case J:
            {
                blocks[0] = new Block(defaultX,     defaultY, Block.Color.BLUE);
                blocks[1] = new Block(defaultX,     defaultY + 1, Block.Color.BLUE);
                blocks[2] = new Block(defaultX,     defaultY - 1, Block.Color.BLUE);
                blocks[3] = new Block(defaultX - 1, defaultY - 1, Block.Color.BLUE);
                break;
            }
            case S:
            {
                blocks[0] = new Block(defaultX,     defaultY, Block.Color.GREEN);
                blocks[1] = new Block(defaultX + 1, defaultY, Block.Color.GREEN);
                blocks[2] = new Block(defaultX - 1, defaultY - 1, Block.Color.GREEN);
                blocks[3] = new Block(defaultX,     defaultY - 1, Block.Color.GREEN);
                break;
            }
            case Z:
            {
                blocks[0] = new Block(defaultX,     defaultY, Block.Color.RED);
                blocks[1] = new Block(defaultX - 1, defaultY, Block.Color.RED);
                blocks[2] = new Block(defaultX,     defaultY - 1, Block.Color.RED);
                blocks[3] = new Block(defaultX + 1, defaultY - 1, Block.Color.RED);
                break;
            }
            default: break;
        }
        System.out.println("created a " + blocks[0].color + " " + shape
                + " at " + blocks[0].x + " " + blocks[0].y);
    }
}
