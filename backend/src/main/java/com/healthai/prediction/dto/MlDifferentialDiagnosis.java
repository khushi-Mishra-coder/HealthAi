package com.healthai.prediction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MlDifferentialDiagnosis {

    private String disease;
    private Double probability;

    @JsonProperty("matched_symptoms")
    private List<String> matchedSymptoms;

    @JsonProperty("suggested_specialty")
    private String suggestedSpecialty;

    public MlDifferentialDiagnosis() {}

    public MlDifferentialDiagnosis(String disease, Double probability, List<String> matchedSymptoms, String suggestedSpecialty) {
        this.disease = disease;
        this.probability = probability;
        this.matchedSymptoms = matchedSymptoms;
        this.suggestedSpecialty = suggestedSpecialty;
    }

    public String getDisease() {
        return disease;
    }

    public void setDisease(String disease) {
        this.disease = disease;
    }

    public Double getProbability() {
        return probability;
    }

    public void setProbability(Double probability) {
        this.probability = probability;
    }

    public List<String> getMatchedSymptoms() {
        return matchedSymptoms;
    }

    public void setMatchedSymptoms(List<String> matchedSymptoms) {
        this.matchedSymptoms = matchedSymptoms;
    }

    public String getSuggestedSpecialty() {
        return suggestedSpecialty;
    }

    public void setSuggestedSpecialty(String suggestedSpecialty) {
        this.suggestedSpecialty = suggestedSpecialty;
    }
}
