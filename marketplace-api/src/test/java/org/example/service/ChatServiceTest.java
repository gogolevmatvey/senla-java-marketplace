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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @Mock
    private ChatDao chatDao;
    @Mock
    private UserDao userDao;
    @Mock
    private AdsDao adsDao;
    @Mock
    private MessageDao messageDao;
    @Mock
    private ChatMapper chatMapper;
    @Mock
    private MessageMapper messageMapper;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;

    private ChatService chatService;
    private User testUser;
    private User sellerUser;
    private Ads testAds;

    @BeforeEach
    void setUp() {
        chatService = new ChatService(chatDao, userDao, adsDao, messageDao, chatMapper, messageMapper);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("buyer");
        testUser.setRole(UserRole.USER);

        sellerUser = new User();
        sellerUser.setId(2L);
        sellerUser.setUsername("seller");

        testAds = new Ads();
        testAds.setId(1L);
        testAds.setUser(sellerUser);

        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("buyer");
        when(userDao.findUserByUsername("buyer")).thenReturn(testUser);
    }

    @Test
    void openChat_NewChat_Success() {
        when(adsDao.read(1L)).thenReturn(testAds);
        when(chatDao.findByAdsAndBuyer(1L, 1L)).thenReturn(null);

        ChatDto expectedDto = new ChatDto();
        when(chatMapper.toDto(any(Chat.class))).thenReturn(expectedDto);

        ChatDto result = chatService.openChat(1L);

        verify(chatDao).create(any(Chat.class));
        assertEquals(expectedDto, result);
    }

    @Test
    void openChat_ExistingChat_ReturnsExistingChat() {
        when(adsDao.read(1L)).thenReturn(testAds);
        Chat existingChat = new Chat(testAds, testUser);
        when(chatDao.findByAdsAndBuyer(1L, 1L)).thenReturn(existingChat);

        ChatDto expectedDto = new ChatDto();
        when(chatMapper.toDto(existingChat)).thenReturn(expectedDto);

        ChatDto result = chatService.openChat(1L);

        verify(chatDao, never()).create(any(Chat.class));
        assertEquals(expectedDto, result);
    }

    @Test
    void openChat_SellerTryingToOpenChat_ThrowsException() {
        testUser.setId(2L);
        when(adsDao.read(1L)).thenReturn(testAds);

        assertThrows(IllegalStateException.class, () -> chatService.openChat(1L));
    }

    @Test
    void sendMessage_Success() {
        when(adsDao.read(1L)).thenReturn(testAds);
        Chat chat = new Chat(testAds, testUser);
        when(chatDao.findByAdsAndBuyer(1L, 1L)).thenReturn(chat);

        MessageDto expectedDto = new MessageDto();
        when(messageMapper.toDto(any(Message.class))).thenReturn(expectedDto);

        MessageDto result = chatService.sendMessage(1L, "Hello");

        verify(messageDao).create(any(Message.class));
        assertEquals(expectedDto, result);
    }

    @Test
    void editMessage_AsAdmin_Success() {
        testUser.setRole(UserRole.ADMIN);
        Message message = new Message();
        message.setSender(sellerUser);

        when(messageDao.read(1L)).thenReturn(message);
        MessageDto expectedDto = new MessageDto();
        when(messageMapper.toDto(any(Message.class))).thenReturn(expectedDto);

        MessageDto result = chatService.editMessage(1L, "Updated content");

        verify(messageDao).update(message);
        assertEquals(expectedDto, result);
    }

    @Test
    void editMessage_NotSender_ThrowsException() {
        Message message = new Message();
        message.setSender(sellerUser);
        when(messageDao.read(1L)).thenReturn(message);

        assertThrows(IllegalStateException.class, () ->
                chatService.editMessage(1L, "Updated content"));
    }

    @Test
    void deleteMessage_AsSender_Success() {
        Message message = new Message();
        message.setId(1L);
        message.setSender(testUser);
        when(messageDao.read(1L)).thenReturn(message);

        chatService.deleteMessage(1L);

        verify(messageDao).delete(1L);
    }

    @Test
    void deleteMessage_NotSender_ThrowsException() {
        Message message = new Message();
        message.setSender(sellerUser);
        when(messageDao.read(1L)).thenReturn(message);

        assertThrows(IllegalStateException.class, () ->
                chatService.deleteMessage(1L));
    }
}
