package com.healthai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPredictionStatus() throws Exception {
        mockMvc.perform(get("/api/prediction/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists());
    }

    @Test
    void shouldReturnSpecialties() throws Exception {
        mockMvc.perform(get("/api/prediction/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    void shouldPredictDiseaseRiskDirectly() throws Exception {
        String payload = """
            {
                "symptoms": ["cough", "fever"],
                "age": 30,
                "gender": "male"
            }
        """;

        mockMvc.perform(post("/api/prediction/predict")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.top_disease").isNotEmpty())
                .andExpect(jsonPath("$.suggested_specialty").isNotEmpty())
                .andExpect(jsonPath("$.confidence").isNumber())
                .andExpect(jsonPath("$.disclaimer").isNotEmpty());
    }

    @Test
    void shouldEvaluateAssessmentWithPersonalization() throws Exception {
        String payload = """
            {
                "symptoms": ["chest pain", "shortness of breath", "cold sweat"],
                "age": 58,
                "gender": "male",
                "duration": "2 hours",
                "severity": "Severe",
                "existingConditions": ["Hypertension"],
                "allergies": ["Penicillin"]
            }
        """;

        mockMvc.perform(post("/api/assessment/evaluate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("High Risk"))
                .andExpect(jsonPath("$.emergencyFlag").value(true))
                .andExpect(jsonPath("$.navigationAction", containsString("Emergency")))
                .andExpect(jsonPath("$.contraindicationWarnings", containsString("Hypertension")))
                .andExpect(jsonPath("$.contraindicationWarnings", containsString("Penicillin")));
    }
}
