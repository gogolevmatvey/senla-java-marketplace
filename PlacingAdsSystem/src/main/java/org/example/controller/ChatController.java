package org.example.controller;

import org.example.dto.ChatDto;
import org.example.dto.MessageDto;
import org.example.model.Message;
import org.example.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/{chatId}")
    public ResponseEntity<MessageDto> sendMessage(@PathVariable("chatId") Long chatId, @RequestBody MessageDto messageDto) {
        MessageDto sentMessage = chatService.sendMessage(chatId, messageDto.getContent());
        return ResponseEntity.ok(sentMessage);
    }
}
