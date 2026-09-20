package com.healthai.service;

import com.healthai.dto.ChatMessageDto;
import com.healthai.dto.ChatSessionDto;
import com.healthai.dto.CreateChatSessionRequest;
import com.healthai.dto.SendMessageRequest;
import com.healthai.entity.ChatMessage;
import com.healthai.entity.ChatSession;
import com.healthai.entity.User;
import com.healthai.exception.ResourceNotFoundException;
import com.healthai.mapper.ChatMapper;
import com.healthai.repository.ChatMessageRepository;
import com.healthai.repository.ChatSessionRepository;
import com.healthai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatMapper chatMapper;

    public ChatService(ChatSessionRepository chatSessionRepository,
                       ChatMessageRepository chatMessageRepository,
                       UserRepository userRepository,
                       ChatMapper chatMapper) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatMapper = chatMapper;
    }

    @Transactional
    public ChatSessionDto createSession(String userEmail, CreateChatSessionRequest request) {
        User user = findUserByEmail(userEmail);

        String title = (request.getSessionTitle() != null && !request.getSessionTitle().isBlank())
                ? request.getSessionTitle()
                : "Health Consultation (" + (user.getPreferredLanguage().equalsIgnoreCase("hi") ? "Hindi" : "English") + ")";

        String lang = (request.getLanguage() != null && !request.getLanguage().isBlank())
                ? request.getLanguage()
                : user.getPreferredLanguage();

        ChatSession session = new ChatSession(user, title, lang);
        ChatSession saved = chatSessionRepository.save(session);
        return chatMapper.toSessionDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDto> getUserSessions(String userEmail) {
        User user = findUserByEmail(userEmail);
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(user.getId())
                .stream()
                .map(chatMapper::toSessionDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatSessionDto getSession(String userEmail, Long sessionId) {
        ChatSession session = findUserSession(userEmail, sessionId);
        return chatMapper.toSessionDto(session);
    }

    @Transactional
    public ChatMessageDto addMessage(String userEmail, Long sessionId, SendMessageRequest request) {
        ChatSession session = findUserSession(userEmail, sessionId);

        String sender = (request.getSender() != null && !request.getSender().isBlank())
                ? request.getSender().toUpperCase()
                : "USER";

        ChatMessage message = new ChatMessage(session, sender, request.getContent(), request.getStructuredData());
        ChatMessage saved = chatMessageRepository.save(message);

        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        return chatMapper.toMessageDto(saved);
    }

    @Transactional(readOnly = true)
    public List<ChatMessageDto> getSessionMessages(String userEmail, Long sessionId) {
        ChatSession session = findUserSession(userEmail, sessionId);
        return chatMessageRepository.findByChatSessionIdOrderByCreatedAtAsc(session.getId())
                .stream()
                .map(chatMapper::toMessageDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatSessionDto completeSession(String userEmail, Long sessionId) {
        ChatSession session = findUserSession(userEmail, sessionId);
        session.setStatus("COMPLETED");
        session.setUpdatedAt(LocalDateTime.now());
        ChatSession saved = chatSessionRepository.save(session);
        return chatMapper.toSessionDto(saved);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private ChatSession findUserSession(String email, Long sessionId) {
        User user = findUserByEmail(email);
        return chatSessionRepository.findByIdAndUserId(sessionId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat session #" + sessionId + " not found for user"));
    }
}
