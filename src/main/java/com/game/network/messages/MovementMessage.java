package com.game.network.messages;

import java.io.Serializable;

public class MovementMessage implements Serializable {
    private String username;   // Which player moved
    private int deltaX;        // How much to move in X
    private int deltaY;        // How much to move in Y

    public MovementMessage(String username, int deltaX, int deltaY) {
        this.username = username;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }

    public String getUsername() { return username; }
    public int getDeltaX() { return deltaX; }
    public int getDeltaY() { return deltaY; }
}