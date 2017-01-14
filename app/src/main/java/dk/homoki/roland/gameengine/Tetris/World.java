package dk.homoki.roland.gameengine.Tetris;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dk.homoki.roland.gameengine.CollisionListener;

public class World
{
    int points;
    float time = 0;
    float timer = 0;
    boolean gameOver = false;
    Tile tile = new Tile();
    int[][] field = new int[13][22];
    List<Block> blocks = new ArrayList<>();

    /*
    int[][] field is the container of the data of blocks
    field[x][y] / 10 = landed
    field[x][y] % 10 = color (1-7)
    0 = empty (no block at that position)
     */

    public World(CollisionListener collisionListener)
    {
        initField();
        getTile();
    }

    private void initField() {
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 22; j++) {
                if( i == 0 || i == 11 || j == 21) field[i][j] = 1;
            }
        }
    }

    public void update(float deltaTime)
    {
        time += deltaTime;
        timer += deltaTime;
        if (time > 1)
        {
            moveTile("down");
            time = 0;
        }
    }

    public void moveTile(String direction)
    {
        if (direction.equals("down") && timer > 0.02f)
        {
            if (!willLand())
            {
                for (Block b : tile.blocks)
                {
                    b.y++;
                    //System.out.println("moved " + b.x + ", " + b.y + direction);
                }
                timer = 0;
            }
            else
            {
                for (Block b : tile.blocks)
                {
                    field[b.x][b.y] = 1;
                    field[12][b.y]++;
                    List<Block> toDelete;
                    if (field[12][b.y] == 10) /*REMOVE LINE b.y*/
                    {
                        // remove blocks in line b.y
                        toDelete = new ArrayList<>();
                        for (Block bl : blocks)
                        {
                            if(bl.y == b.y)
                            {
                                toDelete.add(bl);
                            }
                        }
                        // TODO show removing line
                        blocks.removeAll(toDelete);
                        // move blocks above b.y 1 down
                        for (Block bl : blocks)
                        {
                            if(bl.y < b.y)
                            {
                                bl.y++; // PROBABLE BUG
                            }
                        }
                        // move 1s in field[][] above b.y 1 down
                        for (int i = b.y; i > 0; i--)
                        {
                            for (int j = 0; j < 13; j++)
                            {
                                field[j][i] = field[j][i-1];
                            }
                        }
                    }
                    for (int i = 1; i < 11; i++)
                    {
                        if (field[i][2] == 1)
                        {
                            gameOver = true;
                        }
                    }
                }
                // TODO mention 4 new tiles
                getTile();
            }
        }
        if (direction.equals("left") && !willCollide(-1) && timer > 0.13f)
        {
            for (Block b: tile.blocks)
            {
                b.x--;
                System.out.println("moved " + b.x + ", " + b.y + direction);
                timer = 0;
            }
        }
        if (direction.equals("right") && !willCollide(1) && timer > 0.13f)
        {
            for (Block b: tile.blocks)
            {
                b.x++;
                System.out.println("moved " + b.x + ", " + b.y + direction);
                timer = 0;
            }
        }
    }

    private boolean willCollide(int dx)
    {
        boolean willIt = false;
        for (Block b : tile.blocks)
        {
            if(field[b.x + dx][b.y] == 1)  willIt = true;
        }
        return willIt;
    }

    private boolean willLand()
    {
        boolean willIt = false;
        for (Block b : tile.blocks)
        {
            if(field[b.x][b.y + 1] == 1)  willIt = true;
        }
        return willIt;
    }

    // TODO SHOW ROTATE
    public void rotate()
    {
        Tile.Shape shape = tile.shape;
        if (!shape.equals(Tile.Shape.O) && !willCrash() && timer > 0.2f) {
            int tempX = tile.blocks[0].x;
            int tempY = tile.blocks[0].y;
            int temp;
            for (Block b : tile.blocks)
            {
                temp = b.x - tempX;
                b.x = b.y - tempY + tempX;
                b.y = -temp + tempY;
                System.out.println(b.x + ", " + b.y + " rotated");
            }
            timer = 0;
        }
    }

    private boolean willCrash()
    {
        boolean willIt = false;
        int tempX = tile.blocks[0].x;
        int tempY = tile.blocks[0].y;
        int temp;
        int x;
        int y;

        for (Block b : tile.blocks)
        {
            temp = b.x - tempX;
            x = b.y - tempY + tempX;
            y = -temp + tempY;
            if (field[x][y] == 1) willIt = true;
        }
        return willIt;
    }

    public void getTile()
    {
        tile = new Tile();
        blocks.addAll(Arrays.asList(tile.blocks));
        timer = 0;
    }
}