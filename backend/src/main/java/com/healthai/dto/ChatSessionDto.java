package com.healthai.dto;

import java.time.LocalDateTime;

public class ChatSessionDto {

    private Long id;
    private String sessionTitle;
    private String status;
    private String language;
    private int messageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ChatSessionDto() {
    }

    public ChatSessionDto(Long id, String sessionTitle, String status, String language, int messageCount, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.sessionTitle = sessionTitle;
        this.status = status;
        this.language = language;
        this.messageCount = messageCount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
