package com.healthai.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthai.dto.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ChatAndHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String user1Token;
    private String user2Token;

    @BeforeEach
    void setUp() throws Exception {
        // Register and login User 1
        RegisterRequest user1 = new RegisterRequest(
                "patient1@example.com", "Password123!", "Patient One", 30, "Female", "en");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user1)));

        LoginRequest login1 = new LoginRequest("patient1@example.com", "Password123!");
        MvcResult res1 = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login1))).andReturn();
        user1Token = com.jayway.jsonpath.JsonPath.read(res1.getResponse().getContentAsString(), "$.token");

        // Register and login User 2
        RegisterRequest user2 = new RegisterRequest(
                "patient2@example.com", "Password123!", "Patient Two", 45, "Male", "hi");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user2)));

        LoginRequest login2 = new LoginRequest("patient2@example.com", "Password123!");
        MvcResult res2 = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login2))).andReturn();
        user2Token = com.jayway.jsonpath.JsonPath.read(res2.getResponse().getContentAsString(), "$.token");
    }

    @Test
    void testChatSessionAndMessageFlow() throws Exception {
        // 1. Create chat session for User 1
        CreateChatSessionRequest createSession = new CreateChatSessionRequest("Fever & Cough Assessment", "en");
        MvcResult sessionResult = mockMvc.perform(post("/api/chat/sessions")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createSession)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sessionTitle").value("Fever & Cough Assessment"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andReturn();

        Number sessionIdNum = com.jayway.jsonpath.JsonPath.read(sessionResult.getResponse().getContentAsString(), "$.id");
        Long sessionId = sessionIdNum.longValue();

        // 2. Post user message in session
        SendMessageRequest userMsg = new SendMessageRequest("I have had a mild fever and cough for 2 days", "USER", "{\"symptoms\":[\"fever\",\"cough\"],\"duration\":\"2 days\"}");
        mockMvc.perform(post("/api/chat/sessions/" + sessionId + "/messages")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userMsg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sender").value("USER"))
                .andExpect(jsonPath("$.content").value("I have had a mild fever and cough for 2 days"));

        // 3. Post AI assistant response in session
        SendMessageRequest aiMsg = new SendMessageRequest("Do you also experience difficulty breathing or body chills?", "AI", null);
        mockMvc.perform(post("/api/chat/sessions/" + sessionId + "/messages")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aiMsg)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sender").value("AI"));

        // 4. Retrieve session messages
        mockMvc.perform(get("/api/chat/sessions/" + sessionId + "/messages")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        // 5. User 2 trying to access User 1's chat session messages should be forbidden (404/not found for user)
        mockMvc.perform(get("/api/chat/sessions/" + sessionId + "/messages")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPatientHistoryFlowAndSearchTracking() throws Exception {
        // 1. Create Patient History record for User 1
        CreatePatientHistoryRequest historyReq = new CreatePatientHistoryRequest(
                null,
                "Fever, Headache, Body Pain",
                "3 days",
                "Moderate",
                "Preliminary Risk: Viral Infection / Common Cold",
                0.35,
                "Patient reported moderate fever and headache for 3 days without severe breathing difficulty.",
                "General Physician",
                "You may consider consulting a General Physician for professional evaluation."
        );

        MvcResult historyRes = mockMvc.perform(post("/api/history")
                        .header("Authorization", "Bearer " + user1Token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(historyReq)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.symptoms").value("Fever, Headache, Body Pain"))
                .andExpect(jsonPath("$.duration").value("3 days"))
                .andExpect(jsonPath("$.suggestedSpecialty").value("General Physician"))
                .andExpect(jsonPath("$.searchesCount").value(0))
                .andReturn();

        Number historyIdNum = com.jayway.jsonpath.JsonPath.read(historyRes.getResponse().getContentAsString(), "$.id");
        Long historyId = historyIdNum.longValue();

        // 2. Fetch User 1's history list
        mockMvc.perform(get("/api/history")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(historyId));

        // 3. Increment searches count when user clicks "Find Nearby Doctors"
        mockMvc.perform(post("/api/history/" + historyId + "/searched")
                        .header("Authorization", "Bearer " + user1Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.searchesCount").value(1));

        // 4. User 2's history should be empty
        mockMvc.perform(get("/api/history")
                        .header("Authorization", "Bearer " + user2Token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
