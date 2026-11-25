package Bomberman.ui;

import Bomberman.domain.*;
import Bomberman.persistence.ScoreEntry;
import Bomberman.persistence.ScoreManager;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener {
    public static final int TILE_SIZE = 40;
    public static final int COLS = 21;
    public static final int ROWS = 15;
    public static final int HUD_HEIGHT = 40;
    public static final int WIDTH = COLS * TILE_SIZE;
    public static final int HEIGHT = ROWS * TILE_SIZE + HUD_HEIGHT;

    private Timer timer;
    private final Map map;
    private final String playerName;
    private final ScoreManager scoreManager;

    private Player player;
    private List<NPC> npcs;
    private List<Bomb> bombs;
    private List<Explosion> explosions;
    private List<PowerUp> powerUps;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean scoreSaved = false;

    public GamePanel(String playerName, Map map, ScoreManager scoreManager) {
        this.playerName = playerName;
        this.map = map;
        this.scoreManager = scoreManager;

        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);

        initEntities();
        addKeyListener(new TAdapter());
    }

    private void initEntities() {
        player = new Player(playerName, TILE_SIZE + 4, TILE_SIZE + 4, this);
        npcs = new ArrayList<>();
        npcs.add(new NPC(19 * TILE_SIZE + 4, TILE_SIZE + 4));
        npcs.add(new NPC(TILE_SIZE + 4, 13 * TILE_SIZE + 4));
        npcs.add(new NPC(19 * TILE_SIZE + 4, 13 * TILE_SIZE + 4));

        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        powerUps = new ArrayList<>();
        gameOver = false;
        gameWon = false;
        scoreSaved = false;
    }

    public void start() {
        timer = new Timer(16, this);
        timer.start();
    }

    public void resetGame() {
        if (timer != null && timer.isRunning()) timer.stop();
        map.loadDefaultMap();
        initEntities();
        timer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !gameWon) {
            player.update(this);
            for (NPC npc : npcs) {
                npc.update(this);
                if (npc.isAlive() && player.isAlive() && player.getBounds().intersects(npc.getBounds())) {
                    player.kill();
                }
            }
            checkPowerUpPickup();
            updateBombs();
            updateExplosions();
            checkGameState();
        }
        repaint();
    }

    private void checkPowerUpPickup() {
        for (PowerUp pu : powerUps) {
            if (pu.isActive() && player.getBounds().intersects(pu.getBounds())) {
                player.applyPowerUp(pu.getType());
                pu.collect();
            }
        }
    }

    private void updateBombs() {
        List<Bomb> explodedBombs = new ArrayList<>();
        for (Bomb bomb : bombs) {
            bomb.update();
            if (bomb.isExploded()) {
                explodedBombs.add(bomb);
                explosions.add(map.createExplosion(bomb.getX(), bomb.getY(), bomb.getRadius(), powerUps, player));
            }
        }
        bombs.removeAll(explodedBombs);
    }

    private void updateExplosions() {
        List<Explosion> expired = new ArrayList<>();
        for (Explosion exp : explosions) {
            exp.checkCollision(player, npcs);
            if (exp.isExpired()) expired.add(exp);
        }
        explosions.removeAll(expired);
    }

    private void checkGameState() {
        if (!player.isAlive()) {
            gameOver = true;
            endGameAndSaveScore();
            return;
        }
        if (map.getDestructibleBlocksCount() == 0 && npcs.stream().noneMatch(NPC::isAlive)) {
            gameWon = true;
            endGameAndSaveScore();
        }
    }

    private void endGameAndSaveScore() {
        timer.stop();
        if (!scoreSaved) {
            scoreManager.saveScore(new ScoreEntry(player.getName(), player.getScore()));
            scoreSaved = true;
            System.out.println("\n[RENDSZER] Eredmény elmentve: " + player.getName() + " - " + player.getScore() + " pont.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawHUD(g);

        Graphics gGame = g.create();
        gGame.translate(0, HUD_HEIGHT);
        map.draw(gGame);
        for (PowerUp pu : powerUps) pu.draw(gGame);
        for (Bomb bomb : bombs) bomb.draw(gGame);
        for (Explosion exp : explosions) exp.draw(gGame);
        for (NPC npc : npcs) npc.draw(gGame);
        gGame.dispose();

        player.draw(g);

        if (gameOver || gameWon) {
            g.setColor(gameOver ? Color.RED : Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString(gameOver ? "GAME OVER" : "VICTORY!", WIDTH / 2 - 130, HEIGHT / 2);
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            g.setColor(Color.WHITE);
            g.drawString("Nyomd meg az [R] billentyűt az újrajátékhoz!", WIDTH / 2 - 200, HEIGHT / 2 + 50);
        }
    }

    private void drawHUD(Graphics g) {
        g.setColor(new Color(30, 30, 30));
        g.fillRect(0, 0, WIDTH, HUD_HEIGHT);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        g.drawString("PLAYER: " + player.getName(), 20, 25);
        g.setColor(Color.RED);
        for (int i = 0; i < player.getLives(); i++) g.drawString("❤", 180 + (i * 20), 25);

        long activeBombs = bombs.stream().filter(b -> !b.isExploded()).count();
        g.setColor(Color.CYAN);
        g.drawString("BOMBS: " + (player.getMaxBombs() - activeBombs) + "/" + player.getMaxBombs(), 280, 25);
        g.setColor(Color.ORANGE);
        g.drawString("RADIUS: " + player.getBombRadius(), 450, 25);
        g.setColor(Color.YELLOW);
        g.drawString("SCORE: " + player.getScore(), 620, 25);
    }

    public void addBomb(Bomb bomb) { bombs.add(bomb); }
    public Map getMap() { return map; }
    public List<Bomb> getBombs() { return bombs; }
    public Player getPlayer() { return player; }

    private class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (gameOver || gameWon) {
                if (e.getKeyCode() == KeyEvent.VK_R) resetGame();
            } else {
                player.keyPressed(e);
            }
        }
        @Override
        public void keyReleased(KeyEvent e) {
            if (!gameOver && !gameWon) player.keyReleased(e);
        }
    }
}