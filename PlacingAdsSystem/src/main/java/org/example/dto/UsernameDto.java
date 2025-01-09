package org.example.dto;

import java.io.Serializable;

public class UsernameDto implements Serializable {
    private String newUsername;

    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }
}
