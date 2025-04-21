package com.game.network.messages;

import java.io.Serializable;

public class GameOverMessage implements Serializable {
    private final String winner;
    private final String loser;

    public GameOverMessage(String winner, String loser) {
        this.winner = winner;
        this.loser = loser;
    }

    public String getWinner() {
        return winner;
    }

    public String getLoser() {
        return loser;
    }
}
