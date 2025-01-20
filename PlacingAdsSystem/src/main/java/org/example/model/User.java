package org.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "user_name", nullable = false, unique = true)
    private String username;
    @Column(name = "user_email", nullable = false)
    private String email;
    @Column(name = "user_password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    @Column(name = "user_role")
    private UserRole role;
    @Column(name = "user_seller_rating")
    private Double sellerRating = 0.0;
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Ads> advertisements;
    @OneToMany(mappedBy = "seller")
    private List<SaleHistory> sales = new ArrayList<>();
    @Column(name = "user_balance")
    private Double balance = 0.0;
    @Column(name = "user_picture")
    private byte[] profilePicture;


    public User() {}

    public User(String username, String email, String password, UserRole role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.sellerRating = 0.0;
        this.advertisements = new ArrayList<Ads>();
        this.profilePicture = null;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public List<Ads> getAdvertisements() {
        return advertisements;
    }

    public void setAdvertisements(List<Ads> advertisements) {
        this.advertisements = advertisements;
    }

    public List<SaleHistory> getSales() {
        return sales;
    }

    public void setSales(List<SaleHistory> sales) {
        this.sales = sales;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", role=" + role +
                ", sellerRating=" + sellerRating +
                ", balance=" + balance +
                '}';
    }
}
