package org.example.controller;

import org.example.dto.ChatDto;
import org.example.dto.MessageDto;
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

    @GetMapping("/ads/{adsId}")
    public ResponseEntity<ChatDto> openChat(@PathVariable("adsId") Long adsId) {
        return ResponseEntity.ok(chatService.openChat(adsId));
    }

    @PostMapping("/ads/{adsId}")
    public ResponseEntity<MessageDto> sendMessage(@PathVariable("adsId") Long adsId, @RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(chatService.sendMessage(adsId, messageDto.getContent()));
    }

    @PatchMapping("/ads/{adsId}/messages/{messageId}")
    public ResponseEntity<MessageDto> editMessage(@PathVariable("messageId") Long messageId, @RequestBody MessageDto messageDto) {
        return ResponseEntity.ok(chatService.editMessage(messageId, messageDto.getContent()));
    }

    @DeleteMapping("/ads/{adsId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(@PathVariable("messageId") Long messageId) {
        chatService.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
