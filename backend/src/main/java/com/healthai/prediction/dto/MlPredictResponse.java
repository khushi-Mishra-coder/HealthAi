package com.healthai.prediction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MlPredictResponse {

    @JsonProperty("top_disease")
    private String topDisease;

    private Double confidence;

    @JsonProperty("risk_level")
    private String riskLevel;

    @JsonProperty("suggested_specialty")
    private String suggestedSpecialty;

    private String guidance;

    @JsonProperty("differential_diagnoses")
    private List<MlDifferentialDiagnosis> differentialDiagnoses;

    @JsonProperty("matched_features")
    private List<String> matchedFeatures;

    private String disclaimer;

    private Boolean fallback;

    public MlPredictResponse() {}

    public String getTopDisease() {
        return topDisease;
    }

    public void setTopDisease(String topDisease) {
        this.topDisease = topDisease;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getSuggestedSpecialty() {
        return suggestedSpecialty;
    }

    public void setSuggestedSpecialty(String suggestedSpecialty) {
        this.suggestedSpecialty = suggestedSpecialty;
    }

    public String getGuidance() {
        return guidance;
    }

    public void setGuidance(String guidance) {
        this.guidance = guidance;
    }

    public List<MlDifferentialDiagnosis> getDifferentialDiagnoses() {
        return differentialDiagnoses;
    }

    public void setDifferentialDiagnoses(List<MlDifferentialDiagnosis> differentialDiagnoses) {
        this.differentialDiagnoses = differentialDiagnoses;
    }

    public List<String> getMatchedFeatures() {
        return matchedFeatures;
    }

    public void setMatchedFeatures(List<String> matchedFeatures) {
        this.matchedFeatures = matchedFeatures;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public void setDisclaimer(String disclaimer) {
        this.disclaimer = disclaimer;
    }

    public Boolean getFallback() {
        return fallback;
    }

    public void setFallback(Boolean fallback) {
        this.fallback = fallback;
    }
}
