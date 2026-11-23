package Bomberman.strategy;

import Bomberman.domain.Bomb;
import Bomberman.domain.Map;
import Bomberman.domain.NPC;
import Bomberman.domain.Player;
import java.awt.Color;
import java.util.List;

public class EvadeStrategy implements MovementStrategy {
    @Override
    public void calculateMove(NPC npc, Map map, Player player, List<Bomb> bombs) {
        int myTileX = npc.getTileX();
        int myTileY = npc.getTileY();

        int[] dx = {0, 0, -1, 1};
        int[] dy = {-1, 1, 0, 0};

        int bestDirX = 0;
        int bestDirY = 0;
        double maxSafetyScore = -999;

        Bomb nearestBomb = bombs.isEmpty() ? null : bombs.get(0);

        for (int i = 0; i < 4; i++) {
            int targetX = myTileX + dx[i];
            int targetY = myTileY + dy[i];

            if (!map.isSolid(targetX, targetY)) {
                boolean isDangerous = npc.isTileDangerous(targetX, targetY, bombs, map);
                double score = isDangerous ? 0 : 100;

                if (nearestBomb != null) {
                    score += Math.hypot(targetX - nearestBomb.getX(), targetY - nearestBomb.getY()) * 10;
                }

                if (score > maxSafetyScore) {
                    maxSafetyScore = score;
                    bestDirX = dx[i];
                    bestDirY = dy[i];
                }
            }
        }

        if (bestDirX != 0 || bestDirY != 0) {
            npc.setDirection(bestDirX, bestDirY);
        } else {
            npc.setDirection(-npc.getDirX(), -npc.getDirY());
        }
    }

    @Override
    public Color getIndicatorColor() {
        return Color.MAGENTA;
    }
}