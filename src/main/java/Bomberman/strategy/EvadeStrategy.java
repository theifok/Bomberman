package Bomberman.strategy;

import Bomberman.domain.Bomb;
import Bomberman.domain.Map;
import Bomberman.domain.NPC;
import Bomberman.domain.Player;
import java.awt.Color;
import java.util.List;
import Bomberman.ui.GamePanel;

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

        Bomb nearestBomb = findNearestBomb(myTileX, myTileY, bombs);

        for (int i = 0; i < 4; i++) {
            int targetX = myTileX + dx[i];
            int targetY = myTileY + dy[i];

            if (!map.isSolid(targetX, targetY)) {
                boolean isDangerous = npc.isTileDangerous(targetX, targetY, bombs, map);
                double score = isDangerous ? 0 : 100;

                if (nearestBomb != null) {
                    score += Math.hypot(targetX - nearestBomb.getX(), targetY - nearestBomb.getY()) * 10;
                }

                // Kisebb bónusz a jelenlegi haladási irány megtartására, hogy ne rángatózzon két mező között
                if (dx[i] == npc.getDirX() && dy[i] == npc.getDirY()) {
                    score += 5;
                }

                if (score > maxSafetyScore) {
                    maxSafetyScore = score;
                    bestDirX = dx[i];
                    bestDirY = dy[i];
                }
            }
        }

        if (bestDirX != 0 || bestDirY != 0) {
            float centerX = myTileX * GamePanel.TILE_SIZE + (GamePanel.TILE_SIZE - npc.getWidth()) / 2.0f;
            float centerY = myTileY * GamePanel.TILE_SIZE + (GamePanel.TILE_SIZE - npc.getHeight()) / 2.0f;

            if (bestDirY != 0 && Math.abs(npc.getX() - centerX) > 4.0f) {
                npc.setDirection(npc.getX() < centerX ? 1 : -1, 0);
                npc.alignToTileCenterY();
            }
            else if (bestDirX != 0 && Math.abs(npc.getY() - centerY) > 4.0f) {
                npc.setDirection(0, npc.getY() < centerY ? 1 : -1);
                npc.alignToTileCenterX();
            }
            else {
                npc.setDirection(bestDirX, bestDirY);
                if (bestDirX != 0) {
                    npc.alignToTileCenterY();
                } else {
                    npc.alignToTileCenterX();
                }
            }
        } else {
            npc.setDirection(-npc.getDirX(), -npc.getDirY());
        }
    }

    private Bomb findNearestBomb(int tileX, int tileY, List<Bomb> bombs) {
        Bomb nearest = null;
        double minDist = Double.MAX_VALUE;
        for (Bomb b : bombs) {
            double d = Math.hypot(tileX - b.getX(), tileY - b.getY());
            if (d < minDist) {
                minDist = d;
                nearest = b;
            }
        }
        return nearest;
    }

    @Override
    public Color getIndicatorColor() {
        return Color.MAGENTA;
    }
}