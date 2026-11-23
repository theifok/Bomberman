package Bomberman.strategy;

import Bomberman.domain.Bomb;
import Bomberman.domain.Map;
import Bomberman.domain.NPC;
import Bomberman.domain.Player;
import java.awt.Color;
import java.util.List;

public class ChaseStrategy implements MovementStrategy {
    @Override
    public void calculateMove(NPC npc, Map map, Player player, List<Bomb> bombs) {
        if (player == null || !player.isAlive()) return;

        int diffX = player.getTileX() - npc.getTileX();
        int diffY = player.getTileY() - npc.getTileY();

        if (diffX != 0) {
            npc.setDirection(diffX > 0 ? 1 : -1, 0);
            npc.alignToTileCenterY();
        } else if (diffY != 0) {
            npc.setDirection(0, diffY > 0 ? 1 : -1);
            npc.alignToTileCenterX();
        }
    }

    @Override
    public Color getIndicatorColor() {
        return Color.RED;
    }
}