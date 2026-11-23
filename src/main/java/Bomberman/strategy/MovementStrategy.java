package Bomberman.strategy;

import Bomberman.domain.Bomb;
import Bomberman.domain.Map;
import Bomberman.domain.NPC;
import Bomberman.domain.Player;
import java.awt.Color;
import java.util.List;

public interface MovementStrategy {
    void calculateMove(NPC npc, Map map, Player player, List<Bomb> bombs);
    Color getIndicatorColor();
}