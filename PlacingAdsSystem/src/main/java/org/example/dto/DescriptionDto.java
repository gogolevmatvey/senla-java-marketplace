package org.example.dto;

import java.io.Serializable;

public class DescriptionDto implements Serializable {
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
