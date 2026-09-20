/**
 * PersonalizedHealth AI - Health Assessment History & Timeline
 */

let allHistories = [];

document.addEventListener('DOMContentLoaded', async () => {
  if (!isLoggedIn()) {
    window.location.href = 'login.html';
    return;
  }

  await loadHistories();

  // Search and filter listeners
  const searchInput = document.getElementById('history-search-input');
  const filterSelect = document.getElementById('history-filter-select');

  if (searchInput) searchInput.addEventListener('input', applyFilters);
  if (filterSelect) filterSelect.addEventListener('change', applyFilters);

  // Check if query param ?id=... was passed to automatically open modal
  const urlParams = new URLSearchParams(window.location.search);
  const targetId = urlParams.get('id');
  if (targetId) {
    setTimeout(() => openHistoryDetail(parseInt(targetId, 10)), 300);
  }
});

async function loadHistories() {
  const container = document.getElementById('history-timeline-container');
  try {
    allHistories = await HistoryAPI.getAll();
    renderHistories(allHistories);
  } catch (err) {
    container.innerHTML = `
      <div style="text-align:center; padding:40px; color:var(--text-muted);">
        Failed to load health history. Please ensure backend is running.
      </div>
    `;
  }
}

function applyFilters() {
  const term = document.getElementById('history-search-input').value.toLowerCase().trim();
  const filter = document.getElementById('history-filter-select').value;

  const filtered = allHistories.filter(h => {
    const matchTerm = !term ||
      (h.symptoms && h.symptoms.toLowerCase().includes(term)) ||
      (h.riskAssessmentResult && h.riskAssessmentResult.toLowerCase().includes(term)) ||
      (h.suggestedSpecialty && h.suggestedSpecialty.toLowerCase().includes(term));

    let matchFilter = true;
    if (filter === 'emergency') {
      matchFilter = (h.suggestedSpecialty && h.suggestedSpecialty.toLowerCase().includes('emergency')) ||
                    (h.riskAssessmentResult && h.riskAssessmentResult.toLowerCase().includes('infarction'));
    } else if (filter === 'frequent') {
      matchFilter = (h.searchesCount || 0) > 1;
    }

    return matchTerm && matchFilter;
  });

  renderHistories(filtered);
}

function renderHistories(histories) {
  const container = document.getElementById('history-timeline-container');
  if (!histories || histories.length === 0) {
    container.innerHTML = `
      <div class="card" style="text-align:center; padding:48px 20px; color:var(--text-muted);">
        <p style="font-size:16px; margin-bottom:12px;">No assessment records match your criteria.</p>
        <a href="assessment.html" class="btn btn-primary btn-sm">Start New Symptom Assessment</a>
      </div>
    `;
    return;
  }

  container.innerHTML = histories.map(h => {
    const dateStr = h.assessmentDate
      ? new Date(h.assessmentDate).toLocaleDateString(undefined, { weekday: 'short', month: 'short', day: 'numeric', year: 'numeric', hour: '2-digit', minute: '2-digit' })
      : 'Previous Assessment';

    const isUrgent = (h.suggestedSpecialty || '').toLowerCase().includes('emergency') || (h.riskAssessmentResult || '').toLowerCase().includes('infarction');
    const badgeClass = isUrgent ? 'badge-urgent' : 'badge-teal';

    return `
      <div class="card card-hover" style="margin-bottom:18px; border-left: 5px solid ${isUrgent ? 'var(--urgent-red)' : 'var(--primary)'};">
        <div style="display:flex; justify-content:space-between; align-items:flex-start; flex-wrap:wrap; gap:12px;">
          <div>
            <div style="font-size:12px; color:var(--text-subtle); margin-bottom:4px;">${dateStr}</div>
            <h3 style="font-size:18px; color:var(--text-main); margin-bottom:6px;">${h.riskAssessmentResult || 'Health Check'}</h3>
            <div style="font-size:13.5px; color:var(--text-muted); margin-bottom:10px;">
              Reported Symptoms: <strong>${h.symptoms}</strong> &bull; Duration: <em>${h.duration || 'N/A'}</em>
            </div>
            <div style="display:flex; gap:8px; flex-wrap:wrap;">
              <span class="badge ${badgeClass}">${h.suggestedSpecialty || 'General Physician'}</span>
              <span class="badge" style="background:var(--bg-subtle); color:var(--text-muted);">Reviewed ${h.searchesCount || 0} times</span>
            </div>
          </div>
          <div>
            <button onclick="openHistoryDetail(${h.id})" class="btn btn-outline btn-sm">View Clinical Notes &rarr;</button>
          </div>
        </div>
      </div>
    `;
  }).join('');
}

async function openHistoryDetail(id) {
  const item = allHistories.find(h => h.id === id);
  if (!item) return;

  // Increment search count via API
  HistoryAPI.recordSearch(id).catch(console.warn);
  item.searchesCount = (item.searchesCount || 0) + 1;

  const modal = document.getElementById('history-modal');
  const modalBody = document.getElementById('history-modal-body');

  modalBody.innerHTML = `
    <div style="margin-bottom:16px;">
      <span class="badge badge-teal" style="margin-bottom:8px;">Assessment #${item.id}</span>
      <h2 style="font-size:22px; margin-bottom:6px;">${item.riskAssessmentResult}</h2>
      <div style="font-size:12.5px; color:var(--text-muted);">Date: ${new Date(item.assessmentDate).toLocaleString()}</div>
    </div>

    <div style="padding:14px; background:var(--bg-subtle); border-radius:var(--radius-btn); margin-bottom:16px;">
      <div style="font-size:12px; font-weight:700; color:var(--text-muted); text-transform:uppercase;">Symptoms Profile</div>
      <div style="font-size:14px; font-weight:600; color:var(--text-main); margin-top:4px;">${item.symptoms}</div>
      <div style="font-size:12.5px; color:var(--text-muted); margin-top:4px;">Duration: ${item.duration} &bull; Severity: ${item.severity}</div>
    </div>

    <div style="margin-bottom:16px;">
      <div style="font-size:13px; font-weight:700; color:var(--text-main); margin-bottom:4px;">Recommended Navigation & Action</div>
      <div style="padding:12px; background:#f0fdfa; border-left:3px solid var(--primary); border-radius:var(--radius-btn); font-size:13.5px;">
        ${item.suggestedAction || 'Consult specialist for clinical evaluation.'}
      </div>
    </div>

    ${item.conversationSummary ? `
      <div style="margin-bottom:16px;">
        <div style="font-size:13px; font-weight:700; color:var(--text-main); margin-bottom:4px;">Clinical Summary</div>
        <div style="font-size:13.5px; color:var(--text-body); line-height:1.6; white-space:pre-line;">
          ${item.conversationSummary}
        </div>
      </div>
    ` : ''}

    <div style="display:flex; justify-content:flex-end; gap:10px; margin-top:24px; padding-top:16px; border-top:1px solid var(--border-subtle);">
      <a href="nearby-healthcare.html?specialty=${encodeURIComponent(item.suggestedSpecialty || 'General Physician')}" class="btn btn-primary btn-sm">Find Nearby ${item.suggestedSpecialty || 'Doctor'}</a>
      <button onclick="closeHistoryModal()" class="btn btn-outline btn-sm">Close</button>
    </div>
  `;

  modal.style.display = 'flex';
}

function closeHistoryModal() {
  const modal = document.getElementById('history-modal');
  if (modal) modal.style.display = 'none';
}
