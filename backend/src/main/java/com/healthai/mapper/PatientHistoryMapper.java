package com.healthai.mapper;

import com.healthai.dto.PatientHistoryDto;
import com.healthai.entity.PatientHistory;
import org.springframework.stereotype.Component;

@Component
public class PatientHistoryMapper {

    public PatientHistoryDto toDto(PatientHistory history) {
        if (history == null) return null;
        Long userId = history.getUser() != null ? history.getUser().getId() : null;
        Long sessionId = history.getChatSession() != null ? history.getChatSession().getId() : null;

        return new PatientHistoryDto(
                history.getId(),
                userId,
                sessionId,
                history.getAssessmentDate(),
                history.getSymptoms(),
                history.getDuration(),
                history.getSeverity(),
                history.getRiskAssessmentResult(),
                history.getRiskScore(),
                history.getConversationSummary(),
                history.getSuggestedSpecialty(),
                history.getSuggestedAction(),
                history.getSearchesCount(),
                history.getCreatedAt()
        );
    }
}
