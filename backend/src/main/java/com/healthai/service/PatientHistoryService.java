package com.healthai.service;

import com.healthai.dto.CreatePatientHistoryRequest;
import com.healthai.dto.PatientHistoryDto;
import com.healthai.entity.ChatSession;
import com.healthai.entity.PatientHistory;
import com.healthai.entity.User;
import com.healthai.exception.ResourceNotFoundException;
import com.healthai.mapper.PatientHistoryMapper;
import com.healthai.repository.ChatSessionRepository;
import com.healthai.repository.PatientHistoryRepository;
import com.healthai.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientHistoryService {

    private final PatientHistoryRepository patientHistoryRepository;
    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;
    private final PatientHistoryMapper patientHistoryMapper;

    public PatientHistoryService(PatientHistoryRepository patientHistoryRepository,
                                 UserRepository userRepository,
                                 ChatSessionRepository chatSessionRepository,
                                 PatientHistoryMapper patientHistoryMapper) {
        this.patientHistoryRepository = patientHistoryRepository;
        this.userRepository = userRepository;
        this.chatSessionRepository = chatSessionRepository;
        this.patientHistoryMapper = patientHistoryMapper;
    }

    @Transactional
    public PatientHistoryDto createHistory(String userEmail, CreatePatientHistoryRequest request) {
        User user = findUserByEmail(userEmail);

        ChatSession chatSession = null;
        if (request.getChatSessionId() != null) {
            chatSession = chatSessionRepository.findByIdAndUserId(request.getChatSessionId(), user.getId())
                    .orElse(null);
        }

        PatientHistory history = new PatientHistory();
        history.setUser(user);
        history.setChatSession(chatSession);
        history.setAssessmentDate(LocalDateTime.now());
        history.setSymptoms(request.getSymptoms());
        history.setDuration(request.getDuration());
        history.setSeverity(request.getSeverity());
        history.setRiskAssessmentResult(request.getRiskAssessmentResult());
        history.setRiskScore(request.getRiskScore());
        history.setConversationSummary(request.getConversationSummary());
        history.setSuggestedSpecialty(request.getSuggestedSpecialty());
        history.setSuggestedAction(request.getSuggestedAction());
        history.setSearchesCount(0);

        PatientHistory saved = patientHistoryRepository.save(history);
        return patientHistoryMapper.toDto(saved);
    }

    @Transactional(readOnly = true)
    public List<PatientHistoryDto> getUserHistory(String userEmail) {
        User user = findUserByEmail(userEmail);
        return patientHistoryRepository.findByUserIdOrderByAssessmentDateDesc(user.getId())
                .stream()
                .map(patientHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PatientHistoryDto getHistoryDetails(String userEmail, Long historyId) {
        PatientHistory history = findUserHistory(userEmail, historyId);
        return patientHistoryMapper.toDto(history);
    }

    @Transactional
    public PatientHistoryDto recordHealthcareSearch(String userEmail, Long historyId) {
        PatientHistory history = findUserHistory(userEmail, historyId);
        int currentSearches = history.getSearchesCount() != null ? history.getSearchesCount() : 0;
        history.setSearchesCount(currentSearches + 1);
        history.setUpdatedAt(LocalDateTime.now());
        PatientHistory saved = patientHistoryRepository.save(history);
        return patientHistoryMapper.toDto(saved);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private PatientHistory findUserHistory(String email, Long historyId) {
        User user = findUserByEmail(email);
        return patientHistoryRepository.findByIdAndUserId(historyId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient history #" + historyId + " not found for user"));
    }
}
