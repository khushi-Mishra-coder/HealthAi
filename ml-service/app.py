import os
import json
from typing import List, Optional
import numpy as np
import pandas as pd
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel, Field
import joblib

app = FastAPI(
    title="Personalized AI Health Assistant - Disease Risk Prediction ML Service",
    version="1.0.0",
    description="Explainable preliminary disease-risk prediction using Random Forest trained on 261 diseases and 489 clinical symptom features."
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

BASE_DIR = os.path.dirname(__file__)
MODEL_PATH = os.path.join(BASE_DIR, "models", "model.joblib")
FEATURES_PATH = os.path.join(BASE_DIR, "models", "symptoms_features.json")
METADATA_PATH = os.path.join(BASE_DIR, "models", "disease_metadata.json")

# Load model and artifacts
print("Loading model and artifacts...")
model = joblib.load(MODEL_PATH)
with open(FEATURES_PATH, "r", encoding="utf-8") as f:
    feature_names: List[str] = json.load(f)
with open(METADATA_PATH, "r", encoding="utf-8") as f:
    disease_metadata = json.load(f)

# Normalize symptom feature lookup
feature_index_map = {feat.lower().strip(): idx for idx, feat in enumerate(feature_names)}
classes_list = list(model.classes_)
print(f"ML Service ready. Loaded {len(classes_list)} disease classes and {len(feature_names)} features.")

class PredictRequest(BaseModel):
    symptoms: List[str] = Field(..., description="List of detected symptoms")
    age: Optional[int] = Field(None, description="Patient age")
    gender: Optional[str] = Field(None, description="Patient gender")

class DifferentialDiagnosis(BaseModel):
    disease: str
    probability: float
    matched_symptoms: List[str]
    suggested_specialty: str

class PredictResponse(BaseModel):
    top_disease: str
    confidence: float
    risk_level: str
    suggested_specialty: str
    guidance: str
    differential_diagnoses: List[DifferentialDiagnosis]
    matched_features: List[str]
    disclaimer: str

@app.get("/health")
def health():
    return {
        "status": "UP",
        "service": "Personalized AI Health Assistant ML Prediction Service",
        "model": "Random Forest Classifier",
        "total_classes": len(classes_list),
        "total_features": len(feature_names)
    }

@app.get("/symptoms")
def get_symptoms():
    return {
        "total": len(feature_names),
        "features": feature_names
    }

@app.get("/specialties")
def get_specialties():
    specialties = sorted(list(set(meta["specialty"] for meta in disease_metadata.values())))
    return {"specialties": specialties}

@app.post("/predict", response_model=PredictResponse)
def predict(req: PredictRequest):
    if not req.symptoms:
        raise HTTPException(status_code=400, detail="At least one symptom must be provided.")

    # 1. Build binary feature vector
    input_vector = np.zeros((1, len(feature_names)), dtype=int)
    matched_features = []

    for sym in req.symptoms:
        s_clean = sym.lower().strip()
        # Direct match
        if s_clean in feature_index_map:
            input_vector[0, feature_index_map[s_clean]] = 1
            matched_features.append(s_clean)
        else:
            # Substring match across features
            for feat_clean, f_idx in feature_index_map.items():
                if s_clean in feat_clean or feat_clean in s_clean:
                    input_vector[0, f_idx] = 1
                    matched_features.append(feature_names[f_idx])
                    break

    matched_features = list(set(matched_features))
    input_df = pd.DataFrame(input_vector, columns=feature_names)

    # 2. Model prediction
    probabilities = model.predict_proba(input_df)[0]
    top_indices = np.argsort(probabilities)[::-1]

    differential: List[DifferentialDiagnosis] = []
    for rank in range(min(5, len(top_indices))):
        idx = top_indices[rank]
        prob = float(probabilities[idx])
        disease_name = classes_list[idx]
        meta = disease_metadata.get(disease_name, {})
        spec = meta.get("specialty", "General Physician")
        typical = set(meta.get("typical_symptoms", []))
        
        overlap = [m for m in matched_features if m in typical]

        differential.append(DifferentialDiagnosis(
            disease=disease_name,
            probability=round(prob, 4),
            matched_symptoms=overlap,
            suggested_specialty=spec
        ))

    # Top prediction details
    top_candidate = differential[0]
    top_disease = top_candidate.disease
    confidence = top_candidate.probability

    # Knowledge base Jaccard ranking fallback for sparse inputs
    if confidence < 0.15 and matched_features:
        best_overlap_disease = None
        max_overlap_count = -1
        for d_name, d_meta in disease_metadata.items():
            typical_set = set(d_meta.get("typical_symptoms", []))
            common = len(set(matched_features).intersection(typical_set))
            if common > max_overlap_count:
                max_overlap_count = common
                best_overlap_disease = d_name

        if best_overlap_disease and max_overlap_count > 0:
            top_disease = best_overlap_disease
            confidence = min(0.85, 0.45 + (max_overlap_count * 0.15))
            top_meta = disease_metadata.get(top_disease, {})
            top_candidate = DifferentialDiagnosis(
                disease=top_disease,
                probability=round(confidence, 4),
                matched_symptoms=list(set(matched_features).intersection(set(top_meta.get("typical_symptoms", [])))),
                suggested_specialty=top_meta.get("specialty", "General Physician")
            )
            differential = [d for d in differential if d.disease != top_disease]
            differential.insert(0, top_candidate)
            differential = differential[:5]

    # Dynamic risk level determination
    if confidence >= 0.70 or any("emergency" in d.suggested_specialty.lower() for d in differential[:2]):
        risk_level = "High Risk" if any("emergency" in d.suggested_specialty.lower() for d in differential[:2]) else "Moderate-to-High"
    elif confidence >= 0.40:
        risk_level = "Moderate"
    else:
        risk_level = "Low-to-Moderate"

    guidance = f"Based on preliminary symptom matching, you may consider consulting a {top_candidate.suggested_specialty} for clinical verification."
    disclaimer = "This is a preliminary risk assessment prototype for academic demonstration and healthcare navigation. It is NOT a medical diagnosis or treatment plan."

    return PredictResponse(
        top_disease=top_disease,
        confidence=round(confidence, 2),
        risk_level=risk_level,
        suggested_specialty=top_candidate.suggested_specialty,
        guidance=guidance,
        differential_diagnoses=differential,
        matched_features=matched_features,
        disclaimer=disclaimer
    )

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app:app", host="0.0.0.0", port=8000, reload=False)
