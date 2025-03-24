package com.game.network.messages;

import java.io.Serializable;

public class CreateMatchMessage implements Serializable {
    private String username; // So the server knows who is creating the match

    public CreateMatchMessage(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
