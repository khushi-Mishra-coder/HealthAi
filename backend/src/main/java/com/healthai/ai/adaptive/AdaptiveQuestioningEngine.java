package com.healthai.ai.adaptive;

import com.healthai.entity.ChatMessage;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AdaptiveQuestioningEngine {

    public StructuredSymptomState reconstructState(List<ChatMessage> history, Integer userAge, String userGender) {
        StructuredSymptomState state = new StructuredSymptomState(userAge, userGender);

        for (ChatMessage msg : history) {
            if ("USER".equalsIgnoreCase(msg.getSender())) {
                updateStateWithUserMessage(state, msg.getContent());
            }
        }
        return state;
    }

    public void updateStateWithUserMessage(StructuredSymptomState state, String userText) {
        if (userText == null || userText.isBlank()) return;

        // 1. Emergency Red-Flag Check
        if (isEmergencyText(userText)) {
            state.setEmergency(true);
            state.setAssessmentComplete(true);
            state.setStage("EMERGENCY");
            state.setSuggestedSpecialty("Emergency Medicine / Casualty");
            state.addSymptom("Severe Acute Condition");
            return;
        }

        // 2. Extract Symptoms via Dictionary
        Set<String> extracted = SymptomDictionary.extractCanonicalSymptoms(userText);
        for (String s : extracted) {
            if (state.getSymptoms().isEmpty()) {
                state.addSymptom(s);
            } else {
                state.addAdditionalSymptom(s);
            }
        }

        // 3. Extract Duration if not yet captured
        if (state.getDuration() == null) {
            String dur = extractDuration(userText);
            if (dur != null) {
                state.setDuration(dur);
            }
        }

        // 4. Extract Severity if not yet captured
        if (state.getSeverity() == null) {
            String sev = extractSeverity(userText);
            if (sev != null) {
                state.setSeverity(sev);
            }
        }

        // 5. Update Suggested Specialty
        state.setSuggestedSpecialty(inferSpecialty(state.getSymptoms(), userText));

        // 6. Update Stage
        if (state.getSymptoms().isEmpty()) {
            state.setStage("SYMPTOM_IDENTIFICATION");
        } else if (state.getDuration() == null) {
            state.setStage("DURATION");
        } else if (state.getSeverity() == null) {
            state.setStage("SEVERITY");
        } else if (state.getSymptoms().size() < 3) {
            state.setStage("ASSOCIATED");
        } else {
            state.setStage("COMPLETE");
            state.setAssessmentComplete(true);
        }
    }

    public String generateAdaptiveResponse(StructuredSymptomState state, String language, int conversationLength) {
        boolean isHindi = "hi".equalsIgnoreCase(language);

        // Emergency Response
        if (state.isEmergency()) {
            return isHindi
                    ? "चेतावनी: आपके लक्षण किसी गंभीर आपातकालीन स्थिति का संकेत हो सकते हैं। कृपया तुरंत किसी नजदीकी अस्पताल या आपातकालीन चिकित्सा विभाग (Emergency Room) में जाएं।"
                    : "URGENT SAFETY WARNING: Your symptoms indicate a potentially serious emergency. Please seek immediate emergency medical care or visit the nearest emergency room.";
        }

        // Step 1: No symptoms identified yet
        if (state.getSymptoms().isEmpty()) {
            return isHindi
                    ? "नमस्ते। कृपया बताएं कि आप वर्तमान में क्या शारीरिक परेशानी या लक्षण महसूस कर रहे हैं?"
                    : "Hello. Could you please describe what specific symptoms or health concerns you are experiencing?";
        }

        String primary = state.getSymptoms().get(0);

        // Step 2: Duration missing
        if (state.getDuration() == null) {
            return isHindi
                    ? "आपको " + primary + " कितने दिनों या समय से महसूस हो रहा है?"
                    : "How long have you had the " + primary + "?";
        }

        // Step 3: Severity missing
        if (state.getSeverity() == null) {
            return isHindi
                    ? "यह परेशानी हल्की (mild), मध्यम (moderate) या बहुत तेज (severe) महसूस हो रही है?"
                    : "Would you describe the severity as mild, moderate, or severe?";
        }

        // Step 4: Inquire about associated differential symptoms
        List<String> needed = SymptomDictionary.getDifferentialQuestions(primary, new HashSet<>(state.getSymptoms()));
        if (!needed.isEmpty() && conversationLength < 6) {
            String formattedAssociated = String.join(", ", needed.subList(0, Math.min(3, needed.size())));
            return isHindi
                    ? "क्या आपको इनके साथ " + formattedAssociated + " भी महसूस हो रहा है?"
                    : "Do you also experience " + formattedAssociated + "?";
        }

        // Step 5: Final Assessment Consultation Navigation
        state.setAssessmentComplete(true);
        state.setStage("COMPLETE");
        String symptomList = String.join(", ", state.getSymptoms());
        String durText = state.getDuration() != null ? " (Duration: " + state.getDuration() + ")" : "";

        return isHindi
                ? "आपके द्वारा बताए गए लक्षणों (" + symptomList + durText + ") के आधार पर, यह एक प्रारंभिक स्वास्थ्य मूल्यांकन है। आप पेशेवर जांच के लिए " + state.getSuggestedSpecialty() + " से परामर्श करने पर विचार कर सकते हैं।"
                : "Based on the symptoms you described (" + symptomList + durText + "), this is a preliminary health-risk assessment. You may consider consulting a " + state.getSuggestedSpecialty() + " for professional evaluation.";
    }

    private boolean isEmergencyText(String text) {
        String lower = text.toLowerCase();
        String[] keywords = {
                "chest pain", "cannot breathe", "cant breathe", "shortness of breath",
                "loss of consciousness", "unconscious", "stroke", "paralysis", "heavy bleeding",
                "सीने में दर्द", "सांस नहीं आ रही", "बेहोश", "लकवा"
        };
        for (String kw : keywords) {
            if (lower.contains(kw)) return true;
        }
        return false;
    }

    private String extractDuration(String text) {
        Pattern pattern = Pattern.compile("(\\d+|one|two|three|four|five|six|seven|दस|दो|तीन|चार|पांच)\\s*(days?|weeks?|months?|hours?|दिन|हफ्ते|घंटे)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private String extractSeverity(String text) {
        String lower = text.toLowerCase();
        if (lower.contains("severe") || lower.contains("तेज") || lower.contains("बहुत ज्यादा")) return "severe";
        if (lower.contains("mild") || lower.contains("हल्का") || lower.contains("थोड़ा")) return "mild";
        if (lower.contains("moderate") || lower.contains("मध्यम") || lower.contains("सामान्य")) return "moderate";
        return null;
    }

    private String inferSpecialty(List<String> symptoms, String text) {
        String combined = (String.join(" ", symptoms) + " " + text).toLowerCase();
        if (combined.contains("skin") || combined.contains("rash") || combined.contains("itching")) {
            return "Dermatologist";
        }
        if (combined.contains("cough") || combined.contains("breathing") || combined.contains("asthma")) {
            return "Pulmonologist";
        }
        if (combined.contains("heart") || combined.contains("chest")) {
            return "Cardiologist";
        }
        if (combined.contains("stomach") || combined.contains("diarrhea") || combined.contains("vomit") || combined.contains("nausea")) {
            return "Gastroenterologist";
        }
        if (combined.contains("joint") || combined.contains("bone") || combined.contains("cramp")) {
            return "Orthopedic";
        }
        if (combined.contains("eye") || combined.contains("vision") || combined.contains("blur")) {
            return "Ophthalmologist";
        }
        if (combined.contains("ear") || combined.contains("throat")) {
            return "ENT Specialist";
        }
        if (combined.contains("pregnancy") || combined.contains("period") || combined.contains("cramp")) {
            return "Gynecologist";
        }
        return "General Physician";
    }
}
