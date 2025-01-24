package org.example.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;
    @ManyToOne()
    @JoinColumn(name = "chat_id")
    private Chat chat;
    @ManyToOne()
    @JoinColumn(name = "sender_id")
    private User sender;
    @ManyToOne()
    @JoinColumn(name = "receiver_id")
    private User receiver;
    @Column(name = "message_content", nullable = false)
    private String content;
    @Column(name = "message_send_date")
    private LocalDateTime sendDate;

    public Message() {
    }

    public Message(Chat chat, User sender, User receiver, String content) {
        this.chat = chat;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.sendDate = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDateTime sendDate) {
        this.sendDate = sendDate;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", chat=" + chat +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", content='" + content + '\'' +
                ", sendDate=" + sendDate +
                '}';
    }
}
