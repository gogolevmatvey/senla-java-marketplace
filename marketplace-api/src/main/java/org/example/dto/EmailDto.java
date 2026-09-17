package org.example.dto;

import java.io.Serializable;

public class EmailDto implements Serializable {
    private String newEmail;

    public String getNewEmail() {
        return newEmail;
    }

    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }
}
