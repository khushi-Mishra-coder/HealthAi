/**
 * Personalized AI Health Assistant - Unified API Client & Utilities
 */

const API_BASE = window.location.origin.includes(':8080') ? '' : 'http://localhost:8080';

const StorageKeys = {
  TOKEN: 'healthai_token',
  USER: 'healthai_user'
};

function getToken() {
  return localStorage.getItem(StorageKeys.TOKEN);
}

function setToken(token) {
  localStorage.setItem(StorageKeys.TOKEN, token);
}

function getUser() {
  const userStr = localStorage.getItem(StorageKeys.USER);
  try {
    return userStr ? JSON.parse(userStr) : null;
  } catch (e) {
    return null;
  }
}

function setUser(user) {
  localStorage.setItem(StorageKeys.USER, JSON.stringify(user));
}

function clearAuth() {
  localStorage.removeItem(StorageKeys.TOKEN);
  localStorage.removeItem(StorageKeys.USER);
}

function isLoggedIn() {
  return !!getToken();
}

async function apiFetch(endpoint, options = {}) {
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  const token = getToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const url = `${API_BASE}${endpoint}`;

  try {
    const response = await fetch(url, {
      ...options,
      headers
    });

    if (response.status === 401) {
      // Clear expired credentials
      clearAuth();
      if (!window.location.pathname.endsWith('login.html') && !window.location.pathname.endsWith('register.html') && !window.location.pathname.endsWith('index.html')) {
        window.location.href = 'login.html?expired=true';
      }
      throw new Error('Authentication expired. Please log in again.');
    }

    const data = await response.json().catch(() => null);

    if (!response.ok) {
      const errorMsg = data?.message || data?.error || `Request failed with status ${response.status}`;
      throw new Error(errorMsg);
    }

    return data;
  } catch (err) {
    console.error(`API Error [${endpoint}]:`, err);
    throw err;
  }
}

// Authentication API
const AuthAPI = {
  async login(email, password) {
    const res = await apiFetch('/api/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password })
    });
    if (res.token) {
      setToken(res.token);
      if (res.user) setUser(res.user);
    }
    return res;
  },

  async register(data) {
    const res = await apiFetch('/api/auth/register', {
      method: 'POST',
      body: JSON.stringify(data)
    });
    if (res.token) {
      setToken(res.token);
      if (res.user) setUser(res.user);
    }
    return res;
  },

  async getMe() {
    const user = await apiFetch('/api/auth/me');
    if (user) setUser(user);
    return user;
  },

  logout() {
    clearAuth();
    window.location.href = 'index.html';
  }
};

// Chat & AI Assistant API
const ChatAPI = {
  async startSession(language = 'en', title = 'Health Assessment Consultation') {
    return await apiFetch('/api/chat/sessions', {
      method: 'POST',
      body: JSON.stringify({ language, title })
    });
  },

  async getSessions() {
    return await apiFetch('/api/chat/sessions');
  },

  async getSessionMessages(sessionId) {
    return await apiFetch(`/api/chat/sessions/${sessionId}/messages`);
  },

  async sendMessage(sessionId, messageText) {
    return await apiFetch('/api/ai/chat', {
      method: 'POST',
      body: JSON.stringify({ sessionId, message: messageText })
    });
  },

  async getSessionState(sessionId) {
    return await apiFetch(`/api/ai/sessions/${sessionId}/state`);
  }
};

// Prediction & Assessment API
const PredictionAPI = {
  async getStatus() {
    return await apiFetch('/api/prediction/status');
  },

  async getSpecialties() {
    return await apiFetch('/api/prediction/specialties');
  },

  async predict(symptoms, age, gender) {
    return await apiFetch('/api/prediction/predict', {
      method: 'POST',
      body: JSON.stringify({ symptoms, age, gender })
    });
  },

  async evaluateAssessment(assessmentData) {
    return await apiFetch('/api/assessment/evaluate', {
      method: 'POST',
      body: JSON.stringify(assessmentData)
    });
  }
};

// Patient History API
const HistoryAPI = {
  async getAll() {
    return await apiFetch('/api/history');
  },

  async getById(id) {
    return await apiFetch(`/api/history/${id}`);
  },

  async recordSearch(id) {
    return await apiFetch(`/api/history/${id}/searched`, { method: 'POST' });
  },

  async create(data) {
    return await apiFetch('/api/history', {
      method: 'POST',
      body: JSON.stringify(data)
    });
  }
};

// Healthcare Facilities & Doctor Finder API
const HealthcareAPI = {
  async getFacilities(params = {}) {
    const query = new URLSearchParams(params).toString();
    return await apiFetch(`/api/healthcare/facilities?${query}`);
  },

  async getSpecialties() {
    return await apiFetch('/api/healthcare/specialties');
  },

  async getFacility(id) {
    return await apiFetch(`/api/healthcare/facilities/${id}`);
  }
};


// UI Toast Notification helper
function showToast(message, type = 'info') {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.innerHTML = `
    <span>${message}</span>
    <button onclick="this.parentElement.remove()" style="background:none;border:none;color:#fff;cursor:pointer;font-size:16px;">&times;</button>
  `;

  container.appendChild(toast);
  setTimeout(() => {
    toast.style.opacity = '0';
    setTimeout(() => toast.remove(), 300);
  }, 4000);
}

// Dynamic Header Navigation Renderer
function renderHeaderAuth() {
  const user = getUser();
  const navRight = document.querySelector('.nav-right');
  if (!navRight) return;

  if (isLoggedIn() && user) {
    const initials = (user.fullName || user.email || 'U').substring(0, 2).toUpperCase();
    navRight.innerHTML = `
      <div class="user-badge" title="${user.email}">
        <div class="user-avatar">${initials}</div>
        <span>${user.fullName || user.email}</span>
      </div>
      <a href="dashboard.html" class="btn btn-outline btn-sm">Dashboard</a>
      <button onclick="AuthAPI.logout()" class="btn btn-outline btn-sm">Log Out</button>
    `;
  } else {
    navRight.innerHTML = `
      <a href="login.html" class="btn btn-outline btn-sm">Sign In</a>
      <a href="register.html" class="btn btn-primary btn-sm">Register Free</a>
    `;
  }
}

document.addEventListener('DOMContentLoaded', () => {
  renderHeaderAuth();
});
