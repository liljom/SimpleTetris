package dk.homoki.roland.gameengine.Tetris;

import android.graphics.Bitmap;

import dk.homoki.roland.gameengine.Game;

public class WorldRenderer
{
    Game game;
    World world;
    Bitmap I;
    Bitmap O;
    Bitmap T;
    Bitmap L;
    Bitmap J;
    Bitmap S;
    Bitmap Z;

    public WorldRenderer(Game game, World world)
    {
        this.game = game;
        this.world = world;
        this.I = game.loadBitMap("I.png");
        this.O = game.loadBitMap("O.png");
        this.T = game.loadBitMap("T.png");
        this.L = game.loadBitMap("L.png");
        this.J = game.loadBitMap("J.png");
        this.S = game.loadBitMap("S.png");
        this.Z = game.loadBitMap("Z.png");
    }

    public void render()
    {
        for (Block b : world.blocks) {
            if (b.y >= 1) game.drawBitmap(getPic(b.color), 40 + b.x * 20, 20 + b.y * 20);
        }
    }

    private Bitmap getPic(Block.Color color)
    {       // {CYAN, YELLOW, PURPLE, ORANGE, BLUE, GREEN, RED}
        if (color.equals(Block.Color.CYAN)) return I;
        else if (color.equals(Block.Color.YELLOW)) return O;
        else if (color.equals(Block.Color.PURPLE)) return T;
        else if (color.equals(Block.Color.ORANGE)) return L;
        else if (color.equals(Block.Color.BLUE)) return J;
        else if (color.equals(Block.Color.GREEN)) return S;
        else return Z;
    }
}
