package Bomberman.strategy;

import Bomberman.domain.Bomb;
import Bomberman.domain.Map;
import Bomberman.domain.NPC;
import Bomberman.domain.Player;
import java.awt.Color;
import java.util.List;
import java.util.Random;

public class PatrolStrategy implements MovementStrategy {
    private final Random random = new Random();

    @Override
    public void calculateMove(NPC npc, Map map, Player player, List<Bomb> bombs) {
        if (random.nextInt(100) < 2 || (npc.getDirX() == 0 && npc.getDirY() == 0)) {
            int dir = random.nextInt(4);
            switch (dir) {
                case 0 -> npc.setDirection(0, -1);
                case 1 -> npc.setDirection(0, 1);
                case 2 -> npc.setDirection(-1, 0);
                case 3 -> npc.setDirection(1, 0);
            }
        }
    }

    @Override
    public Color getIndicatorColor() {
        return Color.ORANGE;
    }
}