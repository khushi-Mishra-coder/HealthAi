package com.healthai.ai;

import com.healthai.entity.ChatMessage;
import java.util.List;

public interface AiClient {

    AiStructuredResponse processConversation(
            List<ChatMessage> conversationHistory,
            String newUserMessage,
            String language,
            Integer userAge,
            String userGender
    );

    String generateSummary(List<ChatMessage> conversationHistory, String language);
}
