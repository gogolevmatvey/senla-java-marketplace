package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ads_images")
public class AdsImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "ads_id")
    private Ads ads;
    @Column(name = "image")
    private byte[] image;

    public AdsImage() {
    }

    public AdsImage(Ads ads, byte[] image) {
        this.ads = ads;
        this.image = image;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ads getAds() {
        return ads;
    }

    public void setAds(Ads ads) {
        this.ads = ads;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}



