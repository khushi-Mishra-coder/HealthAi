package com.healthai.prediction;

import com.healthai.prediction.dto.MlDifferentialDiagnosis;
import com.healthai.prediction.dto.MlPredictRequest;
import com.healthai.prediction.dto.MlPredictResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.*;

@Component
public class MlPredictionClient {

    private static final Logger log = LoggerFactory.getLogger(MlPredictionClient.class);

    private final RestClient restClient;
    private final String mlServiceUrl;

    public MlPredictionClient(
            @Value("${healthai.ml.service-url:http://localhost:8000}") String mlServiceUrl,
            @Value("${healthai.ml.connect-timeout-ms:3000}") int connectTimeoutMs,
            @Value("${healthai.ml.read-timeout-ms:5000}") int readTimeoutMs) {
        this.mlServiceUrl = mlServiceUrl;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofMillis(connectTimeoutMs));
        requestFactory.setReadTimeout(Duration.ofMillis(readTimeoutMs));

        this.restClient = RestClient.builder()
                .baseUrl(mlServiceUrl)
                .requestFactory(requestFactory)
                .build();

        log.info("Initialized MlPredictionClient with base URL: {}", mlServiceUrl);
    }

    public MlPredictResponse predict(MlPredictRequest request) {
        try {
            log.info("Dispatching prediction request to ML service: symptoms={}", request.getSymptoms());
            MlPredictResponse response = restClient.post()
                    .uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(MlPredictResponse.class);

            if (response != null) {
                response.setFallback(false);
                return response;
            }
        } catch (Exception ex) {
            log.warn("ML service call failed ({}). Activating clinical heuristic fallback...", ex.getMessage());
        }

        return fallbackPrediction(request);
    }

    public Map<String, Object> checkHealth() {
        try {
            return restClient.get()
                    .uri("/health")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});
        } catch (Exception ex) {
            Map<String, Object> downStatus = new LinkedHashMap<>();
            downStatus.put("status", "DOWN");
            downStatus.put("serviceUrl", mlServiceUrl);
            downStatus.put("message", "Python ML microservice is unreachable. Fallback engine is active.");
            downStatus.put("error", ex.getMessage());
            return downStatus;
        }
    }

    public List<String> getSpecialties() {
        try {
            Map<String, List<String>> res = restClient.get()
                    .uri("/specialties")
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, List<String>>>() {});
            if (res != null && res.containsKey("specialties")) {
                return res.get("specialties");
            }
        } catch (Exception ex) {
            log.warn("Failed to retrieve specialties from ML service: {}", ex.getMessage());
        }
        return List.of(
                "Cardiologist", "Pulmonologist", "General Physician", "Neurologist",
                "Dermatologist", "Gastroenterologist", "Orthopedic", "Pediatrician",
                "Gynecologist", "ENT Specialist", "Endocrinologist", "Oncologist",
                "Emergency Medicine / Critical Care"
        );
    }

    /**
     * Intelligent clinical heuristic fallback when Python ML service is offline.
     */
    public MlPredictResponse fallbackPrediction(MlPredictRequest request) {
        List<String> symptoms = request.getSymptoms() != null ? request.getSymptoms() : List.of();
        String symptomsLower = String.join(" ", symptoms).toLowerCase();

        String topDisease;
        String specialty;
        String riskLevel;
        double confidence = 0.55;

        List<MlDifferentialDiagnosis> differential = new ArrayList<>();

        if (symptomsLower.contains("chest pain") || symptomsLower.contains("heart") || symptomsLower.contains("shortness breath")) {
            topDisease = "Acute Coronary Syndrome / Angina";
            specialty = "Emergency Medicine / Cardiologist";
            riskLevel = "High Risk";
            confidence = 0.78;

            differential.add(new MlDifferentialDiagnosis("Acute Coronary Syndrome", 0.78, List.of("chest pain", "shortness breath"), "Emergency Medicine / Cardiologist"));
            differential.add(new MlDifferentialDiagnosis("Costochondritis", 0.12, List.of("chest pain"), "General Physician / Orthopedic"));
            differential.add(new MlDifferentialDiagnosis("GERD", 0.10, List.of("chest discomfort"), "Gastroenterologist"));
        } else if (symptomsLower.contains("fever") || symptomsLower.contains("cough") || symptomsLower.contains("chills")) {
            topDisease = "Acute Viral Respiratory Infection";
            specialty = "General Physician / Pulmonologist";
            riskLevel = "Moderate";
            confidence = 0.65;

            differential.add(new MlDifferentialDiagnosis("Acute Viral Respiratory Infection", 0.65, List.of("fever", "cough"), "General Physician / Pulmonologist"));
            differential.add(new MlDifferentialDiagnosis("Bronchitis", 0.20, List.of("cough"), "Pulmonologist"));
            differential.add(new MlDifferentialDiagnosis("Common Cold", 0.15, List.of("fever", "cough"), "General Physician"));
        } else if (symptomsLower.contains("rash") || symptomsLower.contains("itching") || symptomsLower.contains("skin")) {
            topDisease = "Contact Dermatitis / Urticaria";
            specialty = "Dermatologist";
            riskLevel = "Low-to-Moderate";
            confidence = 0.60;

            differential.add(new MlDifferentialDiagnosis("Contact Dermatitis", 0.60, List.of("rash", "itching"), "Dermatologist"));
            differential.add(new MlDifferentialDiagnosis("Eczema", 0.25, List.of("itching"), "Dermatologist"));
            differential.add(new MlDifferentialDiagnosis("Allergic Reaction", 0.15, List.of("rash"), "Allergist / Dermatologist"));
        } else if (symptomsLower.contains("headache") || symptomsLower.contains("migraine") || symptomsLower.contains("dizzy")) {
            topDisease = "Tension Headache / Migraine";
            specialty = "Neurologist / General Physician";
            riskLevel = "Moderate";
            confidence = 0.62;

            differential.add(new MlDifferentialDiagnosis("Migraine", 0.62, List.of("headache"), "Neurologist"));
            differential.add(new MlDifferentialDiagnosis("Tension Headache", 0.28, List.of("headache"), "General Physician"));
            differential.add(new MlDifferentialDiagnosis("Cervicogenic Headache", 0.10, List.of("headache"), "Orthopedic / Neurologist"));
        } else {
            topDisease = "Undifferentiated Symptom Presentation";
            specialty = "General Physician";
            riskLevel = "Low-to-Moderate";
            confidence = 0.45;

            differential.add(new MlDifferentialDiagnosis("General Systemic Presentation", 0.45, symptoms, "General Physician"));
        }

        MlPredictResponse resp = new MlPredictResponse();
        resp.setTopDisease(topDisease);
        resp.setConfidence(confidence);
        resp.setRiskLevel(riskLevel);
        resp.setSuggestedSpecialty(specialty);
        resp.setGuidance("Consult a " + specialty + " for formal diagnostic evaluation.");
        resp.setDifferentialDiagnoses(differential);
        resp.setMatchedFeatures(symptoms);
        resp.setDisclaimer("Offline heuristic assessment fallback. This is for preliminary navigation only, not a medical diagnosis.");
        resp.setFallback(true);

        return resp;
    }
}
