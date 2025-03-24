package com.game.network.messages;

import java.io.Serializable;

public class JoinMatchMessage implements Serializable {
    private String username; // The user who wants to join a match

    public JoinMatchMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
