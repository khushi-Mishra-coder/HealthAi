package com.healthai.prediction;

import com.healthai.ai.adaptive.AdaptiveQuestioningEngine;
import com.healthai.ai.adaptive.StructuredSymptomState;
import com.healthai.entity.ChatMessage;
import com.healthai.entity.ChatSession;
import com.healthai.entity.PatientHistory;
import com.healthai.entity.User;
import com.healthai.prediction.dto.AssessmentRequest;
import com.healthai.prediction.dto.AssessmentResponse;
import com.healthai.prediction.dto.MlPredictRequest;
import com.healthai.prediction.dto.MlPredictResponse;
import com.healthai.repository.ChatMessageRepository;
import com.healthai.repository.ChatSessionRepository;
import com.healthai.repository.PatientHistoryRepository;
import com.healthai.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AssessmentService {

    private static final Logger log = LoggerFactory.getLogger(AssessmentService.class);

    private final PredictionService predictionService;
    private final AdaptiveQuestioningEngine questioningEngine;
    private final ChatMessageRepository chatMessageRepository;
    private final PatientHistoryRepository patientHistoryRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final UserRepository userRepository;

    public AssessmentService(
            PredictionService predictionService,
            AdaptiveQuestioningEngine questioningEngine,
            ChatMessageRepository chatMessageRepository,
            PatientHistoryRepository patientHistoryRepository,
            ChatSessionRepository chatSessionRepository,
            UserRepository userRepository) {
        this.predictionService = predictionService;
        this.questioningEngine = questioningEngine;
        this.chatMessageRepository = chatMessageRepository;
        this.patientHistoryRepository = patientHistoryRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AssessmentResponse evaluate(AssessmentRequest request, String userEmail) {
        User currentUser = null;
        if (userEmail != null && !userEmail.isBlank()) {
            currentUser = userRepository.findByEmail(userEmail).orElse(null);
        }
        return evaluate(request, currentUser);
    }

    @Transactional
    public AssessmentResponse evaluate(AssessmentRequest request, User currentUser) {
        List<String> symptoms = new ArrayList<>();
        if (request.getSymptoms() != null) {
            symptoms.addAll(request.getSymptoms());
        }

        // If symptoms are empty, reconstruct from ChatSession messages using AdaptiveQuestioningEngine
        if (symptoms.isEmpty() && request.getSessionId() != null) {
            List<ChatMessage> messages = chatMessageRepository.findByChatSessionIdOrderByCreatedAtAsc(request.getSessionId());
            StructuredSymptomState sessionState = questioningEngine.reconstructState(messages, request.getAge(), request.getGender());
            if (sessionState != null && sessionState.getSymptoms() != null) {
                symptoms.addAll(sessionState.getSymptoms());
                if (request.getDuration() == null) {
                    request.setDuration(sessionState.getDuration());
                }
                if (request.getSeverity() == null) {
                    request.setSeverity(sessionState.getSeverity());
                }
                if (request.getAge() == null && sessionState.getAge() != null) {
                    request.setAge(sessionState.getAge());
                }
                if (request.getGender() == null && sessionState.getGender() != null) {
                    request.setGender(sessionState.getGender());
                }
            }
        }

        if (symptoms.isEmpty()) {
            symptoms.add("unspecified fatigue");
        }

        // Call ML Prediction Microservice
        MlPredictRequest mlRequest = new MlPredictRequest(symptoms, request.getAge(), request.getGender());
        MlPredictResponse mlResponse = predictionService.predictDiseaseRisk(mlRequest);

        // Emergency detection
        boolean emergency = "High Risk".equalsIgnoreCase(mlResponse.getRiskLevel()) ||
                symptoms.stream().anyMatch(s -> s.toLowerCase().contains("chest pain") ||
                        s.toLowerCase().contains("difficulty breathing") ||
                        s.toLowerCase().contains("shortness of breath") ||
                        s.toLowerCase().contains("unconscious"));

        // Determine recommended healthcare navigation action
        String navigationAction;
        if (emergency) {
            navigationAction = "URGENT: Please proceed to the nearest Emergency Department or dial emergency services immediately (112 / 911). Do not drive yourself.";
        } else if ("Moderate-to-High".equalsIgnoreCase(mlResponse.getRiskLevel()) || "Moderate".equalsIgnoreCase(mlResponse.getRiskLevel())) {
            navigationAction = String.format("Recommend consulting a %s within 24 to 48 hours for clinical evaluation and diagnostic testing.", mlResponse.getSuggestedSpecialty());
        } else {
            navigationAction = String.format("Recommend monitoring symptoms. If symptoms persist or worsen, schedule a routine consultation with a %s.", mlResponse.getSuggestedSpecialty());
        }

        // Build personalized recommendation and contraindication alerts
        StringBuilder recommendations = new StringBuilder();
        recommendations.append("Primary Clinical Indication: ").append(mlResponse.getTopDisease()).append(".\n");
        recommendations.append("Recommended Specialty: ").append(mlResponse.getSuggestedSpecialty()).append(".\n");

        if (request.getDuration() != null && !request.getDuration().isBlank()) {
            recommendations.append("Symptom Duration: ").append(request.getDuration()).append(".\n");
        }
        if (request.getSeverity() != null && !request.getSeverity().isBlank()) {
            recommendations.append("Reported Severity: ").append(request.getSeverity()).append(".\n");
        }

        StringBuilder warnings = new StringBuilder();
        if (request.getExistingConditions() != null && !request.getExistingConditions().isEmpty()) {
            warnings.append("Note on Chronic Conditions: Patient reports history of ")
                    .append(String.join(", ", request.getExistingConditions()))
                    .append(". These conditions may exacerbate presentation and should be disclosed to the consulting physician.\n");
        }
        if (request.getAllergies() != null && !request.getAllergies().isEmpty()) {
            warnings.append("Allergy Alert: Patient is allergic to ")
                    .append(String.join(", ", request.getAllergies()))
                    .append(". Ensure all medical staff are notified before any prescriptions or interventions are administered.\n");
        }

        // Persist to PatientHistory if requested and user is authenticated
        if (Boolean.TRUE.equals(request.getSaveToHistory()) && currentUser != null) {
            PatientHistory history = new PatientHistory();
            history.setUser(currentUser);
            history.setAssessmentDate(LocalDateTime.now());
            history.setSymptoms(String.join(", ", symptoms));
            history.setDuration(request.getDuration() != null ? request.getDuration() : "Not specified");
            history.setSeverity(request.getSeverity() != null ? request.getSeverity() : "Moderate");
            history.setRiskAssessmentResult(mlResponse.getTopDisease());
            history.setRiskScore(mlResponse.getConfidence());
            history.setSuggestedSpecialty(mlResponse.getSuggestedSpecialty());
            history.setSuggestedAction(navigationAction);
            history.setConversationSummary(recommendations.toString());

            if (request.getSessionId() != null) {
                chatSessionRepository.findById(request.getSessionId()).ifPresent(history::setChatSession);
            }

            patientHistoryRepository.save(history);
            log.info("Persisted assessment result for user {} (history ID: {})", currentUser.getId(), history.getId());
        }

        AssessmentResponse response = new AssessmentResponse();
        response.setMlPrediction(mlResponse);
        response.setRiskLevel(emergency ? "High Risk" : mlResponse.getRiskLevel());
        response.setPrimarySpecialty(mlResponse.getSuggestedSpecialty());
        response.setNavigationAction(navigationAction);
        response.setEmergencyFlag(emergency);
        response.setRelevantSymptoms(symptoms);
        response.setPersonalizedRecommendations(recommendations.toString());
        response.setContraindicationWarnings(warnings.length() > 0 ? warnings.toString() : "No active contraindication warnings recorded.");
        response.setAssessmentTimestamp(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.setDisclaimer("This is a preliminary risk assessment prototype for educational and healthcare navigation purposes. It does not replace professional medical diagnosis, laboratory testing, or physician consultation.");

        return response;
    }
}
