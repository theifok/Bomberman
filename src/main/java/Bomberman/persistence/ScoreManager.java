package Bomberman.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScoreManager {
    private final String filePath;

    public ScoreManager(String filePath) {
        this.filePath = filePath;
    }

    public void saveScore(ScoreEntry entry) {
        List<ScoreEntry> scores = loadScores();
        scores.add(entry);
        Collections.sort(scores);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (ScoreEntry s : scores) {
                writer.write(s.getPlayerName() + ";" + s.getScore() + ";" + s.getDate());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Hiba az eredmény mentésekor: " + e.getMessage());
        }
    }

    public List<ScoreEntry> loadScores() {
        List<ScoreEntry> scores = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return scores;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    scores.add(new ScoreEntry(parts[0], Integer.parseInt(parts[1]), parts[2]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Hiba az eredmények betöltésekor: " + e.getMessage());
        }

        Collections.sort(scores);
        return scores;
    }
}