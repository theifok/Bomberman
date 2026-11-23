package Bomberman;

import Bomberman.domain.Map;
import Bomberman.exception.InvalidMapFormatException;
import Bomberman.persistence.ScoreEntry;
import Bomberman.persistence.ScoreManager;
import Bomberman.ui.Window;

import javax.swing.SwingUtilities;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ScoreManager scoreManager = new ScoreManager("scores.txt");
        Scanner scanner = new Scanner(System.in);
        String playerName = "Jatekos1";
        Map currentMap = new Map();

        System.out.println("=== BOMBERMAN ===");

        boolean running = true;
        while (running) {
            System.out.println("\n--- FŐMENÜ (Aktuális játékos: " + playerName + ") ---");
            System.out.println("1. Játékos név megadása");
            System.out.println("2. Pálya betöltése fájlból");
            System.out.println("3. Játék indítása (GUI)");
            System.out.println("4. Toplista megtekintése (Rendezve)");
            System.out.println("5. Kilépés");
            System.out.print("Válassz egy menüpontot: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> {
                    System.out.print("Add meg a neved: ");
                    String inputName = scanner.nextLine().trim();
                    if (!inputName.isEmpty()) {
                        playerName = inputName;
                        System.out.println("Név sikeresen frissítve!");
                    } else {
                        System.out.println("Hibás név, maradt az előző.");
                    }
                }
                case "2" -> {
                    System.out.print("Add meg az útvonalat: ");
                    String fileName = scanner.nextLine().trim();
                    try {
                        currentMap.loadMapFromFile(fileName);
                        System.out.println("Pálya sikeresen betöltve!");
                    } catch (InvalidMapFormatException e) {
                        System.err.println("HIBA a pálya betöltésekor: " + e.getMessage());
                        System.out.println("Az alapértelmezett pálya marad aktív.");
                        currentMap.loadDefaultMap();
                    }
                }
                case "3" -> {
                    System.out.println("Grafikus felület indítása...");
                    final String finalName = playerName;
                    final Map finalMap = currentMap;
                    SwingUtilities.invokeLater(() -> {
                        Window window = new Window(finalName, finalMap, scoreManager);
                        window.startGame();
                    });
                }
                case "4" -> {
                    System.out.println("\n=== TOPLISTA ===");
                    List<ScoreEntry> scores = scoreManager.loadScores();
                    if (scores.isEmpty()) {
                        System.out.println("Még nincsenek mentett eredmények.");
                    } else {
                        for (int i = 0; i < scores.size(); i++) {
                            System.out.println((i + 1) + ". " + scores.get(i));
                        }
                    }
                }
                case "5" -> {
                    System.out.println("Kilépés...");
                    running = false;
                    System.exit(0);
                }
                default -> System.out.println("Érvénytelen választás!");
            }
        }
        scanner.close();
    }
}