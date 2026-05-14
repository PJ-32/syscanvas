/**
 * SYSCANVAS - reportes.js
 * Dashboard de Reportes (Jefe)
 */

const API = {
    PROYECTOS: '/t/proyecto',
    CANVAS: '/sc/canvas',
    TAREAS: '/sc/tarea',
    EMPLEADOS: '/t/empleado/activos'
};

let proyectosList = [];
let canvasList = [];
let tareasList = [];
let empleadosList = [];

// Charts
let chartProyectos = null;
let chartAnalistas = null;
let chartTiempo = null;

document.addEventListener('DOMContentLoaded', () => {
    inicializarVista();
});

async function inicializarVista() {
    await cargarDatos();
    aplicarFiltros();
}

async function cargarDatos() {
    mostrarLoading(true);
    
    await Promise.all([
        cargarProyectos(),
        cargarCanvas(),
        cargarTareas(),
        cargarEmpleados()
    ]);
    
    llenarSelectProyectos();
    mostrarLoading(false);
}

async function cargarProyectos() {
    const result = await HttpClient.get(`${API.PROYECTOS}?page=0&size=200`);
    if (result.success) {
        proyectosList = result.data.content || result.data;
    }
}

async function cargarCanvas() {
    const result = await HttpClient.get(`${API.CANVAS}?page=0&size=500`);
    if (result.success) {
        canvasList = result.data.content || result.data;
    }
}

async function cargarTareas() {
    const result = await HttpClient.get(`${API.TAREAS}?page=0&size=2000`);
    if (result.success) {
        tareasList = result.data.content || result.data;
    }
}

async function cargarEmpleados() {
    const result = await HttpClient.get(API.EMPLEADOS);
    if (result.success) {
        empleadosList = result.data.content || result.data;
    }
}

function llenarSelectProyectos() {
    const select = document.getElementById('filter-proyecto');
    while (select.options.length > 1) select.remove(1);

    const proyectosActivos = proyectosList.filter(p => p.vigente === 1);
    proyectosActivos.forEach(proyecto => {
        const option = document.createElement('option');
        option.value = proyecto.codPyto;
        option.textContent = proyecto.nomPyto;
        select.appendChild(option);
    });
}

function aplicarFiltros() {
    const filterProyecto = document.getElementById('filter-proyecto').value;
    const fechaInicio = document.getElementById('filter-fecha-inicio').value;
    const fechaFin = document.getElementById('filter-fecha-fin').value;

    let canvasFiltrados = [...canvasList];
    let tareasFiltradas = [...tareasList];

    // Filtrar por proyecto
    if (filterProyecto) {
    canvasFiltrados = canvasList.filter(c => c.codPyto == filterProyecto);

    const codCanvasFiltrados = canvasFiltrados.map(c => c.codCanvas);

    tareasFiltradas = tareasList.filter(t => {
        const canvasDeTarea =
            t.etapa?.codCanvas ??
            t.codCanvas ??
            null;
        return canvasDeTarea && codCanvasFiltrados.includes(canvasDeTarea);
    });
}
    // Filtrar por fechas (tareas)
    if (fechaInicio) {
        const inicio = new Date(fechaInicio);
        tareasFiltradas = tareasFiltradas.filter(t => 
            new Date(t.fecCreacion) >= inicio
        );
    }

    if (fechaFin) {
        const fin = new Date(fechaFin);
        tareasFiltradas = tareasFiltradas.filter(t => 
            new Date(t.fecCreacion) <= fin
        );
    }

    actualizarEstadisticas(canvasFiltrados, tareasFiltradas);
    renderizarGraficos(canvasFiltrados, tareasFiltradas);
    renderizarTopAnalistas(tareasFiltradas);
}

function actualizarEstadisticas(canvas, tareas) {
    const proyectosActivos = new Set(
        canvas.filter(c => c.codPyto).map(c => c.codPyto)
    ).size;

    const totalCanvas = canvas.length;
    const totalTareas = tareas.length;
    const tareasCompletadas = tareas.filter(t => t.vigente === 0).length;
    const tasaCompletitud = totalTareas > 0 
        ? ((tareasCompletadas / totalTareas) * 100).toFixed(1) 
        : 0;

    document.getElementById('stat-proyectos-activos').textContent = proyectosActivos;
    document.getElementById('stat-canvas-totales').textContent = totalCanvas;
    document.getElementById('stat-tareas-totales').textContent = totalTareas;
    document.getElementById('stat-tasa-completadas').textContent = `${tasaCompletitud}%`;
}

function renderizarGraficos(canvas, tareas) {
    renderizarGraficoProyectos(canvas);
    renderizarGraficoAnalistas(tareas);
    renderizarGraficoTiempo(tareas);
}

function renderizarGraficoProyectos(canvas) {
    const ctx = document.getElementById('chart-proyectos');
    if (chartProyectos) chartProyectos.destroy();

    // Agrupar por proyecto
    const porProyecto = {};
    canvas.forEach(c => {
        if (c.codPyto) {
            if (!porProyecto[c.codPyto]) {
                const proyecto = proyectosList.find(p => p.codPyto === c.codPyto);
                porProyecto[c.codPyto] = {
                    nombre: proyecto ? proyecto.nomPyto : 'Sin nombre',
                    totalTareas: 0,
                    completadas: 0
                };
            }
            porProyecto[c.codPyto].totalTareas += (c.totalTareas || 0);
            porProyecto[c.codPyto].completadas += (c.tareasCompletadas || 0);
        }
    });

    const labels = Object.values(porProyecto).map(p => 
        p.nombre.length > 20 ? p.nombre.substring(0, 20) + '...' : p.nombre
    );
    const progresos = Object.values(porProyecto).map(p => 
        p.totalTareas > 0 ? (p.completadas / p.totalTareas * 100).toFixed(1) : 0
    );

    chartProyectos = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: 'Progreso (%)',
                data: progresos,
                backgroundColor: '#A27BFF',
                borderColor: '#A476F4',
                borderWidth: 1
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            scales: {
                y: {
                    beginAtZero: true,
                    max: 100,
                    ticks: {
                        callback: function(value) {
                            return value + '%';
                        }
                    }
                }
            },
            plugins: {
                legend: {
                    display: false
                }
            }
        }
    });
}

function renderizarGraficoAnalistas(tareas) {
    const ctx = document.getElementById('chart-analistas');
    if (chartAnalistas) chartAnalistas.destroy();

    // Agrupar por analista
    const porAnalista = {};
    tareas.forEach(t => {
        if (t.codPersona) {
            if (!porAnalista[t.codPersona]) {
                const empleado = empleadosList.find(e => e.codPersona === t.codPersona);
                porAnalista[t.codPersona] = {
                    nombre: empleado ? `${empleado.nombre} ${empleado.apellido}` : 'N/A',
                    completadas: 0
                };
            }
            if (t.vigente === 0) {
                porAnalista[t.codPersona].completadas++;
            }
        }
    });

    // Top 10 analistas
    const top = Object.entries(porAnalista)
        .sort((a, b) => b[1].completadas - a[1].completadas)
        .slice(0, 10);

    const labels = top.map(([_, data]) => data.nombre);
    const completadas = top.map(([_, data]) => data.completadas);

    chartAnalistas = new Chart(ctx, {
    type: 'bar',
    data: {
        labels: labels,
        datasets: [{
            label: 'Tareas Completadas',
            data: completadas,
            backgroundColor: '#10B981',
            borderColor: '#059669',
            borderWidth: 1
        }]
    },
    options: {
        indexAxis: 'y',   // 👉 convierte la barra en horizontal
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

function renderizarGraficoTiempo(tareas) {
    const ctx = document.getElementById('chart-tiempo');
    if (chartTiempo) chartTiempo.destroy();

    // Últimas 8 semanas
    const semanas = {};
    const hoy = new Date();

    for (let i = 7; i >= 0; i--) {
        const fecha = new Date(hoy);
        fecha.setDate(fecha.getDate() - (i * 7));
        const key = `${fecha.getFullYear()}-W${getWeekNumber(fecha)}`;
        semanas[key] = 0;
    }

    // Contar tareas completadas por semana
    tareas.filter(t => t.vigente === 0).forEach(t => {
        const fecha = new Date(t.fecModificacion);
        const key = `${fecha.getFullYear()}-W${getWeekNumber(fecha)}`;
        if (semanas[key] !== undefined) {
            semanas[key]++;
        }
    });

    const labels = Object.keys(semanas);
    const datos = Object.values(semanas);

    chartTiempo = new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [{
                label: 'Tareas Completadas',
                data: datos,
                borderColor: '#A27BFF',
                backgroundColor: 'rgba(162, 123, 255, 0.1)',
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

function renderizarTopAnalistas(tareas) {
    const container = document.getElementById('top-analistas');
    container.innerHTML = '';

    // Agrupar por analista
    const porAnalista = {};
    tareas.forEach(t => {
        if (t.codPersona) {
            if (!porAnalista[t.codPersona]) {
                const empleado = empleadosList.find(e => e.codPersona === t.codPersona);
                porAnalista[t.codPersona] = {
                    nombre: empleado ? `${empleado.nombre} ${empleado.apellido}` : 'N/A',
                    total: 0,
                    completadas: 0
                };
            }
            porAnalista[t.codPersona].total++;
            if (t.vigente === 0) {
                porAnalista[t.codPersona].completadas++;
            }
        }
    });

    // Top 5
    const top = Object.entries(porAnalista)
        .sort((a, b) => b[1].completadas - a[1].completadas)
        .slice(0, 5);

    if (top.length === 0) {
        container.innerHTML = '<p class="text-center text-gray">No hay datos suficientes</p>';
        return;
    }

    top.forEach(([codPersona, data], index) => {
        const tasa = data.total > 0 ? (data.completadas / data.total * 100).toFixed(1) : 0;
        
        const item = document.createElement('div');
        item.className = 'card p-3 mb-2';
        item.innerHTML = `
            <div class="flex-between">
                <div class="flex gap-3" style="align-items: center;">
                    <div style="font-size: 32px; width: 50px; text-align: center;">
                        ${index === 0 ? '🥇' : index === 1 ? '🥈' : index === 2 ? '🥉' : `${index + 1}.`}
                    </div>
                    <div>
                        <strong>${data.nombre}</strong>
                        <p class="text-sm text-gray">
                            ${data.completadas} de ${data.total} tareas completadas
                        </p>
                    </div>
                </div>
                <div style="text-align: right;">
                    <div style="font-size: 24px; font-weight: bold; color: var(--primary);">
                        ${tasa}%
                    </div>
                    <div class="text-sm text-gray">Tasa de completitud</div>
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

async function exportarReportePDF() {
    try {
        AlertManager.success("Generando PDF...");

        const { jsPDF } = window.jspdf;

        const pdf = new jsPDF({
            orientation: "portrait",
            unit: "px",
            format: "a4"
        });

        const pageWidth = pdf.internal.pageSize.getWidth();
        const pageHeight = pdf.internal.pageSize.getHeight();
        let y = 30;

        /* ==========================================================
           PORTADA
        ========================================================== */
        pdf.setFont("Helvetica", "bold");
        pdf.setFontSize(22);
        pdf.text("Reporte General SYSCANVAS", pageWidth / 2, y, { align: "center" });

        y += 25;

        pdf.setFontSize(13);
        pdf.setFont("Helvetica", "normal");
        pdf.text("Generado: " + new Date().toLocaleString(), pageWidth / 2, y, { align: "center" });

        y += 40;

        pdf.setFont("Helvetica", "normal");
        pdf.setFontSize(14);
        pdf.text(
            "Este reporte resume el estado de los proyectos, tareas y productividad en el sistema SYSCANVAS.",
            pageWidth / 2,
            y,
            { align: "center", maxWidth: pageWidth - 60 }
        );

        // Nueva página
        pdf.addPage();
        y = 20;

        /* ==========================================================
           1. ESTADÍSTICAS GENERALES
        ========================================================== */
        pdf.setFont("Helvetica", "bold");
        pdf.setFontSize(18);
        pdf.text("Estadísticas Generales", 20, y);

        y += 15;

        const stats = document.querySelector(".stats-row");
        if (stats) {
            const statsCanvas = await html2canvas(stats, {
                scale: 2,
                backgroundColor: "#FFFFFF",
                useCORS: true
            });

            const img = statsCanvas.toDataURL("image/png");
            const imgProps = pdf.getImageProperties(img);

            const ratio = (pageWidth - 40) / imgProps.width;
            const imgHeight = imgProps.height * ratio;

            pdf.addImage(img, "PNG", 20, y, pageWidth - 40, imgHeight);
            y += imgHeight + 20;
        }

        /* ==========================================================
           2. GRÁFICOS
        ========================================================== */
        pdf.setFont("Helvetica", "bold");
        pdf.setFontSize(18);
        pdf.text("Gráficos Analíticos", 20, y);

        y += 25;

        // --- PROGRESO POR PROYECTO ---
        const chartProy = document.getElementById("chart-proyectos");
        if (chartProy) {
            const img = chartProy.toDataURL("image/png");
            pdf.setFontSize(14);
            pdf.text("Progreso por Proyecto", 20, y);
            y += 10;
            pdf.addImage(img, "PNG", 20, y, pageWidth - 40, 180);
            y += 200;
        }

        // Saltar página si queda poco espacio
        if (y + 220 > pageHeight) {
            pdf.addPage();
            y = 20;
        }

        // --- PRODUCTIVIDAD POR ANALISTA ---
        const chartAnalistas = document.getElementById("chart-analistas");
        if (chartAnalistas) {
            const img = chartAnalistas.toDataURL("image/png");
            pdf.setFontSize(14);
            pdf.text("Productividad por Analista", 20, y);
            y += 10;
            pdf.addImage(img, "PNG", 20, y, pageWidth - 40, 180);
            y += 200;
        }

        if (y + 220 > pageHeight) {
            pdf.addPage();
            y = 20;
        }

        // --- TAREAS POR SEMANA ---
        const chartTiempo = document.getElementById("chart-tiempo");
        if (chartTiempo) {
            const img = chartTiempo.toDataURL("image/png");
            pdf.setFontSize(14);
            pdf.text("Tareas Completadas por Semana", 20, y);
            y += 10;
            pdf.addImage(img, "PNG", 20, y, pageWidth - 40, 200);
            y += 220;
        }

        /* ==========================================================
           3. TOP ANALISTAS
        ========================================================== */
        pdf.addPage();
        y = 30;

        pdf.setFont("Helvetica", "bold");
        pdf.setFontSize(18);
        pdf.text("Top 5 Analistas Más Productivos", 20, y);

        y += 20;

        const topContainer = document.getElementById("top-analistas");
        if (topContainer) {
            const topCanvas = await html2canvas(topContainer, {
                scale: 2,
                useCORS: true,
                backgroundColor: "#FFFFFF"
            });

            const img = topCanvas.toDataURL("image/png");
            const imgProps = pdf.getImageProperties(img);
            const ratio = (pageWidth - 40) / imgProps.width;
            const imgHeight = imgProps.height * ratio;

            pdf.addImage(img, "PNG", 20, y, pageWidth - 40, imgHeight);
        }

        /* ==========================================================
           DESCARGAR
        ========================================================== */
        pdf.save("Reporte_SYSCANVAS.pdf");
        AlertManager.success("PDF generado con éxito 🎉");

    } catch (error) {
        console.error(error);
        AlertManager.error("Ocurrió un error al generar el PDF");
    }
}

function mostrarLoading(mostrar) {
    const loading = document.getElementById('loading');
    if (!loading) return;
    loading.classList.toggle('hidden', !mostrar);
}