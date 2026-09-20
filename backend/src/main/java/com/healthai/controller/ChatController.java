package com.healthai.controller;

import com.healthai.dto.ChatMessageDto;
import com.healthai.dto.ChatSessionDto;
import com.healthai.dto.CreateChatSessionRequest;
import com.healthai.dto.SendMessageRequest;
import com.healthai.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/sessions")
    public ResponseEntity<ChatSessionDto> createSession(
            Authentication authentication,
            @RequestBody(required = false) CreateChatSessionRequest request) {
        CreateChatSessionRequest req = request != null ? request : new CreateChatSessionRequest();
        ChatSessionDto session = chatService.createSession(authentication.getName(), req);
        return new ResponseEntity<>(session, HttpStatus.CREATED);
    }

    @GetMapping("/sessions")
    public ResponseEntity<List<ChatSessionDto>> getUserSessions(Authentication authentication) {
        List<ChatSessionDto> sessions = chatService.getUserSessions(authentication.getName());
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<ChatSessionDto> getSession(
            Authentication authentication,
            @PathVariable Long sessionId) {
        ChatSessionDto session = chatService.getSession(authentication.getName(), sessionId);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<ChatMessageDto> sendMessage(
            Authentication authentication,
            @PathVariable Long sessionId,
            @Valid @RequestBody SendMessageRequest request) {
        ChatMessageDto message = chatService.addMessage(authentication.getName(), sessionId, request);
        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping("/sessions/{sessionId}/messages")
    public ResponseEntity<List<ChatMessageDto>> getSessionMessages(
            Authentication authentication,
            @PathVariable Long sessionId) {
        List<ChatMessageDto> messages = chatService.getSessionMessages(authentication.getName(), sessionId);
        return ResponseEntity.ok(messages);
    }

    @PatchMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<ChatSessionDto> completeSession(
            Authentication authentication,
            @PathVariable Long sessionId) {
        ChatSessionDto session = chatService.completeSession(authentication.getName(), sessionId);
        return ResponseEntity.ok(session);
    }
}
