package com.healthai.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthai.ai.adaptive.AdaptiveQuestioningEngine;
import com.healthai.ai.adaptive.StructuredSymptomState;
import com.healthai.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.*;

@Service
public class SpringAiClient implements AiClient {

    private static final Logger log = LoggerFactory.getLogger(SpringAiClient.class);

    private final String apiKey;
    private final String baseUrl;
    private final String model;
    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final AdaptiveQuestioningEngine adaptiveEngine;

    public SpringAiClient(
            @Value("${healthai.ai.api-key:${SPRING_AI_OPENAI_API_KEY:}}") String apiKey,
            @Value("${healthai.ai.base-url:${SPRING_AI_BASE_URL:https://api.openai.com/v1}}") String baseUrl,
            @Value("${healthai.ai.model:${SPRING_AI_MODEL:gpt-4o-mini}}") String model,
            ObjectMapper objectMapper,
            AdaptiveQuestioningEngine adaptiveEngine) {
        this.apiKey = (apiKey != null) ? apiKey.trim() : "";
        this.baseUrl = (baseUrl != null && !baseUrl.isBlank()) ? baseUrl.trim() : "https://api.openai.com/v1";
        this.model = (model != null && !model.isBlank()) ? model.trim() : "gpt-4o-mini";
        this.objectMapper = objectMapper;
        this.adaptiveEngine = adaptiveEngine;
        this.restClient = RestClient.builder().baseUrl(this.baseUrl).build();
    }

    @Override
    public AiStructuredResponse processConversation(
            List<ChatMessage> conversationHistory,
            String newUserMessage,
            String language,
            Integer userAge,
            String userGender) {

        if (apiKey != null && !apiKey.isBlank() && !apiKey.contains("your_")) {
            try {
                return callLlmApi(conversationHistory, newUserMessage, language, userAge, userGender);
            } catch (Exception e) {
                log.warn("Spring AI LLM API call failed, falling back to adaptive engine: {}", e.getMessage());
            }
        }

        // Adaptive clinical reasoning engine
        return runAdaptiveAssessment(conversationHistory, newUserMessage, language, userAge, userGender);
    }

    @Override
    public String generateSummary(List<ChatMessage> conversationHistory, String language) {
        StructuredSymptomState state = adaptiveEngine.reconstructState(conversationHistory, null, null);
        if (state.getSymptoms().isEmpty()) {
            return "General consultation completed without acute symptom flags.";
        }

        String symptomsStr = String.join(", ", state.getSymptoms());
        String durStr = state.getDuration() != null ? " (Duration: " + state.getDuration() + ")" : "";

        if ("hi".equalsIgnoreCase(language)) {
            return "मरीज़ द्वारा बताए गए लक्षण: " + symptomsStr + durStr + "। प्रारंभिक मूल्यांकन पूर्ण हुआ।";
        }
        return "Patient reported symptoms: " + symptomsStr + durStr + ". Preliminary evaluation completed.";
    }

    private AiStructuredResponse callLlmApi(
            List<ChatMessage> conversationHistory,
            String newUserMessage,
            String language,
            Integer userAge,
            String userGender) throws Exception {

        String systemPrompt = "hi".equalsIgnoreCase(language) ? AiPromptTemplates.SYSTEM_PROMPT_HI : AiPromptTemplates.SYSTEM_PROMPT_EN;
        if (userAge != null || userGender != null) {
            systemPrompt += "\nPatient context: Age=" + (userAge != null ? userAge : "unspecified") +
                    ", Gender=" + (userGender != null ? userGender : "unspecified") + ".";
        }

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));

        for (ChatMessage msg : conversationHistory) {
            String role = "USER".equalsIgnoreCase(msg.getSender()) ? "user" : "assistant";
            messages.add(Map.of("role", role, "content", msg.getContent()));
        }
        messages.add(Map.of("role", "user", "content", newUserMessage));

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("response_format", Map.of("type", "json_object"));
        requestBody.put("temperature", 0.3);

        String rawResponse = restClient.post()
                .uri("/chat/completions")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(String.class);

        JsonNode root = objectMapper.readTree(rawResponse);
        String jsonContent = root.path("choices").get(0).path("message").path("content").asText();
        return objectMapper.readValue(jsonContent, AiStructuredResponse.class);
    }

    private AiStructuredResponse runAdaptiveAssessment(
            List<ChatMessage> conversationHistory,
            String newUserMessage,
            String language,
            Integer userAge,
            String userGender) {

        StructuredSymptomState state = adaptiveEngine.reconstructState(conversationHistory, userAge, userGender);
        adaptiveEngine.updateStateWithUserMessage(state, newUserMessage);
        String reply = adaptiveEngine.generateAdaptiveResponse(state, language, conversationHistory.size());

        String summary = (state.getSymptoms().isEmpty() ? "General consultation" : String.join(", ", state.getSymptoms())) +
                (state.getDuration() != null ? " for " + state.getDuration() : "");

        return new AiStructuredResponse(
                reply,
                state.getSymptoms(),
                state.getDuration(),
                state.getSeverity() != null ? state.getSeverity() : "moderate",
                state.getSuggestedSpecialty(),
                state.isEmergency(),
                state.isAssessmentComplete(),
                summary
        );
    }
}
