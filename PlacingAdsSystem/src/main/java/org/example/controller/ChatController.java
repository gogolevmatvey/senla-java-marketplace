package org.example.controller;

import org.example.dto.ChatDto;
import org.example.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chats")
public class ChatController {
    private ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ads/{adsId}")
    public ResponseEntity<ChatDto> startChat(@PathVariable("adsId") Long adsId) {
        return ResponseEntity.ok(chatService.createChat(adsId));
    }
}
