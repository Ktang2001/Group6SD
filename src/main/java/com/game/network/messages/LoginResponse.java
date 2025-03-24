package com.game.network.messages;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private boolean success;
    private User user;  // Will be null if login fails.

    public LoginResponse(boolean success, User user) {
        this.success = success;
        this.user = user;
    }

    public boolean isSuccess() { return success; }
    public User getUser() { return user; }
}