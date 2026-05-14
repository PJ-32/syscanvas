/**
 * SYSCANVAS - mi-progreso.js
 * Dashboard Personal del Analista
 */

const API = {
    CANVAS: '/sc/canvas',
    TAREAS: '/sc/tarea'
};

let canvasList = [];
let tareasList = [];
let codPersonaActual = null;

// Charts
let chartSemanal = null;
let chartDistribucion = null;

document.addEventListener('DOMContentLoaded', () => {
    inicializarVista();
});

async function inicializarVista() {
    const user = getCurrentUser();
    codPersonaActual = Number(user.codPersona);
    
    document.getElementById('saludo-usuario').textContent = 
        `Hola, ${user.nombreCompleto || 'Usuario'} 👋`;
    
    await cargarDatos();
    renderizarTodo();
}

async function cargarDatos() {
    mostrarLoading(true);
    
    await Promise.all([
        cargarCanvas(),
        cargarTareas()
    ]);
    
    mostrarLoading(false);
}

async function cargarCanvas() {
    const result = await HttpClient.get(`${API.CANVAS}?page=0&size=100`);
    
    if (result.success) {
        const todosCanvas = result.data.content || result.data;
        console.log("Canvas del backend:", todosCanvas);

        canvasList = todosCanvas.filter(c => c.codPersona === codPersonaActual);
    }
}

async function cargarTareas() {

    tareasList = [];

    // 1. Obtener todos los canvas
    const resultCanvas = await HttpClient.get(`${API.CANVAS}?page=0&size=200`);
    if (!resultCanvas.success) return;

    const todosCanvas = resultCanvas.data.content || resultCanvas.data;

    // 2. Canvas asignados al analista
    const canvasAsignados = todosCanvas.filter(c => c.codPersona == codPersonaActual);

    if (canvasAsignados.length === 0) return;

    // 3. Obtener TODAS las tareas del sistema
    const resultTareas = await HttpClient.get(`/sc/tarea?page=0&size=5000`);
    if (!resultTareas.success) return;

    const todasTareas = resultTareas.data.content || resultTareas.data;

    // 4. Filtrar tareas cuyos canvas pertenecen al analista
    const canvasIds = canvasAsignados.map(c => c.codCanvas);

    tareasList = todasTareas.filter(t => canvasIds.includes(t.codCanvas));
}


function renderizarTodo() {
    actualizarEstadisticas();
    renderizarGraficoSemanal();
    renderizarGraficoDistribucion();
    renderizarMisCanvas();
    renderizarActividadReciente();
}

function actualizarEstadisticas() {
    const completadas = tareasList.filter(t => t.vigente === 0).length;
    const pendientes = tareasList.filter(t => t.vigente === 1).length;
    const total = tareasList.length;
    const tasa = total > 0 ? ((completadas / total) * 100).toFixed(1) : 0;
    
    document.getElementById('stat-tareas-completadas').textContent = completadas;
    document.getElementById('stat-tareas-pendientes').textContent = pendientes;
    document.getElementById('stat-tasa-completitud').textContent = `${tasa}%`;
    document.getElementById('stat-canvas-activos').textContent = canvasList.length;
}

function renderizarGraficoSemanal() {
    const ctx = document.getElementById('chart-semanal');
    if (chartSemanal) chartSemanal.destroy();
    
    // Últimas 8 semanas
    const semanas = {};
    const hoy = new Date();
    
    for (let i = 7; i >= 0; i--) {
        const fecha = new Date(hoy);
        fecha.setDate(fecha.getDate() - (i * 7));
        const key = `Sem ${getWeekNumber(fecha)}`;
        semanas[key] = 0;
    }
    
    // Contar tareas completadas por semana
    tareasList.filter(t => t.vigente === 0).forEach(t => {
        const fecha = new Date(t.fecModificacion);
        const key = `Sem ${getWeekNumber(fecha)}`;
        if (semanas[key] !== undefined) {
            semanas[key]++;
        }
    });
    
    const labels = Object.keys(semanas);
    const datos = Object.values(semanas);
    
    chartSemanal = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Tareas Completadas',
                data: datos,
                borderColor: '#10B981',
                backgroundColor: 'rgba(16, 185, 129, 0.1)',
                tension: 0.4,
                fill: true
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

function renderizarGraficoDistribucion() {
    const ctx = document.getElementById('chart-distribucion');
    if (chartDistribucion) chartDistribucion.destroy();
    
    const completadas = tareasList.filter(t => t.vigente === 0).length;
    const pendientes = tareasList.filter(t => t.vigente === 1).length;
    
    chartDistribucion = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: ['Completadas', 'Pendientes'],
            datasets: [{
                data: [completadas, pendientes],
                backgroundColor: ['#10B981', '#F59E0B'],
                borderWidth: 2
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: {
                legend: {
                    position: 'bottom'
                }
            }
        }
    });
}

function renderizarMisCanvas() {
    const container = document.getElementById('mis-canvas');
    container.innerHTML = '';
    
    if (canvasList.length === 0) {
        container.innerHTML = '<p class="text-center text-gray">No tienes canvas asignados</p>';
        return;
    }
    
    canvasList.forEach(canvas => {
        const progreso = canvas.porcentajeProgreso || 0;
        
        const item = document.createElement('div');
        item.className = 'card p-3 mb-2';
        item.innerHTML = `
            <div class="flex-between">
                <div style="flex: 1;">
                    <strong>${canvas.nomCanvas}</strong>
                    <p class="text-sm text-gray mb-2">
                        ${canvas.totalTareas || 0} tareas • 
                        ${canvas.tareasCompletadas || 0} completadas
                    </p>
                    <div class="progress-bar">
                        <div class="progress-fill" style="width: ${progreso}%;"></div>
                    </div>
                </div>
                <div style="margin-left: 20px;">
                    <button class="btn btn-primary btn-sm" 
                            onclick="window.location.href='/html/canvas-detalle.html?id=${canvas.codCanvas}'">
                        👁 Ver
                    </button>
                </div>
            </div>
        `;
        container.appendChild(item);
    });
}

function renderizarActividadReciente() {
    const container = document.getElementById('actividad-reciente');
    container.innerHTML = '';
    
    // Últimas 10 tareas modificadas
    const recientes = [...tareasList]
        .sort((a, b) => new Date(b.fecModificacion) - new Date(a.fecModificacion))
        .slice(0, 10);
    
    if (recientes.length === 0) {
        container.innerHTML = '<p class="text-center text-gray">No hay actividad reciente</p>';
        return;
    }
    
    recientes.forEach(tarea => {
        const fecha = new Date(tarea.fecModificacion);
        const fechaStr = fecha.toLocaleDateString('es-ES', {
            day: '2-digit',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit'
        });
        
        const item = document.createElement('div');
        item.className = 'card p-3 mb-2';
        item.innerHTML = `
            <div class="flex-between">
                <div>
                    <strong>${tarea.nomTarea}</strong>
                    <p class="text-sm text-gray">${tarea.desTarea || 'Sin descripción'}</p>
                </div>
                <div style="text-align: right;">
                    <span class="badge ${tarea.vigente === 0 ? 'badge-success' : 'badge-warning'}">
                        ${tarea.vigente === 0 ? '✅ Completada' : '⏳ Pendiente'}
                    </span>
                    <p class="text-xs text-gray mt-1">${fechaStr}</p>
                </div>
            </div>
        `;
        container.appendChild(item);
    });
}

function getWeekNumber(date) {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    const dayNum = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() + 4 - dayNum);
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return Math.ceil((((d - yearStart) / 86400000) + 1) / 7);
}

function mostrarLoading(mostrar) {
    const loading = document.getElementById('loading');
    if (!loading) return;
    loading.classList.toggle('hidden', !mostrar);
}