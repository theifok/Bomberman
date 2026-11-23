package Bomberman.persistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreEntry implements Comparable<ScoreEntry> {
    private final String playerName;
    private final int score;
    private final String date;

    public ScoreEntry(String playerName, int score) {
        if (playerName == null || playerName.trim().isEmpty()) {
            throw new IllegalArgumentException("A játékos neve nem lehet üres!");
        }
        if (score < 0) {
            throw new IllegalArgumentException("A pontszám nem lehet negatív!");
        }
        this.playerName = playerName.trim();
        this.score = score;
        this.date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public ScoreEntry(String playerName, int score, String date) {
        this.playerName = playerName;
        this.score = score;
        this.date = date;
    }

    public String getPlayerName() { return playerName; }
    public int getScore() { return score; }
    public String getDate() { return date; }

    @Override
    public int compareTo(ScoreEntry other) {
        return Integer.compare(other.score, this.score);
    }

    @Override
    public String toString() {
        return String.format("%-15s | %6d pont | %s", playerName, score, date);
    }
}