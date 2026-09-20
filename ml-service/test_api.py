from app import health, predict, get_symptoms, get_specialties, PredictRequest

def test_health():
    res = health()
    assert res["status"] == "UP"
    assert res["total_classes"] == 261
    assert res["total_features"] == 489
    print("test_health passed! Status:", res)

def test_predict_respiratory():
    req = PredictRequest(
        symptoms=["fever", "coughing", "aching"],
        age=28,
        gender="male"
    )
    res = predict(req)
    assert res.top_disease is not None
    assert res.suggested_specialty is not None
    assert res.confidence > 0
    assert len(res.differential_diagnoses) > 0
    assert res.disclaimer is not None
    print(f"test_predict_respiratory passed! Top: {res.top_disease}, Confidence: {res.confidence}, Specialty: {res.suggested_specialty}")

def test_predict_cardio():
    req = PredictRequest(
        symptoms=["chest pain", "shortness breath", "sweat"],
        age=55,
        gender="male"
    )
    res = predict(req)
    assert res.top_disease is not None
    assert "Cardiologist" in res.suggested_specialty or "Emergency" in res.suggested_specialty
    print(f"test_predict_cardio passed! Top: {res.top_disease}, Risk: {res.risk_level}, Specialty: {res.suggested_specialty}")

def test_symptoms_list():
    s = get_symptoms()
    assert s["total"] == 489
    assert "fever" in s["features"]
    assert "headache" in s["features"]
    print("test_symptoms_list passed!")

if __name__ == "__main__":
    test_health()
    test_predict_respiratory()
    test_predict_cardio()
    test_symptoms_list()
    print("All ML service unit tests passed successfully!")
