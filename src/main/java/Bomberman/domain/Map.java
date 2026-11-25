package Bomberman.domain;

import Bomberman.exception.InvalidMapFormatException;
import Bomberman.ui.GamePanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    public static final int EMPTY = 0;
    public static final int INDESTRUCTIBLE = 1;
    public static final int DESTRUCTIBLE = 2;

    private final int[][] grid;
    private final int rows = 15;
    private final int cols = 21;
    private final Random random = new Random();

    public Map() {
        this.grid = new int[rows][cols];
        loadDefaultMap();
    }

    public void loadMapFromFile(String filePath) throws InvalidMapFormatException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);

        if (!file.exists()) {
            throw new InvalidMapFormatException("A pályafájl nem található: " + filePath);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line.trim());
                }
            }
        } catch (IOException e) {
            throw new InvalidMapFormatException("I/O hiba a pálya olvasásakor: " + e.getMessage());
        }

        if (lines.size() != rows) {
            throw new InvalidMapFormatException("Hibás sorszám a pályafájlban! Elvárt: " + rows + ", kapott: " + lines.size());
        }

        for (int r = 0; r < rows; r++) {
            String l = lines.get(r);
            if (l.length() != cols) {
                throw new InvalidMapFormatException("Hibás oszlopszám a(z) " + (r + 1) + ". sorban!");
            }
            for (int c = 0; c < cols; c++) {
                char ch = l.charAt(c);
                if (ch == '1') grid[r][c] = INDESTRUCTIBLE;
                else if (ch == '2') grid[r][c] = DESTRUCTIBLE;
                else if (ch == '0') grid[r][c] = EMPTY;
                else throw new InvalidMapFormatException("Érvénytelen karakter: '" + ch + "'");
            }
        }
    }

    public void loadDefaultMap() {
        int[][] defaultGrid = {
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
            {1,0,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,0,1},
            {1,0,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,0,1},
            {1,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,1},
            {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
            {1,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,1},
            {1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1},
            {1,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,1},
            {1,0,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,2,1,0,1},
            {1,0,0,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,0,0,1},
            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
        };
        for (int r = 0; r < rows; r++) {
            System.arraycopy(defaultGrid[r], 0, grid[r], 0, cols);
        }
    }

    public void draw(Graphics g) {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * GamePanel.TILE_SIZE;
                int y = r * GamePanel.TILE_SIZE;

                if (grid[r][c] == INDESTRUCTIBLE) {
                    g.setColor(Color.DARK_GRAY);
                    g.fillRect(x, y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                } else if (grid[r][c] == DESTRUCTIBLE) {
                    g.setColor(new Color(139, 69, 19));
                    g.fillRect(x, y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                } else {
                    g.setColor(Color.GRAY);
                    g.fillRect(x, y, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE);
                }
            }
        }
    }

    public boolean checkCollision(float newX, float newY, int width, int height) {
        int leftTile = (int) (newX / GamePanel.TILE_SIZE);
        int rightTile = (int) ((newX + width - 1) / GamePanel.TILE_SIZE);
        int topTile = (int) (newY / GamePanel.TILE_SIZE);
        int bottomTile = (int) ((newY + height - 1) / GamePanel.TILE_SIZE);

        for (int r = topTile; r <= bottomTile; r++) {
            for (int c = leftTile; c <= rightTile; c++) {
                if (isSolid(c, r)) return true;
            }
        }
        return false;
    }

    public boolean isSolid(int tileX, int tileY) {
        if (tileY < 0 || tileY >= rows || tileX < 0 || tileX >= cols) return true;
        return grid[tileY][tileX] != EMPTY;
    }

    public Explosion createExplosion(int bombTileX, int bombTileY, int radius, List<PowerUp> powerUps, Player player) {
        List<Rectangle> affectedTiles = new ArrayList<>();
        int[] dx = {0, 0, 1, -1};
        int[] dy = {1, -1, 0, 0};

        affectedTiles.add(new Rectangle(bombTileX * GamePanel.TILE_SIZE, bombTileY * GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE));

        for (int i = 0; i < 4; i++) {
            for (int r = 1; r <= radius; r++) {
                int nx = bombTileX + dx[i] * r;
                int ny = bombTileY + dy[i] * r;

                if (ny < 0 || ny >= rows || nx < 0 || nx >= cols) break;
                if (grid[ny][nx] == INDESTRUCTIBLE) break;

                affectedTiles.add(new Rectangle(nx * GamePanel.TILE_SIZE, ny * GamePanel.TILE_SIZE, GamePanel.TILE_SIZE, GamePanel.TILE_SIZE));

                if (grid[ny][nx] == DESTRUCTIBLE) {
                    grid[ny][nx] = EMPTY;
                    player.addScore(100);
                    spawnPowerUp(nx, ny, powerUps);
                    break;
                }
            }
        }
        return new Explosion(affectedTiles);
    }

    private void spawnPowerUp(int tileX, int tileY, List<PowerUp> powerUps) {
        int chance = random.nextInt(100);
        if (chance < 25) powerUps.add(new PowerUp(tileX, tileY, PowerUp.Type.BOMB_UP));
        else if (chance < 50) powerUps.add(new PowerUp(tileX, tileY, PowerUp.Type.RADIUS_UP));
        else if (chance < 60) powerUps.add(new PowerUp(tileX, tileY, PowerUp.Type.SPEED));
    }

    public int getDestructibleBlocksCount() {
        int count = 0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] == DESTRUCTIBLE) count++;
            }
        }
        return count;
    }
}