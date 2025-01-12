package org.example.model;

import jakarta.persistence.*;

import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_histories")
public class SaleHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;
    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;
    @OneToOne
    @JoinColumn(name = "ads_id")
    private Ads ads;
    @Column(name = "sale_date")
    private LocalDateTime saleDate;
    @Column(name = "sale_price")
    private Double salePrice;

    public SaleHistory() {
    }

    public SaleHistory(User seller, User buyer, Ads ads, Double salePrice) {
        this.seller = seller;
        this.buyer = buyer;
        this.ads = ads;
        this.saleDate = LocalDateTime.now();
        this.salePrice = salePrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSeller() {
        return seller;
    }

    public void setSeller(User seller) {
        this.seller = seller;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public Ads getAds() {
        return ads;
    }

    public void setAds(Ads ads) {
        this.ads = ads;
    }

    public Double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(Double salePrice) {
        this.salePrice = salePrice;
    }
}
