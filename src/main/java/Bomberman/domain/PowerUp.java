package Bomberman.domain;

import Bomberman.ui.GamePanel;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;

public class PowerUp {
    public enum Type { SPEED, BOMB_UP, RADIUS_UP }

    private final int tileX, tileY;
    private final Type type;
    private boolean active = true;

    public PowerUp(int tileX, int tileY, Type type) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;
    }

    public void draw(Graphics g) {
        if (!active) return;
        int px = tileX * GamePanel.TILE_SIZE;
        int py = tileY * GamePanel.TILE_SIZE;

        switch (type) {
            case SPEED -> g.setColor(Color.YELLOW);
            case BOMB_UP -> g.setColor(Color.RED);
            case RADIUS_UP -> g.setColor(Color.ORANGE);
        }

        g.fillOval(px + 10, py + 10, 20, 20);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        String letter = switch (type) {
            case SPEED -> "S";
            case BOMB_UP -> "B";
            case RADIUS_UP -> "R";
        };
        g.drawString(letter, px + 17, py + 25);
    }

    public Rectangle getBounds() {
        return new Rectangle(tileX * GamePanel.TILE_SIZE + 5, tileY * GamePanel.TILE_SIZE + 5, 30, 30);
    }

    public Type getType() { return type; }
    public boolean isActive() { return active; }
    public void collect() { this.active = false; }
}