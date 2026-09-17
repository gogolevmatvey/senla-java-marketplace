package org.example.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.example.model.AdsStatus;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

public class AdsDto implements Serializable {
    private Long id;
    private String title;
    private String category;
    private String description;
    private Double price;
    private String username;
    private Double sellerRating;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate creationDate;
    private boolean promoted;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate promotionStartDate;
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate promotionEndDate;
    private AdsStatus status;
    private List<CommentDto> comments;
    private byte[] mainImage;

    public AdsDto() {
    }

    public AdsDto(String title, String category, String description, Double price) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(Double sellerRating) {
        this.sellerRating = sellerRating;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isPromoted() {
        return promoted;
    }

    public void setPromoted(boolean promoted) {
        this.promoted = promoted;
    }

    public LocalDate getPromotionStartDate() {
        return promotionStartDate;
    }

    public void setPromotionStartDate(LocalDate promotionStartDate) {
        this.promotionStartDate = promotionStartDate;
    }

    public LocalDate getPromotionEndDate() {
        return promotionEndDate;
    }

    public void setPromotionEndDate(LocalDate promotionEndDate) {
        this.promotionEndDate = promotionEndDate;
    }

    public AdsStatus getStatus() {
        return status;
    }

    public void setStatus(AdsStatus status) {
        this.status = status;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentDto> comments) {
        this.comments = comments;
    }

    public byte[] getMainImage() {
        return mainImage;
    }

    public void setMainImage(byte[] mainImage) {
        this.mainImage = mainImage;
    }
}
