package com.healthai.mapper;

import com.healthai.dto.ChatMessageDto;
import com.healthai.dto.ChatSessionDto;
import com.healthai.entity.ChatMessage;
import com.healthai.entity.ChatSession;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatSessionDto toSessionDto(ChatSession session) {
        if (session == null) return null;
        int msgCount = session.getMessages() != null ? session.getMessages().size() : 0;
        return new ChatSessionDto(
                session.getId(),
                session.getSessionTitle(),
                session.getStatus(),
                session.getLanguage(),
                msgCount,
                session.getCreatedAt(),
                session.getUpdatedAt()
        );
    }

    public ChatMessageDto toMessageDto(ChatMessage message) {
        if (message == null) return null;
        Long sessionId = message.getChatSession() != null ? message.getChatSession().getId() : null;
        return new ChatMessageDto(
                message.getId(),
                sessionId,
                message.getSender(),
                message.getContent(),
                message.getStructuredData(),
                message.getCreatedAt()
        );
    }
}
