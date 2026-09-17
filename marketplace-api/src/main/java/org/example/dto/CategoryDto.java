package org.example.dto;

import java.io.Serializable;

public class CategoryDto implements Serializable {
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
