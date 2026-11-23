package Bomberman.domain;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

public class Explosion {
    private final List<Rectangle> tiles;
    private final long startTime;
    private static final long DURATION = 500;

    public Explosion(List<Rectangle> tiles) {
        this.tiles = tiles;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime >= DURATION;
    }

    public void checkCollision(Player player, List<NPC> npcs) {
        for (Rectangle tile : tiles) {
            if (player.isAlive() && player.getBounds().intersects(tile)) player.kill();
            for (NPC npc : npcs) {
                if (npc.isAlive() && npc.getBounds().intersects(tile)) {
                    npc.kill();
                    player.addScore(500);
                }
            }
        }
    }

    public void draw(Graphics g) {
        for (Rectangle tile : tiles) {
            g.setColor(new Color(255, 69, 0, 200));
            g.fillRect(tile.x, tile.y, tile.width, tile.height);
            g.setColor(Color.YELLOW);
            g.drawRect(tile.x + 2, tile.y + 2, tile.width - 4, tile.height - 4);
        }
    }
}