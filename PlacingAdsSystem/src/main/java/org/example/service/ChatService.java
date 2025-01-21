package org.example.service;

import org.example.dto.ChatDto;
import org.example.dto.MessageDto;
import org.example.mapper.ChatMapper;
import org.example.mapper.MessageMapper;
import org.example.model.*;
import org.example.repository.AdsDao;
import org.example.repository.ChatDao;
import org.example.repository.MessageDao;
import org.example.repository.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ChatService {
    private ChatDao chatDao;
    private UserDao userDao;
    private AdsDao adsDao;
    private MessageDao messageDao;
    private ChatMapper chatMapper;
    private MessageMapper messageMapper;
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);

    public ChatService(ChatDao chatDao, UserDao userDao, AdsDao adsDao, MessageDao messageDao, ChatMapper chatMapper,
                       MessageMapper messageMapper) {
        this.chatDao = chatDao;
        this.userDao = userDao;
        this.adsDao = adsDao;
        this.messageDao = messageDao;
        this.chatMapper = chatMapper;
        this.messageMapper = messageMapper;
    }

    public ChatDto openChat(Long adsId) {
        User currentUser = getCurrentUser();
        Ads ads = adsDao.read(adsId);

        if (currentUser.getId().equals(ads.getUser().getId())) {
            throw new IllegalStateException("Seller can't start chat with themselves");
        }

        Chat existingChat = chatDao.findByAdsAndBuyer(adsId, currentUser.getId());
        if (existingChat != null) {
            return chatMapper.toDto(existingChat);
        }

        Chat chat = new Chat(ads, currentUser);
        chatDao.create(chat);
        logger.info("Chat opened for ads {} by user {}", adsId, currentUser.getUsername());
        return chatMapper.toDto(chat);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findUserByUsername(username);
    }

    public MessageDto sendMessage(Long adsId, String content) {
        User sender = getCurrentUser();
        Ads ads = adsDao.read(adsId);
        Chat chat = chatDao.findByAdsAndBuyer(adsId, sender.getId());

        if (chat == null) {
            chat = new Chat(ads, sender);
            chatDao.create(chat);
        }

        User receiver = sender.getId().equals(chat.getBuyer().getId())
                ? chat.getAds().getUser()
                : chat.getBuyer();

        Message message = new Message(chat, sender, receiver, content);
        messageDao.create(message);
        logger.info("Message sent in chat for ads {} by user {}", adsId, sender.getUsername());
        return messageMapper.toDto(message);
    }

    public MessageDto editMessage(Long messageId, String newContent) {
        Message message = messageDao.read(messageId);

        validateMessageSender(message);

        message.setContent(newContent);
        messageDao.update(message);

        logger.info("Message {} edited by user {}", messageId, getCurrentUser().getUsername());
        return messageMapper.toDto(message);
    }

    private void validateMessageSender(Message message) {
        User currentUser = getCurrentUser();

        if (currentUser.getRole() == UserRole.ADMIN) {
            return;
        }

        if (!message.getSender().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("Only message sender can edit the message");
        }
    }

    public void deleteMessage(Long messageId) {
        Message message = messageDao.read(messageId);
        User currentUser = getCurrentUser();

        validateMessageSender(message);
        logger.info("Message {} deleted by user {}", messageId, currentUser.getUsername());
        messageDao.delete(message.getId());
    }
}
