package com.game.network.messages;

import java.io.Serializable;
import java.util.Map;

public class GameUpdateMessage implements Serializable {
    // Maps each username -> [x, y] position
    private Map<String, int[]> playerPositions;
    private int p1Health; // Health for Player 1
    private int p2Health; // Health for Player 2

    // Constructor for GameUpdateMessage
    public GameUpdateMessage(Map<String, int[]> playerPositions, int p1Health, int p2Health) {
        this.playerPositions = playerPositions;
        this.p1Health = p1Health;
        this.p2Health = p2Health;
    }

    // Getter for player positions
    public Map<String, int[]> getPlayerPositions() {
        return playerPositions;
    }

    // Getter for Player 1 health
    public int getP1Health() {
        return p1Health;
    }

    // Getter for Player 2 health
    public int getP2Health() {
        return p2Health;
    }
}
