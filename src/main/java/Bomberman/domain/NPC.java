package Bomberman.domain;

import Bomberman.strategy.ChaseStrategy;
import Bomberman.strategy.EvadeStrategy;
import Bomberman.strategy.MovementStrategy;
import Bomberman.strategy.PatrolStrategy;
import Bomberman.ui.GamePanel;
import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

public class NPC extends Entity {
    private MovementStrategy strategy;
    private int dirX = 0;
    private int dirY = 0;
    private static final int CHASE_RADIUS = 6;

    public NPC(float x, float y) {
        super(x, y, 24, 24, 1.5f);
        this.strategy = new PatrolStrategy();
    }

    public void setStrategy(MovementStrategy strategy) {
        this.strategy = strategy;
    }

    @Override
    public void update(GamePanel game) {
        if (!alive) return;

        Player player = game.getPlayer();
        List<Bomb> bombs = game.getBombs();
        Map map = game.getMap();

        if (isInDanger(map, bombs)) {
            if (!(strategy instanceof EvadeStrategy)) setStrategy(new EvadeStrategy());
        } else if (player != null && player.isAlive() && canSeePlayer(map, player)) {
            if (!(strategy instanceof ChaseStrategy)) setStrategy(new ChaseStrategy());
        } else {
            if (!(strategy instanceof PatrolStrategy)) setStrategy(new PatrolStrategy());
        }

        strategy.calculateMove(this, map, player, bombs);
        moveWithCornerSliding(map);
    }

    private boolean isInDanger(Map map, List<Bomb> bombs) {
        return isTileDangerous(getTileX(), getTileY(), bombs, map);
    }

    public boolean isTileDangerous(int tileX, int tileY, List<Bomb> bombs, Map map) {
        for (Bomb bomb : bombs) {
            int bx = bomb.getX();
            int by = bomb.getY();
            int radius = bomb.getRadius();

            if (tileX == bx && Math.abs(tileY - by) <= radius && hasClearPath(map, bx, by, tileX, tileY)) return true;
            if (tileY == by && Math.abs(tileX - bx) <= radius && hasClearPath(map, bx, by, tileX, tileY)) return true;
        }
        return false;
    }

    private boolean canSeePlayer(Map map, Player player) {
        int myTileX = getTileX();
        int myTileY = getTileY();
        int playerTileX = player.getTileX();
        int playerTileY = player.getTileY();

        if (Math.abs(myTileX - playerTileX) + Math.abs(myTileY - playerTileY) > CHASE_RADIUS) return false;
        return hasClearPath(map, myTileX, myTileY, playerTileX, playerTileY);
    }

    private boolean hasClearPath(Map map, int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            for (int y = Math.min(y1, y2) + 1; y < Math.max(y1, y2); y++) {
                if (map.isSolid(x1, y)) return false;
            }
            return true;
        } else if (y1 == y2) {
            for (int x = Math.min(x1, x2) + 1; x < Math.max(x1, x2); x++) {
                if (map.isSolid(x, y1)) return false;
            }
            return true;
        }
        return false;
    }

    public void alignToTileCenterX() {
        float targetX = getTileX() * GamePanel.TILE_SIZE + (GamePanel.TILE_SIZE - width) / 2.0f;
        if (Math.abs(x - targetX) > 1.0f) {
            x += (x < targetX) ? Math.min(speed, targetX - x) : -Math.min(speed, x - targetX);
        } else {
            x = targetX;
        }
    }

    public void alignToTileCenterY() {
        float targetY = getTileY() * GamePanel.TILE_SIZE + (GamePanel.TILE_SIZE - height) / 2.0f;
        if (Math.abs(y - targetY) > 1.0f) {
            y += (y < targetY) ? Math.min(speed, targetY - y) : -Math.min(speed, y - targetY);
        } else {
            y = targetY;
        }
    }

    private void moveWithCornerSliding(Map map) {
        float nextX = x + dirX * speed;
        float nextY = y + dirY * speed;
        boolean moved = false;

        if (dirX != 0) {
            if (!map.checkCollision(nextX, y, width, height)) {
                x = nextX;
                moved = true;
            } else if (slideY(map)) {
                moved = true;
            }
        }

        if (dirY != 0) {
            if (!map.checkCollision(x, nextY, width, height)) {
                y = nextY;
                moved = true;
            } else if (slideX(map)) {
                moved = true;
            }
        }

        if (!moved) {
            dirX = 0;
            dirY = 0;
        }
    }

    private boolean slideX(Map map) {
        float shift = 2.0f;
        if (!map.checkCollision(x + shift, y + dirY * speed, width, height)) { x += shift; return true; }
        if (!map.checkCollision(x - shift, y + dirY * speed, width, height)) { x -= shift; return true; }
        return false;
    }

    private boolean slideY(Map map) {
        float shift = 2.0f;
        if (!map.checkCollision(x + dirX * speed, y + shift, width, height)) { y += shift; return true; }
        if (!map.checkCollision(x + dirX * speed, y - shift, width, height)) { y -= shift; return true; }
        return false;
    }

    public void setDirection(int dirX, int dirY) {
        this.dirX = dirX;
        this.dirY = dirY;
    }

    public int getDirX() { return dirX; }
    public int getDirY() { return dirY; }

    @Override
    public void draw(Graphics g) {
        if (!alive) return;
        g.setColor(strategy.getIndicatorColor());
        g.fillRect((int) x + 4, (int) y + 4, width, height);
        g.setColor(Color.BLACK);
        g.drawRect((int) x + 4, (int) y + 4, width, height);
    }
}