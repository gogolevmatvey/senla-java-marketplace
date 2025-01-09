package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "advertisements")
public class Ads {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ads_id")
    private Long id;
    @Column(name = "ads_title", nullable = false)
    private String title;
    @Column(name = "ads_category", nullable = false)
    private String category;
    @Column(name = "ads_description", nullable = false, length = 1000)
    private String description;
    @Column(name = "ads_price", nullable = false)
    private double price;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "ads_creation_date")
    private LocalDate creationDate;
//    @Column(name = "ads_is_promoted")
//    private boolean isPromoted;
    @Column(name = "ads_promotion_end_date")
    private LocalDate promotionEndDate;
    @OneToMany(mappedBy = "ads", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments;
    @Enumerated(EnumType.STRING)
    @Column(name = "ads_status")
    private AdsStatus status;
    @Column(name = "ads_main_image")
    private byte[] mainImage;
    @OneToMany(mappedBy = "ads", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AdsImage> additionalImages;

    public Ads() {
    }

    public Ads(String title, String category, String description, double price, User user) {
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.user = user;
        this.creationDate = LocalDate.now();
        this.comments = new ArrayList<Comment>();
        this.status = AdsStatus.ACTIVE;
        this.mainImage = null;
        this.additionalImages = new ArrayList<>();
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDate getPromotionEndDate() {
        return promotionEndDate;
    }

    public void setPromotionEndDate(LocalDate promotionEndDate) {
        this.promotionEndDate = promotionEndDate;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public AdsStatus getStatus() {
        return status;
    }

    public void setStatus(AdsStatus status) {
        this.status = status;
    }

    public byte[] getMainImage() {
        return mainImage;
    }

    public void setMainImage(byte[] mainImage) {
        this.mainImage = mainImage;
    }

    public List<AdsImage> getAdditionalImages() {
        return additionalImages;
    }

    public void setAdditionalImages(List<AdsImage> additionalImages) {
        this.additionalImages = additionalImages;
    }

    @Override
    public String toString() {
        return "Ads{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", user=" + user +
                ", creationDate=" + creationDate +
                ", promotionEndDate=" + promotionEndDate +
                ", comments=" + comments +
                ", status=" + status +
                '}';
    }
}
