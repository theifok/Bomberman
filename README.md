Klasszikus Bomberman játék Java-ban, konzolos menüvel és Swing grafikus felülettel.

Indítás
A projekt főmappájából futtasd a következő parancsot:
mvn clean compile exec:java

A megjelenő konzolos menüben megadhatod a neved, betölthetsz egyedi pályát pl. res/map1.txt, 
elindíthatod a játékot, vagy megnézheted a toplistát.

Mozgás: W, A, S, D
Bomba lerakása: SPACE
Újrakezdés: R (Game Over vagy Győzelem után)

Cél: Robbantsd fel az összes ideiglenes (barna) falat, és győzd le az összes ellenfelet a pályán! 
A felrobbantott falak alól néha erősítsek (gyorsaság, több bomba, nagyobb robbanási sugár) esnek ki, amik segítenek a túlélésben.

Design Pattern: Strategy
A játékban az ellenfelek viselkedése futásidőben folyamatosan változik a helyzettől függően.
Előnye: Ahelyett, hogy az NPC osztályban egy hatalmas if-else blokk kezelné az összes mozgást, minden viselkedés külön osztályba került.
Az NPC játék közben dinamikusan váltogatja ezeket és később új viselkedés is könnyen hozzáadható a meglévő kód átírása nélkül.
