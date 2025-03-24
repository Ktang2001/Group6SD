package com.game.network.messages;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private int wins;
    private int losses;

    public User(String username, int wins, int losses) {
        this.username = username;
        this.wins = wins;
        this.losses = losses;
    }

    public String getUsername() {
        return username;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }
}