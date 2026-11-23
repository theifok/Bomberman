package Bomberman.domain;

import Bomberman.ui.GamePanel;
import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {
    protected float x, y;
    protected final int width, height;
    protected float speed;
    protected boolean alive = true;

    public Entity(float x, float y, int width, int height, float speed) {
        if (width <= 0 || height <= 0 || speed < 0) {
            throw new IllegalArgumentException("Érvénytelen entitás paraméterek!");
        }
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }

    public abstract void update(GamePanel game);
    public abstract void draw(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle((int) x, (int) y, width, height);
    }

    public int getTileX() {
        return (int) ((x + width / 2.0f) / GamePanel.TILE_SIZE);
    }

    public int getTileY() {
        return (int) ((y + height / 2.0f) / GamePanel.TILE_SIZE);
    }

    public void kill() { this.alive = false; }
    public boolean isAlive() { return alive; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getSpeed() { return speed; }
}