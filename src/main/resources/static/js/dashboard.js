const API_BASE_URL = 'http://localhost:8080/api/tasks';
const AUTH_CREDENTIALS = btoa('estudante:python2024'); // Base64 encoded credentials

let allTasks = [];
let currentFilter = 'all';

const categoryColors = {
    'Basico': '#22c55e',
    'Intermediario': '#3b82f6',
    'Avancado': '#f59e0b',
    'Frameworks': '#8b5cf6',
    'DataScience': '#ec4899',
    'Automacao': '#06b6d4',
    'APIs': '#f97316',
    'Testes': '#84cc16',
    'BoasPraticas': '#6366f1'
};

const difficultyColors = {
    'Facil': '#22c55e',
    'Medio': '#f59e0b',
    'Dificil': '#ef4444',
    'Especialista': '#8b5cf6'
};

document.addEventListener('DOMContentLoaded', () => {
    loadTasks();
    setupEventListeners();
});

async function loadTasks() {
    try {
        const response = await fetch(API_BASE_URL, {
            headers: {
                'Authorization': 'Basic ' + AUTH_CREDENTIALS
            }
        });
        if (!response.ok) {
            if (response.status === 401) {
                showToast('Erro de autenticação. Verifique suas credenciais.', 'error');
                return;
            }
            throw new Error('Erro ao carregar tarefas');
        }
        allTasks = await response.json();
        updateDashboard();
    } catch (error) {
        showToast('Erro ao carregar tarefas: ' + error.message, 'error');
    }
}

function updateDashboard() {
    updateProgressCards();
    updateCharts();
    renderTasks();
}

function updateProgressCards() {
    const total = allTasks.length;
    const completed = allTasks.filter(t => t.concluida).length;
    const pending = total - completed;
    const percentage = total > 0 ? Math.round((completed / total) * 100) : 0;

    const totalTime = allTasks.reduce((sum, t) => sum + (t.tempoEstimadoMinutos || 0), 0);
    const hours = Math.floor(totalTime / 60);

    document.getElementById('totalTasks').textContent = `${total} tarefas`;
    document.getElementById('completedCount').textContent = completed;
    document.getElementById('pendingCount').textContent = pending;
    document.getElementById('totalTime').textContent = `${hours}h`;
    document.getElementById('progressText').textContent = `${completed} de ${total} tarefas concluídas`;

    document.getElementById('progressPercentage').textContent = `${percentage}%`;
    const circumference = 283;
    const offset = circumference - (percentage / 100) * circumference;
    document.getElementById('progressCircle').style.strokeDashoffset = offset;
}

function updateCharts() {
    updateCategoryChart();
    updateDifficultyChart();
}

function updateCategoryChart() {
    const categories = {};
    allTasks.forEach(task => {
        const cat = task.categoria || 'Outros';
        if (!categories[cat]) {
            categories[cat] = { total: 0, completed: 0 };
        }
        categories[cat].total++;
        if (task.concluida) categories[cat].completed++;
    });

    const chartContainer = document.getElementById('categoryChart');
    chartContainer.innerHTML = '';

    const maxCount = Math.max(...Object.values(categories).map(c => c.total), 1);

    Object.entries(categories)
        .sort((a, b) => b[1].total - a[1].total)
        .forEach(([category, data]) => {
            const percentage = (data.total / maxCount) * 100;
            const completedPercentage = data.total > 0 ? (data.completed / data.total) * 100 : 0;
            const color = categoryColors[category] || '#306998';

            const bar = document.createElement('div');
            bar.className = 'chart-bar';
            bar.innerHTML = `
                <span class="chart-bar-label">${formatCategoryName(category)}</span>
                <div class="chart-bar-track">
                    <div class="chart-bar-fill" style="width: ${percentage}%; background: linear-gradient(90deg, ${color} 0%, ${color}dd 100%);"></div>
                </div>
                <span class="chart-bar-value">${data.completed}/${data.total}</span>
            `;
            chartContainer.appendChild(bar);
        });

    if (Object.keys(categories).length === 0) {
        chartContainer.innerHTML = '<p style="color: #94a3b8; text-align: center;">Nenhuma tarefa cadastrada</p>';
    }
}

function updateDifficultyChart() {
    const difficulties = {};
    allTasks.forEach(task => {
        const diff = task.dificuldade || 'N/A';
        if (!difficulties[diff]) {
            difficulties[diff] = { total: 0, completed: 0 };
        }
        difficulties[diff].total++;
        if (task.concluida) difficulties[diff].completed++;
    });

    const chartContainer = document.getElementById('difficultyChart');
    chartContainer.innerHTML = '';

    const maxCount = Math.max(...Object.values(difficulties).map(d => d.total), 1);

    Object.entries(difficulties)
        .sort((a, b) => b[1].total - a[1].total)
        .forEach(([difficulty, data]) => {
            const percentage = (data.total / maxCount) * 100;
            const color = difficultyColors[difficulty] || '#306998';

            const bar = document.createElement('div');
            bar.className = 'chart-bar';
            bar.innerHTML = `
                <span class="chart-bar-label">${difficulty}</span>
                <div class="chart-bar-track">
                    <div class="chart-bar-fill" style="width: ${percentage}%; background: ${color};"></div>
                </div>
                <span class="chart-bar-value">${data.completed}/${data.total}</span>
            `;
            chartContainer.appendChild(bar);
        });

    if (Object.keys(difficulties).length === 0) {
        chartContainer.innerHTML = '<p style="color: #94a3b8; text-align: center;">Nenhuma tarefa cadastrada</p>';
    }
}

function formatCategoryName(category) {
    const names = {
        'Basico': 'Básico',
        'Intermediario': 'Intermediário',
        'Avancado': 'Avançado',
        'Frameworks': 'Frameworks',
        'DataScience': 'Data Science',
        'Automacao': 'Automação',
        'APIs': 'APIs/Web',
        'Testes': 'Testes',
        'BoasPraticas': 'Boas Práticas'
    };
    return names[category] || category;
}

function renderTasks() {
    const container = document.getElementById('tasksList');
    container.innerHTML = '';

    let filteredTasks = allTasks;
    if (currentFilter === 'completed') {
        filteredTasks = allTasks.filter(t => t.concluida);
    } else if (currentFilter === 'pending') {
        filteredTasks = allTasks.filter(t => !t.concluida);
    }

    filteredTasks.sort((a, b) => {
        if (a.concluida === b.concluida) {
            return new Date(b.dataCriacao) - new Date(a.dataCriacao);
        }
        return a.concluida ? 1 : -1;
    });

    if (filteredTasks.length === 0) {
        container.innerHTML = `
            <div class="empty-state">
                <i class="fas fa-clipboard-list"></i>
                <p>${currentFilter === 'all' ? 'Nenhuma tarefa cadastrada' : 'Nenhuma tarefa encontrada'}</p>
            </div>
        `;
        return;
    }

    filteredTasks.forEach(task => {
        const taskElement = createTaskElement(task);
        container.appendChild(taskElement);
    });
}

function createTaskElement(task) {
    const div = document.createElement('div');
    div.className = `task-item ${task.concluida ? 'completed' : ''}`;
    div.dataset.id = task.id;

    const minutes = task.tempoEstimadoMinutos || 0;
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    const timeText = hours > 0 ? `${hours}h ${mins}min` : `${mins}min`;

    div.innerHTML = `
        <div class="task-checkbox ${task.concluida ? 'checked' : ''}" onclick="toggleTask(${task.id})">
            ${task.concluida ? '<i class="fas fa-check"></i>' : ''}
        </div>
        <div class="task-content">
            <div class="task-title">${escapeHtml(task.titulo)}</div>
            <div class="task-meta">
                <span class="task-category">${formatCategoryName(task.categoria)}</span>
                <span class="task-difficulty ${task.dificuldade}">${task.dificuldade}</span>
                <span class="task-time"><i class="fas fa-clock"></i> ${timeText}</span>
            </div>
        </div>
        <div class="task-actions">
            <button class="btn-icon" onclick="deleteTask(${task.id})" title="Excluir">
                <i class="fas fa-trash"></i>
            </button>
        </div>
    `;

    return div;
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

async function toggleTask(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/${id}/concluir`, {
            method: 'PATCH',
            headers: {
                'Authorization': 'Basic ' + AUTH_CREDENTIALS
            }
        });

        if (response.ok) {
            const updatedTask = await response.json();
            const index = allTasks.findIndex(t => t.id === id);
            if (index !== -1) {
                allTasks[index] = updatedTask;
            }
            updateDashboard();
            showToast(updatedTask.concluida ? 'Tarefa concluída!' : 'Tarefa marcada como pendente', 'success');
        }
    } catch (error) {
        showToast('Erro ao atualizar tarefa: ' + error.message, 'error');
    }
}

async function deleteTask(id) {
    if (!confirm('Tem certeza que deseja excluir esta tarefa?')) return;

    try {
        const response = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': 'Basic ' + AUTH_CREDENTIALS
            }
        });

        if (response.ok) {
            allTasks = allTasks.filter(t => t.id !== id);
            updateDashboard();
            showToast('Tarefa excluída com sucesso!', 'success');
        }
    } catch (error) {
        showToast('Erro ao excluir tarefa: ' + error.message, 'error');
    }
}

async function createTask(event) {
    event.preventDefault();

    const formData = {
        titulo: document.getElementById('titulo').value,
        descricao: document.getElementById('descricao').value,
        categoria: document.getElementById('categoria').value,
        dificuldade: document.getElementById('dificuldade').value,
        tempoEstimadoMinutos: parseInt(document.getElementById('tempoEstimado').value)
    };

    try {
        const response = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Basic ' + AUTH_CREDENTIALS
            },
            body: JSON.stringify(formData)
        });

        if (response.ok) {
            const newTask = await response.json();
            allTasks.push(newTask);
            updateDashboard();
            document.getElementById('taskForm').reset();
            showToast('Tarefa criada com sucesso!', 'success');
        }
    } catch (error) {
        showToast('Erro ao criar tarefa: ' + error.message, 'error');
    }
}

function setupEventListeners() {
    document.getElementById('taskForm').addEventListener('submit', createTask);

    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            currentFilter = btn.dataset.filter;
            renderTasks();
        });
    });
}

function showToast(message, type = 'success') {
    const existingToast = document.querySelector('.toast');
    if (existingToast) {
        existingToast.remove();
    }

    const toast = document.createElement('div');
    toast.className = `toast ${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.animation = 'slideIn 0.3s ease reverse';
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}
