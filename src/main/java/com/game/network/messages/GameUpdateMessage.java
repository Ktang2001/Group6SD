package com.game.network.messages;

import java.io.Serializable;
import java.util.Map;

public class GameUpdateMessage implements Serializable {
    // Maps each username -> [x, y] position
    private Map<String, int[]> playerPositions;

    public GameUpdateMessage(Map<String, int[]> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public Map<String, int[]> getPlayerPositions() {
        return playerPositions;
    }
}