package org.example.dto;

import org.example.model.AdsStatus;

import java.io.Serializable;

public class StatusDto implements Serializable {
    private AdsStatus status;

    public AdsStatus getStatus() {
        return status;
    }

    public void setStatus(AdsStatus status) {
        this.status = status;
    }
}
