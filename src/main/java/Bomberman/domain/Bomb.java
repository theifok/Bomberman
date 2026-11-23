package Bomberman.domain;

import Bomberman.ui.GamePanel;
import java.awt.Color;
import java.awt.Graphics;

public class Bomb {
    private final int x, y;
    private final long plantTime;
    private final long fuseTime;
    private boolean exploded = false;
    private final int radius;

    public Bomb(int tileX, int tileY, long fuseTimeInMillis, int radius) {
        this.x = tileX;
        this.y = tileY;
        this.fuseTime = fuseTimeInMillis;
        this.radius = radius;
        this.plantTime = System.currentTimeMillis();
    }

    public void update() {
        if (System.currentTimeMillis() - plantTime >= fuseTime) exploded = true;
    }

    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillOval(x * GamePanel.TILE_SIZE + 6, y * GamePanel.TILE_SIZE + 6, GamePanel.TILE_SIZE - 12, GamePanel.TILE_SIZE - 12);
    }

    public boolean isExploded() { return exploded; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getRadius() { return radius; }
}