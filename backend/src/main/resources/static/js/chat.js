/**
 * PersonalizedHealth AI - Conversational AI Assistant JavaScript
 * Handles multi-turn chat, speech recognition, language toggle, and adaptive questioning.
 */

let activeSessionId = null;
let currentLanguage = 'en';
let recognition = null;
let isRecording = false;

document.addEventListener('DOMContentLoaded', async () => {
  // Check auth
  if (!isLoggedIn()) {
    window.location.href = 'login.html';
    return;
  }

  const user = getUser();
  if (user && user.preferredLanguage) {
    currentLanguage = user.preferredLanguage;
    const langSelect = document.getElementById('chat-language-select');
    if (langSelect) langSelect.value = currentLanguage;
  }

  // Initialize Speech Recognition if supported
  initSpeechRecognition();

  // Create or load active session
  await initChatSession();

  // Bind input listeners
  const sendBtn = document.getElementById('chat-send-btn');
  const chatInput = document.getElementById('chat-input');
  const micBtn = document.getElementById('chat-mic-btn');
  const langSelect = document.getElementById('chat-language-select');

  sendBtn.addEventListener('click', handleSendMessage);
  chatInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      handleSendMessage();
    }
  });

  if (micBtn) {
    micBtn.addEventListener('click', toggleSpeechRecognition);
  }

  if (langSelect) {
    langSelect.addEventListener('change', async (e) => {
      currentLanguage = e.target.value;
      showToast(`Language switched to ${currentLanguage === 'hi' ? 'हिंदी (Hindi)' : 'English'}`);
      // Restart session with new language
      await startNewSession();
    });
  }
});

async function initChatSession() {
  try {
    const sessions = await ChatAPI.getSessions();
    if (sessions && sessions.length > 0) {
      activeSessionId = sessions[0].id;
      await loadSessionMessages(activeSessionId);
    } else {
      await startNewSession();
    }
  } catch (err) {
    console.warn('Could not retrieve existing sessions, starting new one:', err);
    await startNewSession();
  }
}

async function startNewSession() {
  try {
    const title = currentLanguage === 'hi' ? 'स्वास्थ्य परामर्श' : 'Clinical Health Consultation';
    const newSession = await ChatAPI.startSession(currentLanguage, title);
    activeSessionId = newSession.id;
    
    // Add default AI welcome message
    const messagesContainer = document.getElementById('chat-messages');
    messagesContainer.innerHTML = '';
    
    const welcomeText = currentLanguage === 'hi'
      ? 'नमस्ते! मैं आपका ऑराहेल्थ एआई स्वास्थ्य सहायक हूँ। कृपया अपने लक्षणों का विस्तार से वर्णन करें। (जैसे: बुखार, खांसी, या सिरदर्द कितने समय से है?)'
      : 'Hello! I am your PersonalizedHealth AI Assistant. Please describe your symptoms in detail (e.g., what symptoms you are experiencing, duration, and severity) so I can guide you.';

    appendMessage('AI', welcomeText);
  } catch (err) {
    showToast('Failed to initialize AI consultation session', 'error');
  }
}

async function loadSessionMessages(sessionId) {
  try {
    const messages = await ChatAPI.getSessionMessages(sessionId);
    const container = document.getElementById('chat-messages');
    container.innerHTML = '';

    if (!messages || messages.length === 0) {
      const welcomeText = currentLanguage === 'hi'
        ? 'नमस्ते! मैं आपका ऑराहेल्थ एआई स्वास्थ्य सहायक हूँ। कृपया अपने लक्षणों का विस्तार से वर्णन करें।'
        : 'Hello! I am your PersonalizedHealth AI Assistant. Please describe your symptoms so I can guide you through preliminary triage.';
      appendMessage('AI', welcomeText);
      return;
    }

    messages.forEach(msg => {
      appendMessage(msg.sender, msg.content);
    });
  } catch (err) {
    console.error('Error loading session messages:', err);
  }
}

function appendMessage(sender, text) {
  const container = document.getElementById('chat-messages');
  const bubble = document.createElement('div');
  const isAI = sender.toUpperCase() === 'AI' || sender.toUpperCase() === 'ASSISTANT';

  bubble.className = `message-bubble ${isAI ? 'message-ai' : 'message-user'}`;
  
  // Format line breaks
  const formatted = text.replace(/\n/g, '<br>');
  bubble.innerHTML = `
    <div style="font-size:11px; font-weight:700; text-transform:uppercase; margin-bottom:4px; opacity:0.8;">
      ${isAI ? 'PersonalizedHealth AI Assistant' : 'You (Patient)'}
    </div>
    <div>${formatted}</div>
  `;

  container.appendChild(bubble);
  container.scrollTop = container.scrollHeight;
}

async function handleSendMessage() {
  const input = document.getElementById('chat-input');
  const text = input.value.trim();
  if (!text || !activeSessionId) return;

  // Render user message immediately
  appendMessage('USER', text);
  input.value = '';

  // Show typing indicator
  const container = document.getElementById('chat-messages');
  const typingBubble = document.createElement('div');
  typingBubble.id = 'typing-indicator';
  typingBubble.className = 'message-bubble message-ai';
  typingBubble.innerHTML = `<em>PersonalizedHealth AI is analyzing your symptoms...</em>`;
  container.appendChild(typingBubble);
  container.scrollTop = container.scrollHeight;

  try {
    const response = await ChatAPI.sendMessage(activeSessionId, text);
    typingBubble.remove();

    if (response && response.response) {
      appendMessage('AI', response.response);
      
      // Update quick assessment shortcut banner if symptoms detected
      updateAssessmentBanner();
    }
  } catch (err) {
    typingBubble.remove();
    appendMessage('AI', 'I encountered an issue analyzing your input. Please ensure your backend service is running and try again.');
  }
}

function sendQuickPrompt(promptText) {
  const input = document.getElementById('chat-input');
  input.value = promptText;
  handleSendMessage();
}

function updateAssessmentBanner() {
  const banner = document.getElementById('convert-assessment-banner');
  if (banner && activeSessionId) {
    banner.style.display = 'flex';
    const link = document.getElementById('convert-assessment-link');
    if (link) {
      link.href = `assessment.html?sessionId=${activeSessionId}`;
    }
  }
}

// Web Speech API Integration
function initSpeechRecognition() {
  const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
  if (!SpeechRecognition) {
    console.log('Web Speech Recognition not supported in this browser.');
    const micBtn = document.getElementById('chat-mic-btn');
    if (micBtn) micBtn.style.display = 'none';
    return;
  }

  recognition = new SpeechRecognition();
  recognition.continuous = false;
  recognition.interimResults = false;

  recognition.onstart = () => {
    isRecording = true;
    const micBtn = document.getElementById('chat-mic-btn');
    if (micBtn) micBtn.classList.add('recording');
    showToast('Listening to your symptoms... Speak clearly.', 'info');
  };

  recognition.onresult = (event) => {
    const transcript = event.results[0][0].transcript;
    const input = document.getElementById('chat-input');
    input.value = transcript;
    showToast(`Transcribed: "${transcript}"`, 'success');
  };

  recognition.onerror = (event) => {
    console.warn('Speech recognition error:', event.error);
    isRecording = false;
    const micBtn = document.getElementById('chat-mic-btn');
    if (micBtn) micBtn.classList.remove('recording');
    showToast(`Voice dictation error: ${event.error}`, 'error');
  };

  recognition.onend = () => {
    isRecording = false;
    const micBtn = document.getElementById('chat-mic-btn');
    if (micBtn) micBtn.classList.remove('recording');
  };
}

function toggleSpeechRecognition() {
  if (!recognition) {
    showToast('Speech recognition is not supported in your browser.', 'error');
    return;
  }

  if (isRecording) {
    recognition.stop();
  } else {
    recognition.lang = currentLanguage === 'hi' ? 'hi-IN' : 'en-US';
    recognition.start();
  }
}
