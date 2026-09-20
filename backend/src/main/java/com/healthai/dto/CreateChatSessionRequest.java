package com.healthai.dto;

public class CreateChatSessionRequest {

    private String sessionTitle;
    private String language = "en";

    public CreateChatSessionRequest() {
    }

    public CreateChatSessionRequest(String sessionTitle, String language) {
        this.sessionTitle = sessionTitle;
        this.language = language != null ? language : "en";
    }

    public String getSessionTitle() {
        return sessionTitle;
    }

    public void setSessionTitle(String sessionTitle) {
        this.sessionTitle = sessionTitle;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
