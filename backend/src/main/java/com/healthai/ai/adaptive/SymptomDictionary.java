package com.healthai.ai.adaptive;

import java.util.*;

/**
 * Standardized dictionary mapping user terms (English & Hindi)
 * to exact medical feature columns matching the disease dataset.
 */
public final class SymptomDictionary {

    private SymptomDictionary() {
    }

    private static final Map<String, String> TERM_TO_CANONICAL = new LinkedHashMap<>();

    static {
        // Fever & Chills
        register("fever", "fever");
        register("high temperature", "fever");
        register("pyrexia", "fever");
        register("बुखार", "fever");
        register("chill", "chill");
        register("chills", "chill");
        register("shivering", "chill");
        register("ठंड", "chill");
        register("कंपकंपी", "chill");

        // Respiratory
        register("cough", "coughing");
        register("coughing", "coughing");
        register("dry cough", "coughing");
        register("खांसी", "coughing");
        register("sore throat", "sore throat");
        register("throat pain", "sore throat");
        register("गले में खराश", "sore throat");
        register("गले में दर्द", "sore throat");
        register("shortness of breath", "shortness breath");
        register("difficulty breathing", "shortness breath");
        register("breathlessness", "shortness breath");
        register("सांस फूलना", "shortness breath");
        register("सांस लेने में तकलीफ", "shortness breath");
        register("runny nose", "runny nose");
        register("sneezing", "sneezing");
        register("छींक", "sneezing");
        register("बहती नाक", "runny nose");

        // Head & Neuro
        register("headache", "headache");
        register("head pain", "headache");
        register("migraine", "headache");
        register("सिरदर्द", "headache");
        register("dizziness", "dizziness");
        register("dizzy", "dizziness");
        register("चक्कर", "dizziness");
        register("blurred vision", "blurred vision");
        register("blurry vision", "blurry vision");
        register("धुंधला दिखना", "blurred vision");
        register("neck stiffness", "neck stiffness");
        register("stiff neck", "neck stiffness");
        register("गर्दन में अकड़न", "neck stiffness");

        // Pain & Musculoskeletal
        register("body pain", "aching");
        register("body ache", "aching");
        register("बदन दर्द", "aching");
        register("joint pain", "joint bone pain");
        register("joint bone pain", "joint bone pain");
        register("जोड़ों का दर्द", "joint bone pain");
        register("chest pain", "chest pain");
        register("सीने में दर्द", "chest pain");
        register("chest tightness", "chest tightness");
        register("सीने में जकड़न", "chest tightness");
        register("back pain", "back");
        register("कमर दर्द", "back");
        register("muscle cramp", "muscle cramp");
        register("ऐंठन", "muscle cramp");

        // Gastrointestinal
        register("stomach pain", "stomach pain");
        register("abdominal cramp", "abdominal cramp");
        register("abdominal pain", "stomach pain");
        register("पेट दर्द", "stomach pain");
        register("nausea", "nausea");
        register("जी मिचलाना", "nausea");
        register("vomiting", "vomiting");
        register("उल्टी", "vomiting");
        register("diarrhea", "diarrhea");
        register("loose motion", "diarrhea");
        register("दस्त", "diarrhea");
        register("heartburn", "heartburn");
        register("acidity", "heartburn");
        register("छाती में जलन", "heartburn");
        register("loss of appetite", "loss appetite");
        register("poor appetite", "poor appetite");
        register("भूख न लगना", "loss appetite");

        // General
        register("fatigue", "fatigue");
        register("tiredness", "tiredness");
        register("exhaustion", "fatigue");
        register("थकान", "fatigue");
        register("weakness", "weakness limb");
        register("कमजोरी", "weakness limb");
        register("sweating", "sweat");
        register("sweat", "sweat");
        register("पसीना", "sweat");

        // Skin
        register("itching", "itching");
        register("itchiness", "itchiness");
        register("खुजली", "itching");
        register("rash", "red rash");
        register("red rash", "red rash");
        register("skin rash", "red rash");
        register("चकत्ते", "red rash");
        register("redness", "redness");
        register("लालिमा", "redness");

        // Emergency Signs
        register("unconscious", "loss consciousness may sweating");
        register("fainting", "loss consciousness may sweating");
        register("paralysis", "paralysis");
        register("लकवा", "paralysis");
    }

    private static void register(String term, String canonical) {
        TERM_TO_CANONICAL.put(term.toLowerCase(), canonical);
    }

    public static Set<String> extractCanonicalSymptoms(String text) {
        Set<String> detected = new LinkedHashSet<>();
        if (text == null || text.isBlank()) return detected;

        String lower = text.toLowerCase();
        for (Map.Entry<String, String> entry : TERM_TO_CANONICAL.entrySet()) {
            if (lower.contains(entry.getKey())) {
                detected.add(entry.getValue());
            }
        }
        return detected;
    }

    public static List<String> getDifferentialQuestions(String primarySymptom, Set<String> alreadyKnown) {
        Map<String, List<String>> associations = new HashMap<>();

        associations.put("fever", List.of("coughing", "headache", "chill", "aching", "sore throat"));
        associations.put("coughing", List.of("fever", "shortness breath", "sore throat", "chest tightness"));
        associations.put("headache", List.of("nausea", "blurred vision", "dizziness", "neck stiffness"));
        associations.put("stomach pain", List.of("nausea", "vomiting", "diarrhea", "heartburn"));
        associations.put("joint bone pain", List.of("joint swelling", "stiffness", "aching"));
        associations.put("red rash", List.of("itching", "burning", "fever"));

        List<String> candidates = associations.getOrDefault(primarySymptom, List.of("fatigue", "headache", "fever"));
        List<String> needed = new ArrayList<>();
        for (String c : candidates) {
            if (!alreadyKnown.contains(c)) {
                needed.add(c);
            }
        }
        return needed;
    }
}
