package Bomberman.ui;

import Bomberman.domain.Map;
import Bomberman.persistence.ScoreManager;
import javax.swing.JFrame;

public class Window extends JFrame {
    private final GamePanel gamePanel;

    public Window(String playerName, Map map, ScoreManager scoreManager) {
        setTitle("Bomberman - " + playerName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        gamePanel = new GamePanel(playerName, map, scoreManager);
        add(gamePanel);
        pack();
        setLocationRelativeTo(null);
    }

    public void startGame() {
        setVisible(true);
        gamePanel.start();
    }
}