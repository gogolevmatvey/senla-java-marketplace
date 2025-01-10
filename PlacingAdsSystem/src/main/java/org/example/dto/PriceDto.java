package org.example.dto;

import java.io.Serializable;

public class PriceDto implements Serializable {
    private double price;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
