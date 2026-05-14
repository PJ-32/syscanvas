/*
 * SYSCANVAS - jefe.js
 * Lógica del panel de jefe de proyecto
 */

// Configuración
const API = {
    CANVAS: '/sc/canvas',
    PROYECTOS: '/t/proyecto',
    TIPO_CANVAS: '/sc/tipo-canvas',
    ETAPAS: '/sc/etapa',
    HISTORIAL: '/sc/historial'
};

// Estado global
let canvasList = [];
let proyectosList = [];
let tiposCanvasList = [];
let empleadosList = [];
let modoCreacion = null;
let canvasEditando = null;
let canvasAsignando = null;

// Generar íconos
function iconify(name, size = 18) {
    return `<span class="iconify" data-icon="${name}" style="font-size:${size}px;"></span>`;
}

// ==========================================
// CARGAR DATOS INICIALES
// ==========================================

async function cargarDatosIniciales() {
    await Promise.all([
        cargarCanvas(),
        cargarProyectos(),
        cargarTiposCanvas(),
        cargarEmpleados()
    ]);
    actualizarEstadisticas();
    //Perfil
    cargarFotoTopbar();
}

/*
 * Cargar lista de canvas
 */
async function cargarCanvas() {
    mostrarLoading(true);
    
    const result = await HttpClient.get(API.CANVAS);
    
    if (result.success) {
        canvasList = result.data.content || result.data;
        aplicarFiltros();
        actualizarEstadisticas();
    } else {
        AlertManager.error('Error al cargar canvas: ' + result.error);
    }
    
    mostrarLoading(false);
}

/*
 * Cargar lista de proyectos
 */
async function cargarProyectos() {
    const result = await HttpClient.get(API.PROYECTOS);
    
    if (result.success) {
        proyectosList = result.data.content;
        llenarSelectProyectos();
    }
}

/*
 * Cargar tipos de canvas (plantillas)
 */
async function cargarTiposCanvas() {
    const result = await HttpClient.get(API.TIPO_CANVAS);
    
    if (result.success) {
        tiposCanvasList = result.data.filter(tipo => tipo.vigente === '1' || tipo.vigente === 1);
        llenarSelectTiposCanvas();
    }
}

/*Cargar empleados*/
async function cargarEmpleados() {
    const result = await HttpClient.get('/t/empleado/activos');
    
    if (result.success) {
        empleadosList = result.data.content || result.data;
    }

    // Llenar select del modal EDITAR
    const selectEditar = document.getElementById("editar-analista");
    if (selectEditar) {
        while (selectEditar.options.length > 1) selectEditar.remove(1);

        empleadosList.forEach(e => {
            const op = document.createElement("option");
            op.value = e.codPersona;
            op.textContent = `${e.nombre} ${e.apellido}`;
            selectEditar.appendChild(op);
        });
    }

    // Llenar select del modal CREAR
    llenarSelectAnalistas();
}

/*
 * Llenar select de proyectos
 */
function llenarSelectProyectos() {
    const selectFilter = document.getElementById('filter-proyecto');
    const selectCanvas = document.getElementById('proyecto-canvas');
    
    [selectFilter, selectCanvas].forEach(select => {
        if (!select) return;

        while (select.options.length > 1) {
            select.remove(1);
        }

        proyectosList.forEach(proyecto => {
            if (proyecto.vigente === 1) {
                const option = document.createElement('option');
                option.value = proyecto.codPyto;
                option.textContent = proyecto.nomPyto;
                select.appendChild(option);
            }
        });
    });

    const selectEditar = document.getElementById('editar-proyecto');
    if (selectEditar) {
        while (selectEditar.options.length > 1) selectEditar.remove(1);

        proyectosList.forEach(proyecto => {
            if (proyecto.vigente === 1) {
                const op = document.createElement("option");
                op.value = proyecto.codPyto;
                op.textContent = proyecto.nomPyto;
                selectEditar.appendChild(op);
            }
        });
    }
}

function llenarSelectAnalistas() {
    const select = document.getElementById("analista-canvas");
    if (!select) return;

    while (select.options.length > 1) {
        select.remove(1);
    }

    empleadosList.forEach(e => {
        const op = document.createElement("option");
        op.value = e.codPersona;
        op.textContent = `${e.nombre} ${e.apellido}`;
        select.appendChild(op);
    });
}

/*
 * Llenar select de tipos de canvas
 */
function llenarSelectTiposCanvas() {
    const select = document.getElementById('tipo-canvas');
    if (!select) return;
    
    // Limpiar opciones existentes
    while (select.options.length > 1) {
        select.remove(1);
    }
    
    // Agregar tipos
    tiposCanvasList
        .filter(tipo => tipo.tipCanvas !== "F")  // EXCLUIR CANVAS LIBRE
        .forEach(tipo => {
        const option = document.createElement('option');
        option.value = tipo.tipCanvas;
        option.textContent = tipo.desTipCanvas;
        select.appendChild(option);
    });
}

/*
 * Actualizar fecha actual y saludo según hora del día
 */
function actualizarFechaSaludo() {
    const ahora = new Date();
    const fechaEl = document.getElementById('fecha-actual');
    const saludoEl = document.getElementById('saludo-usuario');
    
    // Actualizar fecha
    if (fechaEl) {
        const opciones = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
        const fechaTexto = ahora.toLocaleDateString('es-ES', opciones);
        // Capitalizar primera letra
        fechaEl.textContent = fechaTexto.charAt(0).toUpperCase() + fechaTexto.slice(1);
    }
    
    // Actualizar saludo según hora
    if (saludoEl) {
        const hora = ahora.getHours();
        let saludo = 'Buenos días';
        
        if (hora >= 12 && hora < 19) {
            saludo = 'Buenas tardes';
        } else if (hora >= 19 || hora < 6) {
            saludo = 'Buenas noches';
        }
        
        const user = getCurrentUser();
        const nombre = user.nombreCompleto || 'Usuario';
        saludoEl.innerHTML = `${saludo}, ${nombre} ${iconify('noto:waving-hand', 26)}`;
    }
}

// ==========================================
// FILTROS Y VISUALIZACIÓN
// ==========================================

/**
 * Aplicar filtros a la lista de canvas
 */
function aplicarFiltros() {
    const filterProyecto = document.getElementById('filter-proyecto').value;
    const filterEstado = document.getElementById('filter-estado').value;
    
    let canvasFiltrados = [...canvasList];
    
    // Filtrar por proyecto
    if (filterProyecto) {
        canvasFiltrados = canvasFiltrados.filter(canvas => {
            // Verificar proyectoInfo (enriquecido por backend)
            if (canvas.proyectoInfo && canvas.proyectoInfo.codPyto) {
                return canvas.proyectoInfo.codPyto == filterProyecto;
            }
            // Fallback: verificar codPyto directo
            return canvas.codPyto == filterProyecto;
        });
    }
    
    // Filtrar por estado editable
    if (filterEstado !== '') {
        const editable = filterEstado === 'true';
        canvasFiltrados = canvasFiltrados.filter(canvas => 
            canvas.editable === editable
        );
    }
    
    renderizarCanvas(canvasFiltrados);
}

/**
 * Renderizar lista de canvas
 */
function renderizarCanvas(canvas) {
    const container = document.getElementById('canvas-list');
    const noResults = document.getElementById('no-results');
    
    container.innerHTML = '';
    
    if (canvas.length === 0) {
        noResults.classList.remove('hidden');
        return;
    }
    
    noResults.classList.add('hidden');
    
    canvas.forEach(item => {
        const card = crearCanvasCard(item);
        container.appendChild(card);
    });
}

/**
 * Crear card de canvas
 */
function crearCanvasCard(canvas) {
    const template = document.getElementById('canvas-card-template');
    const clone = template.content.cloneNode(true);
    
    // Contenedor principal
    const div = document.createElement('div');
    div.className = 'canvas-card';
    div.setAttribute("data-id", canvas.codCanvas);
    div.appendChild(clone);

    // Buscar el proyecto en la lista
    const proyecto = proyectosList.find(p => p.codPyto === canvas.codPyto);
    const nombreProyecto = proyecto ? proyecto.nomPyto : 'Sin proyecto';
    const analista = empleadosList.find(e => e.codPersona == canvas.codPersona);

    // Llenar datos
    div.querySelector('.canvas-card-title').textContent = canvas.nomCanvas;
    div.querySelector('.canvas-card-meta').textContent =
    `📁 ${nombreProyecto || 'Sin proyecto'}`;
    div.querySelector('.canvas-analista').textContent = analista ? `👤 ${analista.nombre} ${analista.apellido}`: "👤 Sin asignar";

    div.querySelector('.canvas-descripcion').textContent = canvas.desCanvas || 'Sin descripción';
    div.querySelector('.btn-editar').onclick = () => abrirModalEditarCanvas(canvas);

    // Badge de estado
    const badge = div.querySelector('.badge');
    if (canvas.editable) {
        badge.className = 'badge badge-success';
        badge.innerHTML = `${iconify('noto:pencil', 16)} Editable`;

    } else {
        badge.className = 'badge badge-error';
        badge.innerHTML = `${iconify('noto-v1:locked')} Bloqueado`;
    }
    
    // Progreso
    const progreso = Math.round(canvas.porcentajeProgreso || 0);
    div.querySelector('.progreso-texto').textContent = progreso.toFixed(0);
    div.querySelector('.progress-fill').style.width = `${progreso}%`;
    
    // Tareas
    div.querySelector('.total-tareas').textContent = canvas.totalTareas || 0;
    div.querySelector('.tareas-completadas').textContent = canvas.tareasCompletadas || 0;
    
    // Botones (event listeners)
    // Boton Ver
    div.querySelector('.btn-ver').onclick = () => verCanvas(canvas.codCanvas);
    
    //Boton Bloquear/Desbloquear
    const btnToggle = div.querySelector('.btn-toggle');

    btnToggle.className = `btn ${canvas.editable ? 'btn-warning' : 'btn-success'} btn-sm btn-toggle`;
    btnToggle.innerHTML = canvas.editable ? `${iconify('noto-v1:locked')} Bloquear` : `${iconify('noto-v1:unlocked')} Desbloquear`;
    btnToggle.onclick = () => toggleEditable(canvas.codCanvas, !canvas.editable);
    
    // Boton Eliminar
    div.querySelector('.btn-eliminar').onclick = () => eliminarCanvas(canvas.codCanvas);
    
    return div;
}

/**
 * Actualizar estadísticas
 */
function actualizarEstadisticas() {
    const proyectosUnicos = new Set(
        canvasList
            .map(c => c.codPyto || (c.proyectoInfo?.codPyto))
            .filter(p => p !== null && p !== undefined)
    ).size;

    const totalCanvas = canvasList.length;
    const totalTareas = canvasList.reduce((sum, c) => sum + (c.totalTareas || 0), 0);
    const tareasCompletadas = canvasList.reduce((sum, c) => sum + (c.tareasCompletadas || 0), 0);

    document.getElementById('stat-proyectos').textContent = proyectosUnicos;
    document.getElementById('stat-canvas').textContent = totalCanvas;
    document.getElementById('stat-tareas').textContent = totalTareas;
    document.getElementById('stat-tareas-total').textContent = totalTareas;

    const extra = document.querySelectorAll('.stat-value')[3];
    if (extra) extra.textContent = tareasCompletadas;
}

// ==========================================
// MODAL CREAR CANVAS
// ==========================================

/**
 * Abrir modal crear canvas
 */
function abrirModalCrearCanvas() {
    document.getElementById('modal-crear-canvas').classList.add('show');
    document.getElementById('paso-1').classList.remove('hidden');
    document.getElementById('paso-2').classList.add('hidden');
    document.getElementById('form-crear-canvas').reset();

    llenarSelectProyectos();  
    llenarSelectAnalistas();   

    modoCreacion = null;
}


/**
 * Cerrar modal crear canvas
 */
function cerrarModalCrearCanvas() {
    document.getElementById('modal-crear-canvas').classList.remove('show');
    document.getElementById('form-crear-canvas').reset();
    document.getElementById('etapas-container').innerHTML = '';
    modoCreacion = null;
}
/*Modal Editar*/
function abrirModalEditarCanvas(canvas) {
    canvasEditando = canvas;

    document.getElementById("editar-nombre").value = canvas.nomCanvas;
    document.getElementById("editar-descripcion").value = canvas.desCanvas || '';
    document.getElementById("editar-proyecto").value = canvas.codPyto || '';
    document.getElementById("editar-analista").value = canvas.codPersona || '';

    const modal = document.getElementById("modal-editar-canvas");
    modal.classList.add("show");
    modal.classList.remove("hidden");
}

function cerrarModalEditarCanvas() {
    const modal = document.getElementById("modal-editar-canvas");
    modal.classList.remove("show");
    modal.classList.add("hidden");
}

/*Modal Asignar*/
function abrirModalAsignar(canvas) {
    console.log("ABRIENDO MODAL ASIGNAR");
    console.log(canvas);
    canvasAsignando = canvas;

    const select = document.getElementById("asignar-analista");
    select.innerHTML = '<option value="">Seleccione...</option>';

    empleadosList.forEach(e => {
        const op = document.createElement("option");
        op.value = e.codPersona;
        op.textContent = `${e.nombre} ${e.apellido}`;
        select.appendChild(op);
    });

    document.getElementById("asignar-analista").value = canvas.codPersona || "";

    const modal = document.getElementById("modal-asignar");
    modal.classList.add("show");
    modal.classList.remove("hidden");
}

async function guardarAsignacion() {
    const codPersona = document.getElementById("asignar-analista").value;

    const payload = {
        ...canvasAsignando,
        codPersona: codPersona || null
    };

    const result = await HttpClient.request(`/sc/canvas/${canvasAsignando.codCanvas}`, {
        method: "PUT",
        body: JSON.stringify(payload)
    });

    if (result.success) {
        mostrarInfoModal("Analista asignado correctamente");
        cerrarModalAsignar();
        await cargarCanvas();
        actualizarEstadisticas();
    }
}

function cerrarModalAsignar() {
    const modal = document.getElementById("modal-asignar");
    modal.classList.remove("show");
    modal.classList.add("hidden");
    canvasAsignando = null;
}

/**
 * Seleccionar opción de creación
 */
function seleccionarOpcion(tipo) {
    modoCreacion = tipo;
    
    // Ocultar paso 1, mostrar paso 2
    document.getElementById('paso-1').classList.add('hidden');
    document.getElementById('paso-2').classList.remove('hidden');
    
    // Mostrar/ocultar secciones según el tipo
    if (tipo === 'plantilla') {
        document.getElementById('seccion-plantillas').classList.remove('hidden');
        document.getElementById('seccion-etapas').classList.add('hidden');
        document.getElementById('tipo-canvas').required = true;
    } else {
        document.getElementById('seccion-plantillas').classList.add('hidden');
        document.getElementById('seccion-etapas').classList.remove('hidden');
        document.getElementById('tipo-canvas').required = false;
        
        // Agregar 3 etapas por defecto
        const container = document.getElementById('etapas-container');
        container.innerHTML = '';
        for (let i = 0; i < 3; i++) {
            agregarEtapa();
        }
    }
}

/**
 * Volver al paso 1
 */
function volverPaso1() {
    document.getElementById('paso-1').classList.remove('hidden');
    document.getElementById('paso-2').classList.add('hidden');
    document.getElementById('form-crear-canvas').reset();
    document.getElementById('etapas-container').innerHTML = '';
    modoCreacion = null;
}

/**
 * Agregar etapa personalizada
 */
let contadorEtapas = 0;
function agregarEtapa() {
    const container = document.getElementById('etapas-container');
    const index = contadorEtapas++;
    
    // Clonar template del HTML
    const template = document.getElementById('etapa-input-template');
    const clone = template.content.cloneNode(true);
    
    // Contenedor de la etapa
    const div = document.createElement('div');
    div.className = 'form-group';
    div.id = `etapa-${index}`;
    div.appendChild(clone);
    
    // Configurar placeholder
    const input = div.querySelector('.etapa-input');
    input.placeholder = `Nombre de la etapa ${index + 1}`;
    input.dataset.index = index;
    
    // Event listener para botón eliminar
    div.querySelector('.btn-eliminar-etapa').onclick = () => eliminarEtapa(index);
    
    container.appendChild(div);
}

/**
 * Eliminar etapa personalizada
 */
function eliminarEtapa(index) {
    const etapa = document.getElementById(`etapa-${index}`);
    if (etapa) {
        etapa.remove();
    }
}

function toggleProyectoSelect() {
    const radioSeleccionado = document.querySelector('input[name="vincular-proyecto"]:checked');
    const selectProyecto = document.getElementById("proyecto-canvas");


    if (!radioSeleccionado || !selectProyecto) return;


    if (radioSeleccionado.value === "NO") {
        selectProyecto.disabled = true;
        selectProyecto.value = ""; // limpiar selección
    } else {
        selectProyecto.disabled = false;
    }
}

/**
 * Manejar creación de canvas
 */
document.addEventListener('DOMContentLoaded', () => {
    const formCrear = document.getElementById('form-crear-canvas');
    if (formCrear) {
        formCrear.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const nombre = document.getElementById('nombre-canvas').value.trim();
            const descripcion = document.getElementById('descripcion-canvas').value.trim();
            const codProyecto = document.getElementById('proyecto-canvas').value;
            // Obtener valor del radio (SI / NO)
            const radioSeleccionado = document.querySelector('input[name="vincular-proyecto"]:checked');
            const vincularProyecto = radioSeleccionado ? radioSeleccionado.value : "SI"; // por defecto SI

            // Validar solo nombre como obligatorio
            if (!nombre) {
                AlertManager.error('El nombre del canvas es obligatorio');
                return;
            }
            // Si eligió "SI", el proyecto es obligatorio
            let codigoProyectoFinal = null;
            if (vincularProyecto === "SI") {
                if (!codProyecto) {
                    AlertManager.error('Debe seleccionar un proyecto si desea vincularlo');
                    return;
                }
                codigoProyectoFinal = parseInt(codProyecto);
            }
            
            const user = getCurrentUser();
            
            let codPersona = document.getElementById("analista-canvas").value;
            if (codPersona === "") codPersona = null;

            const payload = {
                nomCanvas: nombre,
                desCanvas: descripcion,
                codPyto: codigoProyectoFinal,
                codPersona: codPersona,
                editable: true,
                estado: {
                    codEstado: 1,
                    nomEstado: "Activo",
                    desEstado: "Canvas activo",
                    vigente: 1
                    }
                };

            
            // Tipo de canvas
            if (modoCreacion === 'plantilla') {
                const tipCanvas = document.getElementById('tipo-canvas').value;
                if (!tipCanvas) {
                    AlertManager.error('Seleccione un tipo de canvas');
                    return;
                }
                payload.tipoCanvas = { 
                    tipCanvas: tipCanvas,
                    desTipCanvas: "Plantilla",
                    vigente: 1
                };
            } else if (modoCreacion === 'blanco') {
                // Canvas en blanco (tipo libre)
                payload.tipoCanvas = { 
                    tipCanvas: 'F',// <--- F de canva libre (Free)
                    desTipCanvas: "Libre",
                    vigente: 1
                };
                
                // Obtener etapas personalizadas
                const etapasInputs = document.querySelectorAll('.etapa-input');
                if (etapasInputs.length === 0) {
                    AlertManager.error('Agregue al menos una etapa');
                    return;
                }
                
                // Las etapas se crearán después de crear el canvas
                payload.etapasPersonalizadas = Array.from(etapasInputs).map((input, i) => ({
                    nomEtapa: input.value.trim(),
                    desEtapa: '',
                    numEtapa: i + 1,
                    vigente: 1
                }));
            }
            
            // Log para verificar
            console.log('Payload enviado:', JSON.stringify(payload, null, 2));

            ButtonManager.setLoading('btn-crear-canvas', 'Creando canvas...');

            const result = await HttpClient.post(API.CANVAS, payload);
            console.log("Respuesta CRUDA del backend:", result); // AGREGAR ESTO
            
            if (result.success) {
                AlertManager.success('Canvas creado correctamente');
                
                // Si tiene etapas personalizadas, crearlas
                if (payload.etapasPersonalizadas && result.data.id) {
                    await crearEtapasPersonalizadas(result.data.id, payload.etapasPersonalizadas);
                }
                
                cerrarModalCrearCanvas();
                await cargarCanvas();
                actualizarEstadisticas();
            } else {
                AlertManager.error(result.error);
            }
            
            ButtonManager.reset('btn-crear-canvas');
        });
    }
    
    // Cargar datos iniciales
    cargarDatosIniciales();

    // Actualizar fecha y saludo
    actualizarFechaSaludo();
    
    // Actualizar fecha cada minuto
    setInterval(actualizarFechaSaludo, 60000);
});

/**
 * Crear etapas personalizadas
 */
async function crearEtapasPersonalizadas(codCanvas, etapas) {
    for (const etapa of etapas) {
        await HttpClient.post(API.ETAPAS, {
            ...etapa,
            canvas: { codCanvas }
        });
    }
}

// ==========================================
// ACCIONES DE CANVAS
// ==========================================

/**
 * Ver detalles de canvas
 */
function verCanvas(codCanvas) {
  window.location.href = `/html/canvas-detalle.html?id=${codCanvas}`;
}

/**
 * Toggle estado editable
 */
async function toggleEditable(codCanvas, nuevoEstado) {
    const confirm = await mostrarConfirmModal(
        `¿${nuevoEstado ? 'Desbloquear' : 'Bloquear'} este canvas?`
    );
    if (!confirm) return;
    
    try {
        const token = localStorage.getItem("token");

        const res = await fetch(`/api/sc/canvas/${codCanvas}/toggle`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });

        if (!res.ok) {
            mostrarInfoModal("Error al cambiar el estado del canvas");
            return;
        }

        const data = await res.json();
        const editableResult = data?.data?.editable;
        
        // REGISTRAR HISTORIAL
        const user = getCurrentUser();
        await registrarHistorial(
            codCanvas,
            user.codPersona,
            editableResult ? "Desbloquear Canvas" : "Bloquear Canvas",
            `El usuario ${user.nombreCompleto} ${editableResult ? "desbloqueó" : "bloqueó"} el canvas`
        );

        actualizarCanvasCardUI(codCanvas, editableResult);

        mostrarInfoModal(
            editableResult 
                ? "Canvas desbloqueado correctamente" 
                : "Canvas bloqueado correctamente"
        );
        await cargarCanvas();
        actualizarEstadisticas();

    } catch (error) {
        console.error(error);
        mostrarInfoModal("Error en el servidor");
    }
}

/**
 * Eliminar canvas
 */
async function eliminarCanvas(codCanvas) {
    const confirm = await mostrarConfirmModal(
        '¿Está seguro de eliminar este canvas? Esta acción no se puede deshacer.'
    );
    if (!confirm) return;

    const result = await HttpClient.request(`${API.CANVAS}/${codCanvas}`, {
        method: 'DELETE'
    });
    
    if (result.success) {
        mostrarInfoModal('Canvas eliminado correctamente');
        await cargarCanvas();
        actualizarEstadisticas();
    } else {
        mostrarInfoModal(result.error);
    }
}

document.getElementById("form-editar-canvas").addEventListener("submit", async (e) => {
    e.preventDefault();

    const payload = {
        ...canvasEditando,
        nomCanvas: document.getElementById("editar-nombre").value,
        desCanvas: document.getElementById("editar-descripcion").value,
        codPyto: document.getElementById("editar-proyecto").value || null,
        codPersona: document.getElementById("editar-analista").value || null
    };

    const result = await HttpClient.request(`/sc/canvas/${canvasEditando.codCanvas}`, {
        method: "PUT",
        body: JSON.stringify(payload)
    });

    if (result.success) {
        // REGISTRAR HISTORIAL
        const user = getCurrentUser();
        await registrarHistorial(
            canvasEditando.codCanvas,
            user.codPersona,
            "Editar Canvas",
            `Se modificó el canvas: "${payload.nomCanvas}"`
        );

        mostrarInfoModal("Canvas actualizado correctamente");
        cerrarModalEditarCanvas();
        await cargarCanvas();
        actualizarEstadisticas();
    } else {
        mostrarInfoModal("Error al guardar cambios");
    }
});

// ==========================================
// UTILIDADES
// ==========================================

/**
 * Mostrar/ocultar loading
 */
function mostrarLoading(mostrar) {
    const loading = document.getElementById('loading');
    if (loading) {
        if (mostrar) {
            loading.classList.remove('hidden');
        } else {
            loading.classList.add('hidden');
        }
    }
}

/**
 * 
 * Modal de confirmación para Acciones de Canvas
 * @param {string} mensaje - Texto del modal
 * @returns {Promise<boolean>} - true = aceptar, false = cancelar
 */
function mostrarConfirmModal(message) {
    return new Promise(resolve => {
        const modal = document.getElementById("modalConfirm");
        const messageElem = modal.querySelector(".modal-message");
        const btnAceptar = modal.querySelector(".btn-success");
        const btnCancelar = modal.querySelector(".btn-secondary");
        const btnCerrar = modal.querySelector(".modal-close");

        messageElem.textContent = message;
        modal.classList.remove("hidden");
        btnAceptar.onclick = null;
        btnCancelar.onclick = null;
        btnCerrar.onclick = null;

        //Aceptar
        btnAceptar.onclick = () => {
            modal.classList.add("hidden");
            resolve(true);
        };

        //Cancelar / Cerrar
        btnCancelar.onclick = () => {
            modal.classList.add("hidden");
            resolve(false);
        };

        btnCerrar.onclick = () => {
            modal.classList.add("hidden");
            resolve(false);
        }
    });
}

/**
 * Muestra un modal informativo
 */
function mostrarInfoModal(message) {
    return new Promise(resolve => {
        const modal = document.getElementById("modalInfo");
        const mensajeElem = modal.querySelector(".modal-message");
        const btnAceptar = modal.querySelector(".btn-success");

        mensajeElem.textContent = message;
        modal.classList.remove("hidden");

        //Aceptar
        btnAceptar.onclick = () => {
            modal.classList.add("hidden");
            resolve();
        };
    });
}

// ==========================================
// CREACIÓN DE MODALES
// ==========================================

/*
 * Abre/cierra de toggle de perfil
 */
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

// --- Cerrar al hacer click fuera (registrar sólo una vez) ---
(function setupProfileOutsideClickHandler() {
    // Evitar registrar varias veces
    if (window.__profileOutsideHandlerRegistered) return;
    window.__profileOutsideHandlerRegistered = true;

    document.addEventListener('click', function (e) {
        const menu = document.getElementById('profile-menu');
        const userInfoEl = document.querySelector('.user-info');
        const chevron = document.getElementById('user-info-toggle-chevron-down');
        if (!menu || !userInfoEl) return;

        const target = e.target;

        // si el menú está cerrado, nada que hacer
        if (!window.__profileMenuOpen) return;

        // Si clic dentro del menú o sobre el userInfo, no cerramos
        if (menu.contains(target) || userInfoEl.contains(target)) return;

        // clic fuera -> cerrar
        menu.style.display = 'none';
        if (chevron) {
            chevron.classList.remove('fa-chevron-up');
            chevron.classList.add('fa-chevron-down');
        }
        window.__profileMenuOpen = false;
    }, true); // useCapture true para mayor fiabilidad
})();


/**
 * Actualiza solo el card del canvas indicado, solo algo visual para bloquear/desv
 */
function actualizarCanvasCardUI(codCanvas, editable) {

    // Buscar el card por atributo data-id
    const card = document.querySelector(`.canvas-card[data-id="${codCanvas}"]`);
    if (!card) return console.warn("No se encontró card para actualizar:", codCanvas);

    // === Actualizar badge ===
    const badge = card.querySelector(".badge");
    if (editable) {
        badge.className = "badge badge-success";
        badge.innerHTML = `${iconify('noto:pencil', 16)} Editable`;
    } else {
        badge.className = "badge badge-error";
        badge.innerHTML = `${iconify('noto-v1:locked')} Bloqueado`;
    }

    // === Actualizar botón ===
    const btn = card.querySelector(".btn-toggle");
    
    btn.className = `btn ${editable ? 'btn-warning' : 'btn-success'} btn-sm btn-toggle`;
    btn.innerHTML = editable
        ? `${iconify('noto-v1:locked')} Bloquear`
        : `${iconify('noto-v1:unlocked')} Desbloquear`;

    // Reconfigurar onclick
    btn.onclick = () => toggleEditable(codCanvas, !editable);

    // === Animación suave para indicar que se actualizó ===
    card.classList.add("card-update-glow");
    setTimeout(() => card.classList.remove("card-update-glow"), 800);
}



// Funcion para registrar cambios
async function registrarHistorial(codCanvas, codPersona, accion, detalle) {
    const payload = {
        codPersona: codPersona,
        accion: accion,
        detalle: detalle,
        canvas: { codCanvas: codCanvas }
    };

    return await HttpClient.post(API.HISTORIAL, payload);
}


// Foto de perfil
async function cargarFotoTopbar() {
    const user = getCurrentUser();
    if (!user || !user.codPersona) return;

    try {
        const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
            headers: {
                "Authorization": `Bearer ${user.token}`
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

