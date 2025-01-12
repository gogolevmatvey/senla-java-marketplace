package org.example.service;

import org.example.dto.ChatDto;
import org.example.dto.MessageDto;
import org.example.mapper.ChatMapper;
import org.example.mapper.MessageMapper;
import org.example.model.Ads;
import org.example.model.Chat;
import org.example.model.Message;
import org.example.model.User;
import org.example.repository.AdsDao;
import org.example.repository.ChatDao;
import org.example.repository.MessageDao;
import org.example.repository.UserDao;
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
        return chatMapper.toDto(chat);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userDao.findUserByUsername(username);
    }

    public MessageDto sendMessage(Long chatId, String content) {
        User sender = getCurrentUser();
        Chat chat = chatDao.read(chatId);

        User receiver = sender.getId().equals(chat.getBuyer().getId())
                ? chat.getAds().getUser()
                : chat.getBuyer();

        Message message = new Message(chat, sender, receiver, content);
        messageDao.create(message);
        return messageMapper.toDto(message);
    }
}
