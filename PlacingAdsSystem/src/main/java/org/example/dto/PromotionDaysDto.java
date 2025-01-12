package org.example.dto;

import java.io.Serializable;

public class PromotionDaysDto implements Serializable {
    private int promotionDays;

    public int getPromotionDays() {
        return promotionDays;
    }

    public void setPromotionDays(int promotionDays) {
        this.promotionDays = promotionDays;
    }
}
