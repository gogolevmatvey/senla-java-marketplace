package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.ChatDto;
import org.example.dto.MessageDto;
import org.example.service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class ChatControllerTest {
    @Mock
    private ChatService chatService;

    @InjectMocks
    private ChatController chatController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void openChat_ShouldReturnChatDto() throws Exception {
        Long adsId = 1L;
        ChatDto expectedChat = new ChatDto();
        expectedChat.setId(1L);
        expectedChat.setAdsId(adsId);
        expectedChat.setMessages(new ArrayList<>());

        when(chatService.openChat(adsId)).thenReturn(expectedChat);

        mockMvc.perform(get("/chats/ads/{adsId}", adsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedChat.getId()))
                .andExpect(jsonPath("$.adsId").value(expectedChat.getAdsId()));
    }

    @Test
    void sendMessage_ShouldReturnMessageDto() throws Exception {
        Long adsId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setContent("Test message");

        MessageDto expectedResponse = new MessageDto();
        expectedResponse.setId(1L);
        expectedResponse.setContent("Test message");
        expectedResponse.setSendDate(LocalDateTime.now());

        when(chatService.sendMessage(eq(adsId), any(String.class))).thenReturn(expectedResponse);

        mockMvc.perform(post("/chats/ads/{adsId}", adsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.content").value(expectedResponse.getContent()));
    }

    @Test
    void editMessage_ShouldReturnUpdatedMessageDto() throws Exception {
        Long adsId = 1L;
        Long messageId = 1L;
        MessageDto messageDto = new MessageDto();
        messageDto.setContent("Updated message");

        MessageDto expectedResponse = new MessageDto();
        expectedResponse.setId(messageId);
        expectedResponse.setContent("Updated message");

        when(chatService.editMessage(eq(messageId), any(String.class))).thenReturn(expectedResponse);

        mockMvc.perform(patch("/chats/ads/{adsId}/messages/{messageId}", adsId, messageId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(messageDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedResponse.getId()))
                .andExpect(jsonPath("$.content").value(expectedResponse.getContent()));
    }

    @Test
    void deleteMessage_ShouldReturnNoContent() throws Exception {
        Long adsId = 1L;
        Long messageId = 1L;

        doNothing().when(chatService).deleteMessage(messageId);

        mockMvc.perform(delete("/chats/ads/{adsId}/messages/{messageId}", adsId, messageId))
                .andExpect(status().isNoContent());

        verify(chatService, times(1)).deleteMessage(messageId);
    }


}
