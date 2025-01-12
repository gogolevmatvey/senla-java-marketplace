package org.example.dto;

import org.example.model.SaleHistory;
import org.example.model.UserRole;

import java.io.Serializable;
import java.util.List;

public class UserDto implements Serializable {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private Double sellerRating;
    private List<Long> advertisementIds;
    private byte[] profilePicture;
    private List<SaleHistoryDto> salesAsSeller;
    private List<SaleHistoryDto> salesAsBuyer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(Double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public List<Long> getAdvertisementIds() {
        return advertisementIds;
    }

    public void setAdvertisementIds(List<Long> advertisementIds) {
        this.advertisementIds = advertisementIds;
    }
}
