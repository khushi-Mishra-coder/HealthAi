package com.healthai.dto;

import jakarta.validation.constraints.NotBlank;

public class SendMessageRequest {

    @NotBlank(message = "Message content cannot be blank")
    private String content;

    private String sender = "USER"; // USER, AI, SYSTEM

    private String structuredData;

    public SendMessageRequest() {
    }

    public SendMessageRequest(String content, String sender, String structuredData) {
        this.content = content;
        this.sender = sender != null ? sender : "USER";
        this.structuredData = structuredData;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getStructuredData() {
        return structuredData;
    }

    public void setStructuredData(String structuredData) {
        this.structuredData = structuredData;
    }
}
