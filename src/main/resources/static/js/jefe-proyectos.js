/**
 * SYSCANVAS - jefe-proyectos.js (ADAPTADO)
 */

const API = {
    PROYECTOS: '/t/proyecto',
    CANVAS: '/sc/canvas',
    PYTOPERS: '/t/pytopers'
};

let proyectosList = [];
let canvasList = [];
let proyectoActual = null;
let chartProgreso = null;
let empleadosList = [];

document.addEventListener('DOMContentLoaded', async () => {
    await inicializarVista();
});

/* ================================
   CARGA INICIAL
================================ */
async function inicializarVista() {
    mostrarLoading(true);

    await Promise.all([
        cargarProyectos(),
        cargarCanvas(),
        cargarEmpleados()
    ]);

    mostrarLoading(false);

    aplicarFiltros();
}

/* ================================
   CARGA DE DATOS
================================ */
async function cargarProyectos() {
    const result = await HttpClient.get(`${API.PROYECTOS}?page=0&size=100`);

    if (result.success) {
        proyectosList = result.data.content || result.data;
    } else {
        AlertManager.error("Error al cargar proyectos");
    }
}

async function cargarCanvas() {
    const result = await HttpClient.get(`${API.CANVAS}?page=0&size=200`);

    if (result.success) {
        canvasList = result.data.content || result.data;
    }
}

async function cargarEmpleados() {
    const result = await HttpClient.get('/t/empleado/activos');

    if (result.success) {
        empleadosList = result.data.content || result.data;
    }
}

/* ================================
   FILTROS Y RENDER
================================ */
function aplicarFiltros() {
    const estado = document.getElementById("filter-estado").value;
    const texto = document.getElementById("search-proyecto").value.toLowerCase();

    let lista = [...proyectosList];

    if (estado !== "") {
        lista = lista.filter(p => p.vigente == estado);
    }

    if (texto.length > 0) {
        lista = lista.filter(p => p.nomPyto.toLowerCase().includes(texto));
    }

    renderizarProyectos(lista);
}

function renderizarProyectos(lista) {
    const cont = document.getElementById("proyectos-list");
    const noResults = document.getElementById("no-results");

    cont.innerHTML = "";

    if (lista.length === 0) {
        noResults.classList.remove("hidden");
        return;
    }

    noResults.classList.add("hidden");

    lista.forEach(p => cont.appendChild(crearProyectoCard(p)));
}

/* ================================
   CREAR CARD
================================ */
function crearProyectoCard(proyecto) {
    const template = document.getElementById("proyecto-card-template");
    const clone = template.content.cloneNode(true);

    const card = document.createElement("div");
    card.className = "canvas-card";
    card.appendChild(clone);

    card.querySelector(".proyecto-card-title").textContent = proyecto.nomPyto;

    const badge = card.querySelector(".badge-estado");

        if (proyecto.vigente === 1) {
            badge.textContent = "Activo";
            badge.classList.add("badge-success");
        } else {
            badge.textContent = "Inactivo";
            badge.classList.add("badge-secondary");
        }


    card.querySelector(".proyecto-anios").textContent =
        proyecto.annoIni && proyecto.annoFin
            ? `${proyecto.annoIni} - ${proyecto.annoFin}`
            : "Sin fechas";

    const canvasProyecto = canvasList.filter(c => c.codPyto === proyecto.codPyto);
    const totalCanvas = canvasProyecto.length;
    const totalTareas = canvasProyecto.reduce((a, c) => a + (c.totalTareas || 0), 0);
    const tareasCompletadas = canvasProyecto.reduce((a, c) => a + (c.tareasCompletadas || 0), 0);

    const progreso = totalTareas > 0 
        ? Math.round((tareasCompletadas / totalTareas) * 100)
        : 0;

    card.querySelector(".proyecto-canvas").textContent = totalCanvas;
    card.querySelector(".proyecto-tareas").textContent = totalTareas;
    card.querySelector(".progress-fill").style.width = progreso + "%";

    const btn = card.querySelector(".btn-primary");
    btn.onclick = () => verDetalleProyecto(proyecto.codPyto);

    return card;
}

/* ================================
   DETALLE DEL PROYECTO
================================ */
async function verDetalleProyecto(id) {
    proyectoActual = proyectosList.find(p => p.codPyto === id);
    if (!proyectoActual) return;

    document.getElementById("modal-proyecto-nombre").textContent =
        proyectoActual.nomPyto;

    await cargarDatosProyecto(id);

    document.getElementById("modal-detalle").classList.add("show");
    cambiarTab("canvas");
}

async function cargarDatosProyecto(id) {

    const canvasProyecto = canvasList.filter(c => c.codPyto == id);

    const contCanvas = document.getElementById("canvas-proyecto-list");
    contCanvas.innerHTML = "";

    if (canvasProyecto.length === 0) {
        contCanvas.innerHTML = `<p class="text-center text-gray">No hay canvas para este proyecto</p>`;
    } else {
        canvasProyecto.forEach(c => {
    const analista = c.codPersona
        ? empleadosList?.find(e => e.codPersona === c.codPersona)
        : null;

    const nombreAnalista = analista
        ? `${analista.nombre} ${analista.apellido}`
        : "Sin asignar";

    const item = document.createElement("div");
    item.className = "card p-3 mb-2";
    item.innerHTML = `
        <div class="flex-between">
            <div>
                <strong>${c.nomCanvas}</strong>
                <p class="text-sm text-gray">${c.desCanvas || "Sin descripción"}</p>
                <p class="text-sm text-gray mt-1">
                    👤 <strong>${nombreAnalista}</strong>
                </p>
            </div>

            <button class="btn btn-primary btn-sm"
                onclick="window.location.href='/html/canvas-detalle.html?id=${c.codCanvas}'">
                👁 Ver
            </button>
        </div>
    `;

    contCanvas.appendChild(item);
});
    }

    /* Analistas asignados */
const resp = await HttpClient.get(`${API.PYTOPERS}/proyecto/${id}`);
const contAnalistas = document.getElementById("analistas-proyecto-list");
contAnalistas.innerHTML = "";

// resp.data YA ES una lista de T_PytoPers
if (resp.success) {
    const analistas = resp.data.filter(a => a.vigente === 1);

    analistas.forEach(a => {
        const div = document.createElement("div");
        div.className = "card p-3 mb-2";
        div.innerHTML = `
            <div class="flex-between">
                <div>
                    <strong>${a.empleado?.nombre || ""} ${a.empleado?.apellido || ""}</strong>
                    <p class="text-sm text-gray">
                        Cargo: ${a.codCargo ?? "N/A"}
                    </p>
                </div>
                <span class="badge badge-success">Asignado</span>
            </div>
        `;
        contAnalistas.appendChild(div);
    });
}

    /* Estadísticas */
    document.getElementById("stat-canvas-total").textContent = canvasProyecto.length;

    const totalT = canvasProyecto.reduce((a, c) => a + (c.totalTareas || 0), 0);
    const compT = canvasProyecto.reduce((a, c) => a + (c.tareasCompletadas || 0), 0);

    const prom = totalT > 0 ? Math.round((compT / totalT) * 100) : 0;

    document.getElementById("stat-tareas-total").textContent = totalT;
    document.getElementById("stat-progreso-prom").textContent = prom + "%";

    renderizarGrafico(canvasProyecto);
}

/* ================================
   GRÁFICO
================================ */
function renderizarGrafico(lista) {
    const ctx = document.getElementById("chart-progreso");

    if (chartProgreso) chartProgreso.destroy();

    chartProgreso = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: lista.map(c => c.nomCanvas.substring(0, 20)),
            datasets: [{
                label: 'Progreso (%)',
                data: lista.map(c => c.porcentajeProgreso || 0),
                backgroundColor: '#A27BFF',
                borderColor: '#A476F4',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: { beginAtZero: true, max: 100 }
            }
        }
    });
}

function cambiarTab(tab, btnElement = null) {
    document.querySelectorAll(".tab-content").forEach(t => t.classList.add("hidden"));
    document.querySelectorAll(".tab-btn").forEach(b => b.classList.remove("active"));

    document.getElementById(`tab-${tab}`).classList.remove("hidden");

    if (btnElement) {
        btnElement.classList.add("active");
    }
}

function cerrarModalDetalle() {
    const modal = document.getElementById("modal-detalle");
    if (modal) modal.classList.remove("show");

    if (chartProgreso) {
        chartProgreso.destroy();
        chartProgreso = null;
    }
}

/* ================================
   UTILIDADES
================================ */
function mostrarLoading(show) {
    const el = document.getElementById("loading");
    if (el) el.classList.toggle("hidden", !show);
}
