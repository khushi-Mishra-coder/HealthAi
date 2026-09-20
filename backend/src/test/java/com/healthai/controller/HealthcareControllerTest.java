package com.healthai.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class HealthcareControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnNearbyFacilities() throws Exception {
        mockMvc.perform(get("/api/healthcare/facilities")
                        .param("lat", "28.5672")
                        .param("lng", "77.2100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[0].name").exists())
                .andExpect(jsonPath("$[0].distanceKm").isNumber());
    }

    @Test
    void shouldFilterFacilitiesBySpecialty() throws Exception {
        mockMvc.perform(get("/api/healthcare/facilities")
                        .param("specialty", "Cardiologist"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].specialties", everyItem(hasItem(containsStringIgnoringCase("Cardio")))));
    }

    @Test
    void shouldFilterEmergencyFacilities() throws Exception {
        mockMvc.perform(get("/api/healthcare/facilities")
                        .param("emergencyOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())))
                .andExpect(jsonPath("$[*].emergency24x7", everyItem(is(true))));
    }

    @Test
    void shouldReturnHealthcareSpecialties() throws Exception {
        mockMvc.perform(get("/api/healthcare/specialties"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }
}
