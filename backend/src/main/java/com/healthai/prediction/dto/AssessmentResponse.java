package com.healthai.prediction.dto;

import java.util.List;

public class AssessmentResponse {

    private MlPredictResponse mlPrediction;
    private String riskLevel;
    private String primarySpecialty;
    private String navigationAction;
    private Boolean emergencyFlag;
    private List<String> relevantSymptoms;
    private String personalizedRecommendations;
    private String contraindicationWarnings;
    private String assessmentTimestamp;
    private String disclaimer;

    public AssessmentResponse() {}

    public MlPredictResponse getMlPrediction() {
        return mlPrediction;
    }

    public void setMlPrediction(MlPredictResponse mlPrediction) {
        this.mlPrediction = mlPrediction;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getPrimarySpecialty() {
        return primarySpecialty;
    }

    public void setPrimarySpecialty(String primarySpecialty) {
        this.primarySpecialty = primarySpecialty;
    }

    public String getNavigationAction() {
        return navigationAction;
    }

    public void setNavigationAction(String navigationAction) {
        this.navigationAction = navigationAction;
    }

    public Boolean getEmergencyFlag() {
        return emergencyFlag;
    }

    public void setEmergencyFlag(Boolean emergencyFlag) {
        this.emergencyFlag = emergencyFlag;
    }

    public List<String> getRelevantSymptoms() {
        return relevantSymptoms;
    }

    public void setRelevantSymptoms(List<String> relevantSymptoms) {
        this.relevantSymptoms = relevantSymptoms;
    }

    public String getPersonalizedRecommendations() {
        return personalizedRecommendations;
    }

    public void setPersonalizedRecommendations(String personalizedRecommendations) {
        this.personalizedRecommendations = personalizedRecommendations;
    }

    public String getContraindicationWarnings() {
        return contraindicationWarnings;
    }

    public void setContraindicationWarnings(String contraindicationWarnings) {
        this.contraindicationWarnings = contraindicationWarnings;
    }

    public String getAssessmentTimestamp() {
        return assessmentTimestamp;
    }

    public void setAssessmentTimestamp(String assessmentTimestamp) {
        this.assessmentTimestamp = assessmentTimestamp;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }
}
