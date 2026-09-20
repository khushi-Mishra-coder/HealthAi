package com.healthai.prediction;

import com.healthai.prediction.dto.MlPredictRequest;
import com.healthai.prediction.dto.MlPredictResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PredictionService {

    private static final Logger log = LoggerFactory.getLogger(PredictionService.class);

    private final MlPredictionClient mlPredictionClient;

    public PredictionService(MlPredictionClient mlPredictionClient) {
        this.mlPredictionClient = mlPredictionClient;
    }

    public MlPredictResponse predictDiseaseRisk(MlPredictRequest request) {
        log.info("Processing disease risk prediction for {} symptoms", request.getSymptoms() != null ? request.getSymptoms().size() : 0);
        return mlPredictionClient.predict(request);
    }

    public Map<String, Object> checkStatus() {
        return mlPredictionClient.checkHealth();
    }

    public List<String> getAvailableSpecialties() {
        return mlPredictionClient.getSpecialties();
    }
}
