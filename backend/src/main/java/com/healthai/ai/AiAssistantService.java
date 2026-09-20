package com.healthai.ai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthai.ai.adaptive.AdaptiveQuestioningEngine;
import com.healthai.ai.adaptive.StructuredSymptomState;
import com.healthai.dto.AiChatResponse;
import com.healthai.dto.PatientHistoryDto;
import com.healthai.entity.ChatMessage;
import com.healthai.entity.ChatSession;
import com.healthai.entity.PatientHistory;
import com.healthai.entity.User;
import com.healthai.exception.ResourceNotFoundException;
import com.healthai.mapper.PatientHistoryMapper;
import com.healthai.repository.ChatMessageRepository;
import com.healthai.repository.ChatSessionRepository;
import com.healthai.repository.PatientHistoryRepository;
import com.healthai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiAssistantService {

    private final AiClient aiClient;
    private final AdaptiveQuestioningEngine adaptiveEngine;
    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final PatientHistoryRepository patientHistoryRepository;
    private final PatientHistoryMapper patientHistoryMapper;
    private final ObjectMapper objectMapper;

    public AiAssistantService(
            AiClient aiClient,
            AdaptiveQuestioningEngine adaptiveEngine,
            ChatSessionRepository chatSessionRepository,
            ChatMessageRepository chatMessageRepository,
            UserRepository userRepository,
            PatientHistoryRepository patientHistoryRepository,
            PatientHistoryMapper patientHistoryMapper,
            ObjectMapper objectMapper) {
        this.aiClient = aiClient;
        this.adaptiveEngine = adaptiveEngine;
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.patientHistoryRepository = patientHistoryRepository;
        this.patientHistoryMapper = patientHistoryMapper;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public AiChatResponse chat(String userEmail, Long sessionId, String userMessage) {
        User user = findUserByEmail(userEmail);
        ChatSession session = findUserSession(user.getId(), sessionId);

        // 1. Fetch prior conversation history
        List<ChatMessage> history = chatMessageRepository.findByChatSessionIdOrderByCreatedAtAsc(session.getId());

        // 2. Persist the new incoming user message
        ChatMessage userMsg = new ChatMessage(session, "USER", userMessage, null);
        ChatMessage savedUserMsg = chatMessageRepository.save(userMsg);

        // 3. Process via Spring AI / Adaptive Engine
        AiStructuredResponse aiResponse = aiClient.processConversation(
                history,
                userMessage,
                session.getLanguage(),
                user.getAge(),
                user.getGender()
        );

        // 4. Serialize structured clinical data
        String structuredJson = null;
        try {
            Map<String, Object> data = new LinkedHashMap<>();
            data.put("symptoms", aiResponse.getSymptoms());
            data.put("duration", aiResponse.getDuration());
            data.put("severity", aiResponse.getSeverity());
            data.put("suggestedSpecialty", aiResponse.getSuggestedSpecialty());
            data.put("isEmergency", aiResponse.isEmergency());
            data.put("isAssessmentComplete", aiResponse.isAssessmentComplete());
            data.put("age", user.getAge());
            data.put("gender", user.getGender());
            structuredJson = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            structuredJson = "{}";
        }

        // 5. Persist the AI assistant message
        ChatMessage aiMsg = new ChatMessage(session, "AI", aiResponse.getReply(), structuredJson);
        ChatMessage savedAiMsg = chatMessageRepository.save(aiMsg);

        // 6. Update session state and title if applicable
        if ((session.getSessionTitle() == null || session.getSessionTitle().startsWith("Health Consultation")) && !aiResponse.getSymptoms().isEmpty()) {
            session.setSessionTitle("Consultation: " + String.join(", ", aiResponse.getSymptoms()));
        }
        if (aiResponse.isEmergency()) {
            session.setStatus("EMERGENCY_ESCALATED");
        } else if (aiResponse.isAssessmentComplete()) {
            session.setStatus("COMPLETED");
        }
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        return new AiChatResponse(
                session.getId(),
                savedUserMsg.getId(),
                savedAiMsg.getId(),
                aiResponse.getReply(),
                aiResponse.getSymptoms(),
                aiResponse.getDuration(),
                aiResponse.getSeverity(),
                aiResponse.getSuggestedSpecialty(),
                aiResponse.isEmergency(),
                aiResponse.isAssessmentComplete()
        );
    }

    @Transactional(readOnly = true)
    public StructuredSymptomState getSessionState(String userEmail, Long sessionId) {
        User user = findUserByEmail(userEmail);
        ChatSession session = findUserSession(user.getId(), sessionId);
        List<ChatMessage> history = chatMessageRepository.findByChatSessionIdOrderByCreatedAtAsc(session.getId());
        return adaptiveEngine.reconstructState(history, user.getAge(), user.getGender());
    }

    @Transactional
    public PatientHistoryDto evaluateAndSaveSession(String userEmail, Long sessionId) {
        User user = findUserByEmail(userEmail);
        ChatSession session = findUserSession(user.getId(), sessionId);
        List<ChatMessage> history = chatMessageRepository.findByChatSessionIdOrderByCreatedAtAsc(session.getId());

        AiStructuredResponse evaluation = aiClient.processConversation(
                history, "", session.getLanguage(), user.getAge(), user.getGender());

        String summary = aiClient.generateSummary(history, session.getLanguage());

        String symptomsStr = evaluation.getSymptoms().isEmpty()
                ? "General symptoms"
                : String.join(", ", evaluation.getSymptoms());

        String riskResult = evaluation.isEmergency()
                ? "HIGH RISK: Potential Emergency Signs Detected"
                : "Preliminary Risk: Low-to-Moderate (Symptoms: " + symptomsStr + ")";

        double score = evaluation.isEmergency() ? 0.90 : 0.35;

        String action = "You may consider consulting a " + evaluation.getSuggestedSpecialty() + " for professional evaluation.";

        PatientHistory historyRecord = new PatientHistory(
                user,
                session,
                symptomsStr,
                evaluation.getDuration() != null ? evaluation.getDuration() : "recent",
                evaluation.getSeverity() != null ? evaluation.getSeverity() : "moderate",
                riskResult,
                score,
                summary,
                evaluation.getSuggestedSpecialty(),
                action
        );

        PatientHistory saved = patientHistoryRepository.save(historyRecord);
        session.setStatus("COMPLETED");
        session.setUpdatedAt(LocalDateTime.now());
        chatSessionRepository.save(session);

        return patientHistoryMapper.toDto(saved);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private ChatSession findUserSession(Long userId, Long sessionId) {
        return chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat session #" + sessionId + " not found for user"));
    }
}
