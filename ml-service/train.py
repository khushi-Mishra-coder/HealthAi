import os
import json
import pandas as pd
import numpy as np
from sklearn.ensemble import RandomForestClassifier
import joblib

DATA_PATH = os.path.join(os.path.dirname(__file__), "data", "diseases_symptoms.csv")
MODELS_DIR = os.path.join(os.path.dirname(__file__), "models")
SPECIALTY_MAP_PATH = os.path.join(os.path.dirname(__file__), "full_specialty_map.json")

def train():
    os.makedirs(MODELS_DIR, exist_ok=True)
    
    print(f"Loading disease dataset from: {DATA_PATH}")
    df = pd.read_csv(DATA_PATH)
    
    target_col = df.columns[0]
    X = df.drop(columns=[target_col])
    y = df[target_col]
    
    feature_names = list(X.columns)
    print(f"Loaded {len(df)} disease samples with {len(feature_names)} symptom features.")
    
    # Train Random Forest classifier
    clf = RandomForestClassifier(n_estimators=100, max_depth=25, random_state=42)
    clf.fit(X, y)
    print("Random Forest model trained successfully.")

    # Save trained model and feature list
    joblib.dump(clf, os.path.join(MODELS_DIR, "model.joblib"))
    
    with open(os.path.join(MODELS_DIR, "symptoms_features.json"), "w", encoding="utf-8") as f:
        json.dump(feature_names, f, indent=2)

    # Load complete specialty map
    with open(SPECIALTY_MAP_PATH, "r", encoding="utf-8") as f:
        specialty_map = json.load(f)

    # Build disease metadata
    disease_meta = {}
    for idx, row in df.iterrows():
        disease = row[target_col]
        active_symptoms = [feat for feat in feature_names if row[feat] == 1]
        specialty = specialty_map.get(disease, "General Physician")
        
        disease_meta[disease] = {
            "specialty": specialty,
            "typical_symptoms": active_symptoms,
            "symptom_count": len(active_symptoms)
        }
        
    with open(os.path.join(MODELS_DIR, "disease_metadata.json"), "w", encoding="utf-8") as f:
        json.dump(disease_meta, f, indent=2)

    print(f"Artifacts saved to {MODELS_DIR}: model.joblib, symptoms_features.json, disease_metadata.json")

if __name__ == "__main__":
    train()
