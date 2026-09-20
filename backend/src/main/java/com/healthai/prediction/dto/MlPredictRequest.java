package com.healthai.prediction.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class MlPredictRequest {

    @NotEmpty(message = "At least one symptom must be provided.")
    private List<String> symptoms;

    private Integer age;
    private String gender;

    public MlPredictRequest() {}

    public MlPredictRequest(List<String> symptoms, Integer age, String gender) {
        this.symptoms = symptoms;
        this.age = age;
        this.gender = gender;
    }

    public List<String> getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(List<String> symptoms) {
        this.symptoms = symptoms;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
