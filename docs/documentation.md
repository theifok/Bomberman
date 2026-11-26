A program egy klasszikus Bomberman játékot valósít meg, amely kombinálja a konzolos vezérlést (névbeállítás, pályabetöltés, toplista) a valós idejű 2D grafikus játékmenettel. 
A cél egy tiszta, objektumorientált architektúra felépítése, amely megőrzi a játékosok eredményeit és dinamikus mesterséges intelligenciát biztosít az ellenfeleknek.

UML Osztálydiagram

mermaid
classDiagram
    class Main {
        +main(String[] args) void
    }
    class GamePanel {
        -Map map
        -Player player
        -List~NPC~ npcs
        -List~Bomb~ bombs
        -ScoreManager scoreManager
        +start() void
        +resetGame() void
    }
    class Window {
        -GamePanel gamePanel
        +startGame() void
    }
    class Entity {
        <<abstract>>
        #float x
        #float y
        #int width
        #int height
        #float speed
        #boolean alive
        +update(GamePanel)* void
        +draw(Graphics)* void
    }
    class Player {
        -String name
        -int lives
        -int score
        +applyPowerUp(PowerUp.Type) void
    }
    class NPC {
        -MovementStrategy strategy
        +setStrategy(MovementStrategy) void
    }
    class MovementStrategy {
        <<interface>>
        +calculateMove(...) void
        +getIndicatorColor() Color
    }
    class PatrolStrategy
    class ChaseStrategy
    class EvadeStrategy
    class Map {
        -int[][] grid
        +loadMapFromFile(String) void
        +checkCollision(...) boolean
    }
    class ScoreManager {
        +saveScore(ScoreEntry) void
        +loadScores() List~ScoreEntry~
    }
    class ScoreEntry {
        -String playerName
        -int score
        +compareTo(ScoreEntry) int
    }
    class InvalidMapFormatException

    Main --> Map
    Main --> ScoreManager
    Main ..> Window
    Window *-- GamePanel
    GamePanel *-- Player
    GamePanel o-- "0..*" NPC
    GamePanel --> Map
    GamePanel --> ScoreManager
    Entity <|-- Player
    Entity <|-- NPC
    NPC o-- MovementStrategy
    MovementStrategy <|.. PatrolStrategy
    MovementStrategy <|.. ChaseStrategy
    MovementStrategy <|.. EvadeStrategy
    ScoreManager o-- "0..*" ScoreEntry
    Map ..> InvalidMapFormatException : throws
