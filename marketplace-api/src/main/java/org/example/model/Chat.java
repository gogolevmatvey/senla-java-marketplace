package org.example.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "ads_id")
    private Ads ads;
    @ManyToOne()
    @JoinColumn(name = "buyer_id")
    private User buyer;
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages;

    public Chat() {
    }

    public Chat(Ads ads, User buyer) {
        this.ads = ads;
        this.buyer = buyer;
        this.messages = new ArrayList<Message>();
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

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", ads=" + ads +
                ", buyer=" + buyer +
                ", messages=" + messages +
                '}';
    }
}
