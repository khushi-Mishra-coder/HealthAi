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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest registerReq = new RegisterRequest(
                "aipatient@example.com", "Password123!", "AI Patient", 29, "Female", "en");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerReq)));

        LoginRequest loginReq = new LoginRequest("aipatient@example.com", "Password123!");
        MvcResult res = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq))).andReturn();

        userToken = com.jayway.jsonpath.JsonPath.read(res.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void testStandardAiChatAndSymptomExtraction() throws Exception {
        // 1. Create chat session
        CreateChatSessionRequest sessionReq = new CreateChatSessionRequest("Fever Evaluation", "en");
        MvcResult sessionRes = mockMvc.perform(post("/api/chat/sessions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionReq)))
                .andExpect(status().isCreated())
                .andReturn();

        Number sessionIdNum = com.jayway.jsonpath.JsonPath.read(sessionRes.getResponse().getContentAsString(), "$.id");
        Long sessionId = sessionIdNum.longValue();

        // 2. Chat with AI
        AiChatRequest chatReq = new AiChatRequest(sessionId, "I have had fever and severe headache for 3 days");
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").isNotEmpty())
                .andExpect(jsonPath("$.symptoms").isArray())
                .andExpect(jsonPath("$.duration").value("3 days"))
                .andExpect(jsonPath("$.emergency").value(false))
                .andExpect(jsonPath("$.disclaimer").isNotEmpty());

        // 3. Evaluate and save session as PatientHistory
        mockMvc.perform(post("/api/ai/evaluate/" + sessionId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedSpecialty").isNotEmpty())
                .andExpect(jsonPath("$.riskAssessmentResult").isNotEmpty());
    }

    @Test
    void testEmergencyDetection() throws Exception {
        CreateChatSessionRequest sessionReq = new CreateChatSessionRequest("Emergency Check", "en");
        MvcResult sessionRes = mockMvc.perform(post("/api/chat/sessions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionReq)))
                .andReturn();

        Number sessionIdNum = com.jayway.jsonpath.JsonPath.read(sessionRes.getResponse().getContentAsString(), "$.id");
        Long sessionId = sessionIdNum.longValue();

        AiChatRequest chatReq = new AiChatRequest(sessionId, "I am having sudden severe chest pain and cannot breathe");
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.emergency").value(true))
                .andExpect(jsonPath("$.suggestedSpecialty").value("Emergency Medicine / Casualty"));
    }

    @Test
    void testHindiLanguageAiChat() throws Exception {
        CreateChatSessionRequest sessionReq = new CreateChatSessionRequest("Hindi Consultation", "hi");
        MvcResult sessionRes = mockMvc.perform(post("/api/chat/sessions")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sessionReq)))
                .andReturn();

        Number sessionIdNum = com.jayway.jsonpath.JsonPath.read(sessionRes.getResponse().getContentAsString(), "$.id");
        Long sessionId = sessionIdNum.longValue();

        AiChatRequest chatReq = new AiChatRequest(sessionId, "मुझे 2 दिनों से तेज बुखार और खांसी है");
        mockMvc.perform(post("/api/ai/chat")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").isNotEmpty())
                .andExpect(jsonPath("$.symptoms").isArray());
    }
}
