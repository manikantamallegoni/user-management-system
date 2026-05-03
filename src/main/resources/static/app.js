const API_URL = '/api/users';
let currentUser = null;

// DOM Elements
const views = {
    login: document.getElementById('login-view'),
    register: document.getElementById('register-view'),
    dashboard: document.getElementById('dashboard-view')
};

// --- Initialization ---
document.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    checkSession();
});

function setupEventListeners() {
    // Navigation
    document.getElementById('show-register').onclick = (e) => { e.preventDefault(); switchView('register'); };
    document.getElementById('show-login').onclick = (e) => { e.preventDefault(); switchView('login'); };
    document.getElementById('logout-btn').onclick = logout;
    document.getElementById('forgot-password-trigger').onclick = (e) => { 
        e.preventDefault(); 
        showToast('Password reset link sent to your email (Demo)', 'success');
    };

    // Forms
    document.getElementById('login-form').onsubmit = handleLogin;
    document.getElementById('register-form').onsubmit = handleRegister;
    document.getElementById('user-form').onsubmit = handleSaveUser;

    // Dashboard Actions
    document.getElementById('add-user-btn').onclick = () => openUserModal();
    
    // Modal Close
    document.querySelectorAll('.close-modal').forEach(btn => {
        btn.onclick = () => document.getElementById('user-modal').classList.remove('active');
    });
}

// --- View Management ---
function switchView(viewName) {
    Object.values(views).forEach(v => {
        v.classList.remove('active');
        v.style.display = 'none';
    });
    
    const target = views[viewName];
    target.style.display = viewName === 'dashboard' ? 'flex' : 'flex'; // maintain flex
    setTimeout(() => target.classList.add('active'), 50);
    
    if (viewName === 'dashboard') loadUsers();
}

// --- Auth Operations ---
async function handleLogin(e) {
    e.preventDefault();
    clearErrors();
    
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;

    try {
        const response = await fetch(`${API_URL}/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            currentUser = data;
            localStorage.setItem('management_user', JSON.stringify(data));
            document.getElementById('current-user-name').textContent = `${data.firstName} ${data.lastName}`;
            showToast(`Welcome back, ${data.firstName}!`, 'success');
            switchView('dashboard');
        } else {
            if (response.status === 401) {
                showError('login-password', 'Invalid credentials');
            } else if (data.message) {
                showToast(data.message, 'error');
            }
        }
    } catch (err) {
        showToast('Server connection failed', 'error');
    }
}

async function handleRegister(e) {
    e.preventDefault();
    clearErrors();

    const payload = {
        firstName: document.getElementById('reg-firstname').value,
        lastName: document.getElementById('reg-lastname').value,
        email: document.getElementById('reg-email').value,
        password: document.getElementById('reg-password').value
    };

    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            showToast('Account created successfully!', 'success');
            switchView('login');
            document.getElementById('register-form').reset();
        } else {
            handleValidationErrors(data, 'reg-');
        }
    } catch (err) {
        showToast('Registration failed', 'error');
    }
}

function logout() {
    currentUser = null;
    localStorage.removeItem('management_user');
    switchView('login');
}

function checkSession() {
    const savedUser = localStorage.getItem('management_user');
    if (savedUser) {
        currentUser = JSON.parse(savedUser);
        document.getElementById('current-user-name').textContent = `${currentUser.firstName} ${currentUser.lastName}`;
        switchView('dashboard');
    } else {
        switchView('login');
    }
}

// --- Dashboard Operations ---
async function loadUsers() {
    try {
        const response = await fetch(API_URL);
        const users = await response.json();
        renderUsers(users);
    } catch (err) {
        showToast('Failed to load users', 'error');
    }
}

function renderUsers(users) {
    const tbody = document.getElementById('users-body');
    const emptyState = document.getElementById('empty-state');
    tbody.innerHTML = '';

    if (users.length === 0) {
        emptyState.classList.remove('hidden');
    } else {
        emptyState.classList.add('hidden');
        users.forEach(user => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>
                    <div style="font-weight: 500">${user.firstName} ${user.lastName}</div>
                </td>
                <td style="color: var(--text-muted)">${user.email}</td>
                <td><code style="color: var(--primary)">#${user.id}</code></td>
                <td>
                    <div class="action-btns">
                        <button onclick="editUser(${user.id})" class="btn-icon" title="Edit">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path></svg>
                        </button>
                        <button onclick="deleteUser(${user.id})" class="btn-icon btn-delete" title="Delete">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="3 6 5 6 21 6"></polyline><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path><line x1="10" y1="11" x2="10" y2="17"></line><line x1="14" y1="11" x2="14" y2="17"></line></svg>
                        </button>
                    </div>
                </td>
            `;
            tbody.appendChild(tr);
        });
    }
}

async function handleSaveUser(e) {
    e.preventDefault();
    clearErrors();

    const id = document.getElementById('user-id').value;
    const payload = {
        firstName: document.getElementById('user-firstname').value,
        lastName: document.getElementById('user-lastname').value,
        email: document.getElementById('user-email').value,
        password: document.getElementById('user-password').value || undefined
    };

    const method = id ? 'PUT' : 'POST';
    const url = id ? `${API_URL}/${id}` : API_URL;

    try {
        const response = await fetch(url, {
            method: method,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.ok) {
            showToast(id ? 'User updated' : 'User created', 'success');
            document.getElementById('user-modal').classList.remove('active');
            loadUsers();
        } else {
            handleValidationErrors(data, 'user-');
        }
    } catch (err) {
        showToast('Operation failed', 'error');
    }
}

async function editUser(id) {
    try {
        const response = await fetch(`${API_URL}/${id}`);
        const user = await response.json();
        openUserModal(user);
    } catch (err) {
        showToast('Failed to fetch user details', 'error');
    }
}

async function deleteUser(id) {
    if (!confirm('Are you sure you want to delete this user?')) return;

    try {
        const response = await fetch(`${API_URL}/${id}`, { method: 'DELETE' });
        if (response.ok) {
            showToast('User deleted successfully', 'success');
            loadUsers();
        } else {
            showToast('Deletion failed', 'error');
        }
    } catch (err) {
        showToast('Network error', 'error');
    }
}

// --- Helper Functions ---
function openUserModal(user = null) {
    const modal = document.getElementById('user-modal');
    const form = document.getElementById('user-form');
    const title = document.getElementById('modal-title');
    
    form.reset();
    clearErrors();
    
    if (user) {
        title.textContent = 'Edit User';
        document.getElementById('user-id').value = user.id;
        document.getElementById('user-firstname').value = user.firstName;
        document.getElementById('user-lastname').value = user.lastName;
        document.getElementById('user-email').value = user.email;
    } else {
        title.textContent = 'Add New User';
        document.getElementById('user-id').value = '';
    }
    
    modal.classList.add('active');
}

function handleValidationErrors(errors, prefix) {
    if (typeof errors === 'object' && !Array.isArray(errors)) {
        Object.keys(errors).forEach(field => {
            // Backend fields: firstName, lastName, email, password
            // Frontend ID pattern: prefix + lowercase field
            const elementId = prefix + field.toLowerCase();
            showError(elementId, errors[field]);
        });
    } else {
        showToast('Invalid data provided', 'error');
    }
}

function showError(id, msg) {
    const input = document.getElementById(id);
    const errorSpan = document.getElementById(id + '-error');
    if (input) input.style.borderColor = 'var(--danger)';
    if (errorSpan) errorSpan.textContent = msg;
}

function clearErrors() {
    document.querySelectorAll('.error-msg').forEach(el => el.textContent = '');
    document.querySelectorAll('input').forEach(el => el.style.borderColor = '');
}

function showToast(message, type = 'info') {
    const container = document.getElementById('toast-container');
    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    
    container.appendChild(toast);
    
    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(20px)';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}
