package Bomberman.domain;

import Bomberman.ui.GamePanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

public class Player extends Entity {
    private final String name;
    private final GamePanel game;
    private boolean up, down, left, right;

    private int maxBombs = 1;
    private int bombRadius = 1;
    private int lives = 3;
    private int score = 0;

    private boolean invulnerable = false;
    private long invulnerableStartTime = 0;
    private static final long INVULNERABLE_DURATION = 1500;

    public Player(String name, float x, float y, GamePanel game) {
        super(x, y, 26, 26, 3.0f);
        this.name = (name == null || name.isBlank()) ? "Player1" : name;
        this.game = game;
    }

    @Override
    public void update(GamePanel g) {
        if (!alive) return;

        if (invulnerable && System.currentTimeMillis() - invulnerableStartTime >= INVULNERABLE_DURATION) {
            invulnerable = false;
        }

        float dx = 0;
        float dy = 0;

        if (up) dy -= speed;
        if (down) dy += speed;
        if (left) dx -= speed;
        if (right) dx += speed;

        if (dx != 0) {
            if (!game.getMap().checkCollision(x + dx, y, width, height)) {
                x += dx;
            }
        }
        if (dy != 0) {
            if (!game.getMap().checkCollision(x, y + dy, width, height)) {
                y += dy;
            }
        }
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) up = true;
        if (key == KeyEvent.VK_S) down = true;
        if (key == KeyEvent.VK_A) left = true;
        if (key == KeyEvent.VK_D) right = true;
        if (key == KeyEvent.VK_SPACE) placeBomb();
    }

    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_W) up = false;
        if (key == KeyEvent.VK_S) down = false;
        if (key == KeyEvent.VK_A) left = false;
        if (key == KeyEvent.VK_D) right = false;
    }

    private void placeBomb() {
        int tileX = getTileX();
        int tileY = getTileY();

        long currentBombs = game.getBombs().stream().filter(b -> !b.isExploded()).count();
        if (currentBombs >= maxBombs) return;

        for (Bomb bomb : game.getBombs()) {
            if (bomb.getX() == tileX && bomb.getY() == tileY) return;
        }

        game.addBomb(new Bomb(tileX, tileY, 3000, bombRadius));
    }

    public void applyPowerUp(PowerUp.Type type) {
        addScore(200);
        switch (type) {
            case SPEED -> this.speed += 0.5f;
            case BOMB_UP -> this.maxBombs++;
            case FIRE_UP -> this.bombRadius++;
        }
    }

    @Override
    public void kill() {
        if (invulnerable) return;
        lives--;
        if (lives <= 0) {
            this.alive = false;
        } else {
            this.x = GamePanel.TILE_SIZE + 4;
            this.y = GamePanel.TILE_SIZE + 4;
            this.invulnerable = true;
            this.invulnerableStartTime = System.currentTimeMillis();
        }
    }

    @Override
    public void draw(Graphics g) {
        if (!alive) return;
        if (invulnerable && (System.currentTimeMillis() / 150) % 2 == 0) return;

        g.setColor(Color.BLUE);
        g.fillOval((int) x - 3, (int) y - 3 + GamePanel.HUD_HEIGHT, 32, 32);
    }

    public void addScore(int amount) { this.score += amount; }
    public String getName() { return name; }
    public int getLives() { return lives; }
    public int getScore() { return score; }
    public int getMaxBombs() { return maxBombs; }
    public int getBombRadius() { return bombRadius; }
}