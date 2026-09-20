package com.healthai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthai.dto.AiChatRequest;
import com.healthai.dto.CreateChatSessionRequest;
import com.healthai.dto.LoginRequest;
import com.healthai.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdaptiveChatbotTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerReq = new RegisterRequest(
                "adaptive.user@example.com", "Password123!", "Ananya Sen", 21, "female", "en");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)));

        LoginRequest loginReq = new LoginRequest("adaptive.user@example.com", "Password123!");
        MvcResult res = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq))).andReturn();

        userToken = com.jayway.jsonpath.JsonPath.read(res.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void testAdaptiveMultiTurnQuestioningScenario() throws Exception {
        // Create session
        CreateChatSessionRequest sessionReq = new CreateChatSessionRequest("Adaptive Test", "en");
        MvcResult sessionRes = mockMvc.perform(post("/api/chat/sessions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Number sessionIdNum = com.jayway.jsonpath.JsonPath.read(sessionRes.getResponse().getContentAsString(), "$.id");
        Long sessionId = sessionIdNum.longValue();

        // Turn 1: User: "I have fever"
        AiChatRequest turn1 = new AiChatRequest(sessionId, "I have fever");
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turn1)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", containsString("How long have you had the fever?")));

        // Turn 2: User: "3 days"
        AiChatRequest turn2 = new AiChatRequest(sessionId, "3 days");
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turn2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", containsString("mild, moderate, or severe")));

        // Turn 3: User provides severity and additional symptoms
        AiChatRequest turn3 = new AiChatRequest(sessionId, "moderate, and I also have cough and body pain");
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turn3)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", containsString("Do you also experience")));

        // Turn 4: User concludes: "No other symptoms"
        AiChatRequest turn4 = new AiChatRequest(sessionId, "No other symptoms");
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(turn4)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply", containsString("You may consider consulting")));

        // Verify Structured Symptom State endpoint
        mockMvc.perform(get("/api/ai/sessions/" + sessionId + "/state")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.duration").value("3 days"))
                .andExpect(jsonPath("$.severity").value("moderate"))
                .andExpect(jsonPath("$.age").value(21))
                .andExpect(jsonPath("$.gender").value("female"))
                .andExpect(jsonPath("$.symptoms").isArray());
    }
}
