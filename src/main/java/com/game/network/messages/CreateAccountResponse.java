package com.game.network.messages;

import java.io.Serializable;

public class CreateAccountResponse implements Serializable {
    private boolean success;
    private String message;

    public CreateAccountResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
