/**
 * ================================
 * SYSCANVAS - Panel Analista (Optimizado Final)
 * Fusión basada en tu código + código mejorado
 * ================================
 */

const API = {
    CANVAS: '/sc/canvas',
    TAREAS: '/sc/tarea',
    PROYECTOS: '/t/proyecto/activos'
};

// Estado Global
let canvasList = [];
let todasLasTareas = [];
let proyectosList = [];
let codPersonaActual = null;

// ================================
// INICIALIZACIÓN
// ================================
document.addEventListener('DOMContentLoaded', async () => {
    const user = getCurrentUser();
    codPersonaActual = Number(user.codPersona);

    console.log("Analista actual (cargado primero):", codPersonaActual);

    actualizarFechaSaludo();
    setInterval(actualizarFechaSaludo, 60000);

    await cargarCanvas(); // cargar canvas primero
    await cargarProyectos();
    await cargarTodasLasTareas();

    actualizarEstadisticas();
    //Perfil
    cargarFotoTopbar();

});

// ================================
// CARGA DE DATOS
// ================================
async function cargarDatosIniciales() {
    mostrarLoading(true);

    await Promise.all([
        cargarCanvas(),
        cargarProyectos(),
        cargarTodasLasTareas()
    ]);
    
    mostrarLoading(false);
}

/**
 * Cargar CANVAS asignados al analista
 */
async function cargarCanvas() {
    const result = await HttpClient.get(`${API.CANVAS}?page=0&size=100`);
    console.log("Analista actual:", codPersonaActual);

    console.log("RESULTADO BACKEND:", result);   // LOG 1

    if (!result.success) {
        AlertManager.error("Error al cargar canvas");
        return;
    }

    const todosCanvas = result.data.content || result.data;
    todosCanvas.forEach(c => console.log(typeof c.codPersona, c.codPersona));
    console.log("Canvas completos:", todosCanvas);

    canvasList = todosCanvas.filter(c => c.codPersona === codPersonaActual);

    console.log("CANVAS DEL ANALISTA:", canvasList); // LOG 3

    llenarSelectCanvas();
    aplicarFiltros();
}


/**
 * Cargar proyectos para mostrar en cards
 */
async function cargarProyectos() {
    const result = await HttpClient.get(API.PROYECTOS);
    if (result.success) {
        proyectosList = result.data.content || result.data;
    }
}

/**
 * Cargar todas las tareas del analista
 */
async function cargarTodasLasTareas() {
    const result = await HttpClient.get(`${API.TAREAS}/persona/${codPersonaActual}?page=0&size=1000`);
    if (result.success) {
        todasLasTareas = result.data.content || result.data;
    }
}

// ================================
// SELECT PROYECTOS
// ================================
function llenarSelectProyectos() {
    const select = document.getElementById('filter-proyecto');
    if (!select) return;

    select.innerHTML = '<option value="">Todos los proyectos</option>';

    const proyectosUnicos = [...new Set(canvasList.map(c => c.codPyto))].filter(Boolean);

    proyectosUnicos.forEach(codPyto => {
        const proyecto = proyectosList.find(p => p.codPyto === codPyto);
        if (proyecto) {
            const option = document.createElement('option');
            option.value = proyecto.codPyto;
            option.textContent = proyecto.nomPyto;
            select.appendChild(option);
        }
    });
}

// ================================
// SELECT CANVAS
// ================================
function llenarSelectCanvas() {
    const select = document.getElementById('filter-canvas');
    if (!select) return;

    select.innerHTML = '<option value="">Todos los canvas</option>';

    canvasList.forEach(canvas => {
        const option = document.createElement('option');
        option.value = canvas.nomCanvas;
        option.textContent = canvas.nomCanvas;
        select.appendChild(option);
    });
}

// ================================
// FILTROS (COMBINADO Y OPTIMIZADO)
// ================================
function aplicarFiltros() {
    const filtroProyecto = document.getElementById('filter-proyecto')?.value || '';
    const filtroCanvas = document.getElementById('filter-canvas')?.value || '';
    const filtroEstado = document.getElementById('filter-estado')?.value || '';

    let lista = [...canvasList];

    if (filtroProyecto) {
        lista = lista.filter(c => c.codPyto == filtroProyecto);
    }

    if (filtroEstado !== '') {
        const editable = filtroEstado === 'true';
        lista = lista.filter(c => c.editable === editable);
    }

    if (filtroCanvas) {
        lista = lista.filter(c =>
            c.nomCanvas.toLowerCase().includes(filtroCanvas.toLowerCase())
        );
    }

    renderizarCanvas(lista);
}

// ================================
// RENDER CARDS
// ================================
function renderizarCanvas(lista) {
    const container = document.getElementById('canvas-list');
    const noResults = document.getElementById('no-results');

    container.innerHTML = '';

    if (lista.length === 0) {
        noResults.classList.remove('hidden');
        return;
    }

    noResults.classList.add('hidden');

    lista.forEach(canvas => {
        container.appendChild(crearCanvasCard(canvas));
    });
}

function crearCanvasCard(canvas) {
    const template = document.getElementById('canvas-card-template');
    const clone = template.content.cloneNode(true);

    const card = document.createElement('div');
    card.className = 'canvas-card';
    card.appendChild(clone);

    // Nombre
    card.querySelector('.canvas-card-title').textContent = canvas.nomCanvas;

    // Proyecto
    const proyecto = proyectosList.find(p => p.codPyto === canvas.codPyto);
    card.querySelector('.canvas-card-meta').textContent =
        `📁 ${proyecto ? proyecto.nomPyto : 'Sin proyecto'}`;

    // Descripción
    card.querySelector('.canvas-descripcion').textContent = canvas.desCanvas || 'Sin descripción';

    // Badge
    const badge = card.querySelector('.badge');
    if (canvas.editable) {
        badge.className = 'badge badge-success';
        badge.textContent = '✏️ Editable';
    } else {
        badge.className = 'badge badge-error';
        badge.textContent = '🔒 Bloqueado';
    }

    // Progreso
    const progreso = Math.round(canvas.porcentajeProgreso || 0);
    card.querySelector('.progreso-texto').textContent = progreso;
    card.querySelector('.progress-fill').style.width = `${progreso}%`;

    // Tareas
    card.querySelector('.total-tareas').textContent = canvas.totalTareas || 0;
    card.querySelector('.tareas-completadas').textContent = canvas.tareasCompletadas || 0;

    // Botón Ver
    card.querySelector('.btn-ver').onclick = () => verCanvas(canvas.codCanvas);

    return card;
}

// ================================
// NAVEGAR A DETALLE
// ================================
function verCanvas(codCanvas) {
    window.location.href = `/html/canvas-detalle.html?id=${codCanvas}`;
}
// ==========================================
// CREACIÓN DE MODALES
// ==========================================
function toggleProfileMenu(evt) {
    const eventObj = evt || window.event;
    const menu = document.getElementById('profile-menu');
    const userInfo = document.querySelector('.user-info');
    const chevron = document.getElementById('user-info-toggle-chevron-down');

    if (!menu || !chevron || !userInfo) return;

    try {
        const user = getCurrentUser();
        const nombre = user.nombreCompleto || 'Usuario';

        const menuName = menu.querySelector('.profile-name');
        if (menuName) menuName.textContent = nombre;
    } catch (e) {
        console.debug('No se pudo obtener usuario en toggleProfileMenu', e);
    }

    const isOpen = window.__profileMenuOpen === true;
    if(isOpen) {
        //Cerrar
        menu.style.display = "none";
        chevron.classList.remove("fa-chevron-up");
        chevron.classList.add("fa-chevron-down");
        window.__profileMenuOpen = false;
    }
    else {
        //Abrir
        menu.style.display = "block";
        chevron.classList.remove("fa-chevron-down");
        chevron.classList.add("fa-chevron-up");
        window.__profileMenuOpen = true;
    }

    if (eventObj && eventObj.stopPropagation) eventObj.stopPropagation();
}

// ================================
// ESTADÍSTICAS FINAL (IMPECABLE)
// ================================
function actualizarEstadisticas() {
    let totalTareas = 0;
    let completadas = 0;

    canvasList.forEach(c => {
        totalTareas += c.totalTareas || 0;
        completadas += c.tareasCompletadas || 0;
    });

    const pendientes = totalTareas - completadas;
    const progresoGeneral = totalTareas > 0 ? Math.round((completadas / totalTareas) * 100) : 0;

    document.getElementById('stat-canvas-asignados').textContent = canvasList.length;
    document.getElementById('stat-tareas-pendientes').textContent = pendientes;
    document.getElementById('stat-tareas-completadas').textContent = completadas;
    document.getElementById('stat-progreso-general').textContent = `${progresoGeneral}%`;
}

// ================================
// UTILIDADES
// ================================
function mostrarLoading(show) {
    const el = document.getElementById('loading');
    if (el) el.classList.toggle('hidden', !show);
}

function actualizarFechaSaludo() {
    const ahora = new Date();
    const fechaEl = document.getElementById('fecha-actual');
    const saludoEl = document.getElementById('saludo-usuario');

    if (fechaEl) {
        const opciones = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        let fechaTexto = ahora.toLocaleDateString('es-ES', opciones);
        fechaEl.textContent = fechaTexto.charAt(0).toUpperCase() + fechaTexto.slice(1);
    }

    if (saludoEl) {
        const hora = ahora.getHours();
        const saludo = hora < 12 ? 'Buenos días' : hora < 19 ? 'Buenas tardes' : 'Buenas noches';
        const user = getCurrentUser();
        saludoEl.textContent = `${saludo}, ${user.nombreCompleto} 👋`;
    }
}



// Foto de perfil
async function cargarFotoTopbar() {
    const user = getCurrentUser();
    if (!user || !user.codPersona) return;

    try {
        const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
            headers: {
                "Authorization": `Bearer ${localStorage.getItem("token")}`
            }
        });

        const fotoTop = document.getElementById("user-photo-topbar");
        const fotoMenu = document.getElementById("profile-menu-photo");

        if (!res.ok) {
            console.warn("Foto no encontrada, usando default.");
            if (fotoTop) fotoTop.src = "/uploads/default-foto.png";
            if (fotoMenu) fotoMenu.src = "/uploads/default-foto.png";
            return;
        }

        const data = await res.json();

        if (data.fotoBase64 && data.fotoBase64.trim() !== "") {

            const img64 = `data:image/png;base64,${data.fotoBase64}`;

            if (fotoTop) fotoTop.src = img64;
            if (fotoMenu) fotoMenu.src = img64;

        } else {
            if (fotoTop) fotoTop.src = "/uploads/default-foto.png";
            if (fotoMenu) fotoMenu.src = "/uploads/default-foto.png";
        }

    } catch (err) {
        console.error("Error cargando foto del topbar:", err);
        const fotoTop = document.getElementById("user-photo-topbar");
        const fotoMenu = document.getElementById("profile-menu-photo");

        if (fotoTop) fotoTop.src = "/uploads/default-foto.png";
        if (fotoMenu) fotoMenu.src = "/uploads/default-foto.png";
    }
}
