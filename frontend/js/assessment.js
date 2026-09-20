/**
 * PersonalizedHealth AI - Symptom Assessment Wizard
 */

let selectedSymptoms = new Set();
let currentStep = 1;
const totalSteps = 3;

// Common symptom catalog for instant tag suggestions
const COMMON_SYMPTOMS = [
  "fever", "cough", "shortness breath", "chest pain", "headache", "fatigue",
  "sore throat", "nausea", "vomiting", "diarrhea", "cold sweat", "chills",
  "dizziness", "rash", "joint pain", "muscle ache", "abdominal pain",
  "loss of smell", "loss of taste", "fast heartbeat", "back pain", "wheezing"
];

document.addEventListener('DOMContentLoaded', async () => {
  // Pre-fill user profile if logged in
  const user = getUser();
  if (user) {
    if (user.age) document.getElementById('assess-age').value = user.age;
    if (user.gender) document.getElementById('assess-gender').value = user.gender.toLowerCase();
  }

  // Check if sessionId is provided in URL params
  const urlParams = new URLSearchParams(window.location.search);
  const sessionId = urlParams.get('sessionId');
  if (sessionId) {
    await prefillFromSession(sessionId);
  }

  // Render quick symptom suggestion chips
  renderSymptomSuggestions();

  // Symptom input keyboard events
  const input = document.getElementById('symptom-input');
  input.addEventListener('keydown', (e) => {
    if (e.key === 'Enter' || e.key === ',') {
      e.preventDefault();
      addSymptom(input.value.trim());
      input.value = '';
    }
  });

  // Severity range slider listener
  const severityRange = document.getElementById('severity-slider');
  const severityValue = document.getElementById('severity-val-text');
  if (severityRange && severityValue) {
    severityRange.addEventListener('input', (e) => {
      const val = parseInt(e.target.value, 10);
      let text = 'Mild (1-3)';
      if (val >= 8) text = 'Severe / Critical (8-10)';
      else if (val >= 4) text = 'Moderate (4-7)';
      severityValue.textContent = text;
    });
  }
});

function renderSymptomSuggestions() {
  const container = document.getElementById('symptom-suggestions');
  if (!container) return;

  container.innerHTML = COMMON_SYMPTOMS.map(s => `
    <span class="prompt-pill" style="cursor:pointer;" onclick="addSymptom('${s}')">+ ${s}</span>
  `).join('');
}

function addSymptom(symptom) {
  const clean = symptom.toLowerCase().replace(/[^\w\s-]/g, '').trim();
  if (!clean || selectedSymptoms.has(clean)) return;

  selectedSymptoms.add(clean);
  renderSelectedSymptomTags();
}

function removeSymptom(symptom) {
  selectedSymptoms.delete(symptom);
  renderSelectedSymptomTags();
}

function renderSelectedSymptomTags() {
  const container = document.getElementById('selected-symptoms-container');
  if (!container) return;

  if (selectedSymptoms.size === 0) {
    container.innerHTML = `<span style="color:var(--text-muted); font-size:13px;">No symptoms added yet. Type above or click suggestions below.</span>`;
    return;
  }

  container.innerHTML = Array.from(selectedSymptoms).map(s => `
    <span class="badge badge-teal" style="padding:6px 12px; font-size:13px;">
      ${s}
      <button type="button" onclick="removeSymptom('${s}')" style="background:none; border:none; color:inherit; margin-left:6px; cursor:pointer; font-weight:700;">&times;</button>
    </span>
  `).join('');
}

async function prefillFromSession(sessionId) {
  try {
    const state = await ChatAPI.getSessionState(sessionId);
    if (state && state.symptoms && state.symptoms.length > 0) {
      state.symptoms.forEach(s => selectedSymptoms.add(s.toLowerCase()));
      renderSelectedSymptomTags();
      if (state.duration) document.getElementById('assess-duration').value = state.duration;
      if (state.age) document.getElementById('assess-age').value = state.age;
      if (state.gender) document.getElementById('assess-gender').value = state.gender.toLowerCase();
      showToast(`Imported ${state.symptoms.length} symptoms from your AI consultation`, 'success');
    }
  } catch (err) {
    console.warn('Could not load session state:', err);
  }
}

function nextStep(step) {
  if (step === 2) {
    if (selectedSymptoms.size === 0) {
      showToast('Please add at least one symptom to proceed.', 'error');
      return;
    }
  }

  document.getElementById(`step-${currentStep}`).style.display = 'none';
  currentStep = step;
  document.getElementById(`step-${currentStep}`).style.display = 'block';

  // Update progress indicators
  for (let i = 1; i <= totalSteps; i++) {
    const indicator = document.getElementById(`step-indicator-${i}`);
    if (indicator) {
      if (i === currentStep) {
        indicator.className = 'badge badge-teal';
      } else if (i < currentStep) {
        indicator.className = 'badge badge-success';
      } else {
        indicator.className = 'badge';
        indicator.style.background = '#e2e8f0';
        indicator.style.color = '#64748b';
      }
    }
  }
}

function prevStep(step) {
  document.getElementById(`step-${currentStep}`).style.display = 'none';
  currentStep = step;
  document.getElementById(`step-${currentStep}`).style.display = 'block';
}

async function submitAssessment() {
  const submitBtn = document.getElementById('submit-assessment-btn');
  submitBtn.disabled = true;
  submitBtn.textContent = 'Evaluating Clinical Risk...';

  // Gather existing conditions
  const conditions = [];
  document.querySelectorAll('input[name="chronic-condition"]:checked').forEach(cb => {
    conditions.push(cb.value);
  });

  // Gather allergies
  const allergies = [];
  document.querySelectorAll('input[name="allergy"]:checked').forEach(cb => {
    allergies.push(cb.value);
  });
  const customAllergy = document.getElementById('custom-allergy')?.value.trim();
  if (customAllergy) allergies.push(customAllergy);

  const duration = document.getElementById('assess-duration').value;
  const severity = document.getElementById('severity-val-text').textContent;
  const age = parseInt(document.getElementById('assess-age').value, 10) || 30;
  const gender = document.getElementById('assess-gender').value || 'unspecified';
  const lifestyle = document.getElementById('lifestyle-notes')?.value || '';

  const payload = {
    symptoms: Array.from(selectedSymptoms),
    age,
    gender,
    duration,
    severity,
    existingConditions: conditions,
    allergies,
    lifestyleFactors: lifestyle,
    saveToHistory: isLoggedIn()
  };

  try {
    const result = await PredictionAPI.evaluateAssessment(payload);
    // Store in sessionStorage for result.html
    sessionStorage.setItem('healthai_last_assessment', JSON.stringify(result));
    sessionStorage.setItem('healthai_patient_input', JSON.stringify(payload));
    showToast('Assessment completed successfully!', 'success');
    setTimeout(() => {
      window.location.href = 'result.html';
    }, 400);
  } catch (err) {
    showToast(err.message || 'Assessment evaluation failed', 'error');
    submitBtn.disabled = false;
    submitBtn.textContent = 'Complete Assessment & View Report';
  }
}
