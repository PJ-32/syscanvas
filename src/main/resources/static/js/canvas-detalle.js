document.addEventListener("DOMContentLoaded", async () => {
  btnBloqueo = document.getElementById("btn-bloqueo");

  const urlParams = new URLSearchParams(window.location.search);
  const canvasId = urlParams.get("id");

  if (!canvasId) {
    alert("No se encontró el ID del canvas en la URL");
    return;
  }

  document.getElementById("btn-nueva-tarea")
          .addEventListener("click", () => {
              if (canvasBloqueado) return alert("El canvas está bloqueado");
              abrirModal("modal-tarea");
          });

  document.getElementById("form-tarea")
          .addEventListener("submit", guardarTarea);

  // Bloquear/desbloquear canvas
if (btnBloqueo) {
    btnBloqueo.addEventListener("click", async () => {

        const id = canvasActual.codCanvas;
        const token = localStorage.getItem("token");

        // OBTENER USUARIO AQUÍ
        const user = getCurrentUser();
        const codPersona = user?.codPersona ?? "(desconocido)";

        try {
            const res = await fetch(`/api/sc/canvas/${id}/toggle`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${token}`
                }
            });

            if (!res.ok) throw new Error("Error al actualizar el estado editable");

            const data = await res.json();
            console.log("Canvas actualizado:", data);

            // El backend devuelve dentro de "data"
            canvasActual.editable = data.data.editable;
            canvasBloqueado = !data.data.editable;

            actualizarBotonBloqueo();
            aplicarBloqueoUI();
            

            //Historial
            const persona = await obtenerPersonaPorCodigo(codPersona);
            const nombreCompleto = persona 
                ? `${persona.nombre} ${persona.apellido}`
                : `Usuario ${codPersona}`;

            const accion = canvasBloqueado
                ? "Bloquear Canvas"
                : "Desbloquear Canvas";

            const detalle = canvasBloqueado
                ? `El usuario ${nombreCompleto} bloqueó el canvas "${canvasActual.nomCanvas}".`
                : `El usuario ${nombreCompleto} desbloqueó el canvas "${canvasActual.nomCanvas}".`;

            await registrarHistorialTarea(id, accion, detalle);
            // ============================================

        } catch (error) {
            console.error("Error al cambiar estado de bloqueo:", error);
            alert("No se pudo actualizar el estado del canvas");
        }

    });
}

// === EXPORTAR CANVAS ===
document.getElementById("btnExportarCanvas")?.addEventListener("click", async () => {
    const codCanvas = obtenerIdCanvasActual(); 

    if (!codCanvas || isNaN(codCanvas)) {
        alert("❌ No se pudo obtener el ID del canvas actual.");
        return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Tu sesión ha expirado. Inicia sesión nuevamente.");
        return;
    }

    try {
        const response = await fetch(`http://localhost:8080/api/sc/canvas/${codCanvas}/exportar`, {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,   // 👈 TOKEN AQUI
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            const errorText = await response.text();
            console.error("❌ Error server:", errorText);
            alert("No se pudo exportar el canvas.");
            return;
        }

        // Recibir archivo JSON
        const blob = await response.blob();
        const url = window.URL.createObjectURL(blob);

        // Crear descarga
        const a = document.createElement("a");
        a.href = url;
        a.download = `canvas_${codCanvas}.json`;
        document.body.appendChild(a);
        a.click();

        // Eliminar el enlace temporal
        a.remove();
        window.URL.revokeObjectURL(url);

        console.log("✅ Canvas exportado correctamente");

    } catch (error) {
        console.error("❌ Error al exportar:", error);
        alert("Hubo un problema al exportar el canvas.");
    }
});

// === IMPORTAR CANVAS ===
document.getElementById("btnImportarCanvas")?.addEventListener("click", () => {
  document.getElementById("inputImportarCanvas").click();
});

document.getElementById("inputImportarCanvas")?.addEventListener("change", async (event) => {
    const file = event.target.files[0];
    if (!file) return;

    const token = localStorage.getItem("token");
    if (!token) {
        alert("Tu sesión expiró. Inicia sesión nuevamente.");
        return;
    }

    // Preparar el archivo para enviar
    const formData = new FormData();
    formData.append("file", file);

    try {
        const response = await fetch("http://localhost:8080/api/sc/canvas/importar", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`  // 👈 TOKEN AQUÍ
                // ❗ NO colocar "Content-Type": multipart/form-data (Se rompe)
            },
            body: formData
        });

        if (!response.ok) throw new Error("Error al importar el canvas");

        const data = await response.json();
        console.log("📦 Importación OK:", data);

        Swal.fire({
            icon: "success",
            title: "Canvas importado correctamente",
            timer: 1500,
            showConfirmButton: false
        });

        const nuevoId = data?.data?.codCanvas;

        setTimeout(() => {
            window.location.href = `/html/canvas-detalle.html?id=${nuevoId}`;
        }, 1500);

    } catch (error) {
        console.error(error);
        Swal.fire({
            icon: "error",
            title: "Error al importar",
            text: "Ocurrió un problema al importar el canvas. Revisa el archivo.",
            confirmButtonText: "Ok"
        });
    }
});

  await cargarSelectorCanvas();
  await cargarCanvasDetalle(canvasId);
});

let etapasGlobal = [];
let draggedTask = null;
let tareaSeleccionada = null;
let canvasActual = null;
let canvasBloqueado = false;
let listaCanvasGlobal = [];
let btnBloqueo = null;
let tareaEnEdicion = null;

// Obtener el ID desde el parámetro ?id= en la URL
function obtenerIdCanvasActual() {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('id');
}

// =============================
// SELECTOR DE CANVAS
// =============================
async function cargarSelectorCanvas() {
    try {
        const res = await fetch("/api/sc/canvas", {
  headers: getAuthHeaders()
});
        const data = await res.json();

        const lista = data?.content ?? [];

        console.log("✅ Canvas obtenidos:", lista);

        const select = document.getElementById("selector-canvas");
        if (!select) return;

        select.innerHTML = '<option value="">Seleccionar canvas…</option>';

        lista.forEach(c => {
            const opt = document.createElement("option");
            opt.value = c.codCanvas;
            opt.textContent = c.nomCanvas;
            select.appendChild(opt);
        });

        // Seleccionar el canvas actual
        const urlParams = new URLSearchParams(window.location.search);
        const idActual = urlParams.get("id");
        if (idActual) select.value = idActual;

        // Cambiar de canvas
        select.addEventListener("change", () => {
            if (select.value) {
                window.location.href = `canvas-detalle.html?id=${select.value}`;
            }
        });

    } catch (error) {
        console.error("Error cargando selector de canvas:", error);
    }
}
function mostrarToast(mensaje, tipo = "info") {
  const toast = document.getElementById("toast");
  toast.textContent = mensaje;

  // Cambia el color según tipo
  const colores = {
    success: "#7dcfb6",
    error: "#ff9aa2",
    info: "#a8d8ea"
  };
  toast.style.borderLeftColor = colores[tipo] || colores.info;

  toast.classList.add("show");
  toast.classList.remove("hidden");

  setTimeout(() => {
    toast.classList.remove("show");
    setTimeout(() => toast.classList.add("hidden"), 300);
  }, 3000);
}

function actualizarBotonBloqueo() {
  if (!btnBloqueo) return;

    if (canvasBloqueado) {
        btnBloqueo.textContent = "🔒 Bloqueado";
        btnBloqueo.classList.remove("btn-success");
        btnBloqueo.classList.add("btn-warning");
    } else {
        btnBloqueo.textContent = "✅ Desbloqueado";
        btnBloqueo.classList.remove("btn-warning");
        btnBloqueo.classList.add("btn-success");
    }
}

function aplicarBloqueoUI() {
  const botones = document.querySelectorAll("button, input, textarea, select");
  const contenedorKanban = document.getElementById("kanban-container");

  if (canvasBloqueado) {
    botones.forEach(btn => {

      if (
          btn.id === "btn-bloqueo" || 
          btn.id === "selector-canvas"
      ) return;

      btn.disabled = true;
    });

    contenedorKanban.classList.add("bloqueado");
    document.body.classList.add("modo-bloqueado");

  } else {
    botones.forEach(btn => btn.disabled = false);
    contenedorKanban.classList.remove("bloqueado");
    document.body.classList.remove("modo-bloqueado");
  }
}

async function toggleEditable(canvasId, editable) {
  try {
    const response = await fetch(`/api/sc/canvas/${canvasId}/editable?editable=${editable}`, {
      method: 'PUT', 
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`, 
      },
    });

    if (!response.ok) {
      throw new Error('Error al actualizar editable');
    }

    const data = await response.json();
    console.log('Canvas actualizado:', data);
  } catch (error) {
    console.error('Error:', error);
  }
}

/* === CARGAR CANVAS Y ETAPAS === */
async function cargarCanvasDetalle(id) {
  try {
    const res = await fetch(`/api/sc/canvas/${id}`, {
  headers: getAuthHeaders()
});

if (!res.ok) {
  throw new Error(`Error ${res.status}: no se pudo obtener el canvas`);
}

const data = await res.json();
const canvas = data.data || data;

    if (!canvas) throw new Error("Datos inválidos");

    canvasActual = canvas;
    canvasBloqueado = !canvasActual.editable;

    document.getElementById("canvas-nombre").textContent = canvas.nomCanvas;

    actualizarBotonBloqueo();
    aplicarBloqueoUI();

    canvas.etapasPersonalizadas = canvas.etapasPersonalizadas || [];
    etapasGlobal = canvas.etapasPersonalizadas.map(e => ({ ...e, tareas: e.tareas || [] }));

    renderizarEtapas(etapasGlobal);
    poblarSelectEtapas(etapasGlobal);
    actualizarProgresoCanvasDesdeBackend(canvas);

  } catch (error) {
    console.error("Error al cargar canvas:", error);
  }
}

/* === RENDERIZAR ETAPAS Y TAREAS === */
function renderizarEtapas(etapas) {
  if (!Array.isArray(etapas)) etapas = [];

  etapas = [...etapas].sort((a, b) => {
  etapas = [...etapas].sort((a, b) => a.numEtapa - b.numEtapa);
});
  const container = document.getElementById("kanban-container");
  container.innerHTML = "";

  if (!etapas.length) {
    console.warn("⚠️ No hay etapas para renderizar");
    return;
  }

  const coloresEncabezado = [
    "#A3CEF1", // azul pastel
    "#C9E4DE", // verde menta
    "#F6D6AD", // durazno
    "#F9C6C9", // rosa claro
    "#E5D4EF", // lila suave
    "#FFF1C1", // amarillo crema
    "#CBE2B0", // verde claro
    "#FFD6A5"  // melón pastel
  ];

  etapas.forEach((etapa, index) => {
  const columna = document.createElement("div");
  columna.className = "kanban-column";
  columna.dataset.etapaId = etapa.codEtapa;

  const header = document.createElement("div");
  header.className = "kanban-header";
  header.textContent = etapa.nomEtapa || "Etapa sin nombre";

  header.style.background = coloresEncabezado[index % coloresEncabezado.length];
  header.style.color = "#333"; // texto oscuro para contraste
  header.style.fontWeight = "600";

  const tareasContainer = document.createElement("div");
  tareasContainer.className = "kanban-tasks";
  tareasContainer.dataset.etapaId = etapa.codEtapa;
  tareasContainer.addEventListener("dragover", handleDragOver);
  tareasContainer.addEventListener("drop", handleDrop);
  
  /* === CREAR TARJETA === */
  (etapa.tareas || []).forEach((tarea) => {
    const card = document.createElement("div");
    card.className = "task-card";
    card.draggable = true;
    card.dataset.id = tarea.codTarea;

    const descripcionTruncada = tarea.desTarea
      ? (tarea.desTarea.length > 100
          ? tarea.desTarea.substring(0, 100) + "..."
          : tarea.desTarea)
      : "Sin descripción";

    const esCompleta = tarea.vigente === 0;

    card.innerHTML = `
    <div class="task-title"><strong>${tarea.nomTarea}</strong></div>
    <div class="task-desc">${descripcionTruncada}</div>
    <div class="task-actions">
      <button class="btn-view" title="Ver">🔎</button>
      <button class="btn-edit" title="Editar">✏️</button>
      <button class="btn-delete" title="Eliminar">🗑️</button>

      <!-- ✔ / ↩ NUEVO BOTÓN -->
      <button class="btn-toggle" title="${esCompleta ? 'Reabrir' : 'Completar'}">
        ${esCompleta ? "↩️" : "✔️"}
      </button>
    </div>
    `;


    card.addEventListener("dragstart", handleDragStart);
    card.addEventListener("dragend", handleDragEnd);

    card.querySelector(".btn-view").addEventListener("click", () => verTarea(tarea));
    card.querySelector(".btn-edit").addEventListener("click", () => editarTarea(tarea));
    card.querySelector(".btn-delete").addEventListener("click", () => eliminarTarea(tarea.codTarea));
    card.querySelector(".btn-toggle").addEventListener("click", () => cambiarEstadoTarea(tarea.codTarea, esCompleta));

    tareasContainer.appendChild(card);
  });

  columna.appendChild(header);
  columna.appendChild(tareasContainer);
  container.appendChild(columna);
});
}

function getAuthHeaders() {
  const token = localStorage.getItem('token');
  return {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  };
}

/* === FUNCIONES DE ACCIONES EN TAREAS === */

function verTarea(tarea) {
  if (canvasBloqueado) {
    mostrarToast("El canvas está bloqueado 🔒 No puedes visualizar tareas.", "error");
    return;
  }
  document.getElementById("ver-nombre").textContent = tarea.nomTarea;
  document.getElementById("ver-descripcion").textContent = tarea.desTarea || "Sin descripción";
  abrirModal("modal-ver");
}

function editarTarea(tarea) {
  if (canvasBloqueado) {
    mostrarToast("El canvas está bloqueado 🔒 No puedes editar tareas.", "error");
    return;
  }
  tareaEnEdicion = tarea;
  document.getElementById("editar-nombre").value = tarea.nomTarea;
  document.getElementById("editar-descripcion").value = tarea.desTarea || "";

  // Guardamos el ID temporalmente
  tareaSeleccionada = tarea.codTarea;

  abrirModal("modal-editar");
}

async function guardarEdicion() {
  if (!tareaEnEdicion) {
    console.error("No hay tarea seleccionada");
    return;
  }

  // --- Datos ANTES del cambio ---
    const nombreAntes = tareaEnEdicion.nomTarea;
    const descAntes = tareaEnEdicion.desTarea || "Sin descripción";

    // --- Datos NUEVOS ---
    const nombreDespues = document.getElementById("editar-nombre").value.trim();
    const descDespues = document.getElementById("editar-descripcion").value.trim();

  const nuevaTarea = {
    nomTarea: document.getElementById("editar-nombre").value,
    desTarea: document.getElementById("editar-descripcion").value,
    codEtapa: tareaEnEdicion.codEtapa
  };

  try {
    const payload = {
      nomTarea: nuevaTarea.nomTarea,
      desTarea: nuevaTarea.desTarea,
      codEtapa: nuevaTarea.codEtapa,
      vigente: tareaEnEdicion.vigente ?? 1,
      numTarea: tareaEnEdicion.numTarea ?? 1,
      etapa: { codEtapa: nuevaTarea.codEtapa }
    };

    const response = await fetch(`/api/sc/tarea/${tareaEnEdicion.codTarea}`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": "Bearer " + localStorage.getItem("token")
      },
      body: JSON.stringify(payload)
    });

    if (!response.ok) throw new Error("Error al actualizar tarea");

    const canvasId = new URLSearchParams(window.location.search).get("id");
    await cargarCanvasDetalle(canvasId);
    //historial
    await registrarHistorialTarea(
        canvasActual.codCanvas,
        "Editar Tarea",
        `Se editó la tarea:
        • Antes: "${nombreAntes}" – "${descAntes}"
        • Después: "${nombreDespues}" – "${descDespues || 'Sin descripción'}"`
    );



    cerrarModal("modal-editar");

  } catch (error) {
    console.error("❌ Error al actualizar tarea:", error);
    alert("Error al actualizar tarea");
  }
}

function eliminarTarea(codTarea) {
  if (canvasBloqueado) {
    mostrarToast("El canvas está bloqueado🔒 No puedes eliminar tareas.", "error");
    return;
  }
  tareaSeleccionada = codTarea;
  // Guardamos el nombre antes de eliminar
    const tarea = etapasGlobal.flatMap(e => e.tareas).find(t => t.codTarea == codTarea);
    window.nombreTareaAEliminar = tarea?.nomTarea || "(desconocida)";

  abrirModal("modal-eliminar");
}

async function confirmarEliminacion() {
  const token = localStorage.getItem("token");

  if (!token) {
    alert("Tu sesión ha expirado. Inicia sesión nuevamente.");
    return;
  }

  try {
    const res = await fetch(`/api/sc/tarea/${tareaSeleccionada}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",   
        "Authorization": `Bearer ${token}`
      }
    });

    if (!res.ok) {
  const errorText = await res.text();
  console.error("❌ Error server:", errorText);  
  alert("No se pudo eliminar la tarea: " + errorText);
  return;
}


    cerrarModal("modal-eliminar");

    const canvasId = new URLSearchParams(window.location.search).get("id");
    
    await cargarCanvasDetalle(canvasId);
    //historial
    await registrarHistorialTarea(
        obtenerIdCanvasActual(),
        "Eliminar Tarea",
        `Se eliminó la tarea "${window.nombreTareaAEliminar}".`
    );


    mostrarToast("Tarea eliminada correctamente", "success");

  } catch (error) {
    console.error("❌ Error al eliminar tarea:", error);
  }
}

/* === DRAG & DROP === */
function handleDragStart(e) {
  if (canvasBloqueado) return;
  draggedTask = e.target;
  e.target.classList.add("dragging");
}

function handleDragEnd(e) {
  e.target.classList.remove("dragging");
  draggedTask = null;
}

function handleDragOver(e) {
  e.preventDefault();
}

function handleDrop(e) {
  e.preventDefault();
  const dropZone = e.currentTarget;
  if (draggedTask) {
    dropZone.appendChild(draggedTask);
    const etapaDestino = dropZone.dataset.etapaId;
    actualizarEtapaTarea(draggedTask.dataset.id, etapaDestino);
  }
}

/* === SELECT ETAPAS === */
function poblarSelectEtapas(etapas) {
  const select = document.getElementById("etapa-tarea");
  if (!select) return;
  select.innerHTML = '<option value="">-- Seleccionar etapa --</option>';
  etapas.forEach(etapa => {
    const opt = document.createElement("option");
    opt.value = etapa.codEtapa;
    opt.textContent = etapa.nomEtapa;
    select.appendChild(opt);
  });
}

/* === MODAL GENERAL === */
function abrirModal(idModal = "modal-tarea") {
  console.log("✅ abrirModal fue llamado con:", idModal);

  const modal = document.getElementById(idModal);
  if (modal) {
    modal.classList.remove("hidden");
    modal.classList.add("show");
  } else {
    console.error("❌ No se encontró el modal con id:", idModal);
  }
}

function cerrarModal(idModal = "modal-tarea") {
  const modal = document.getElementById(idModal);
  if (modal) {
    modal.classList.add("hidden");
    modal.classList.remove("show");
  } else {
    console.error("❌ No se encontró el modal con id:", idModal);
  }
}

/* === GUARDAR TAREA === */
async function guardarTarea(e) {
  e.preventDefault();

  const nombre = document.getElementById("nombre-tarea").value.trim();
  const descripcion = document.getElementById("descripcion-tarea").value.trim();
  const etapaId = document.getElementById("etapa-tarea").value;
  const canvasId = new URLSearchParams(window.location.search).get("id");

  const user = getCurrentUser();
  const codPersona = user?.codPersona;


  if (!nombre || !etapaId) {
    alert("Por favor completa el nombre y selecciona una etapa.");
    return;
  }

  const token = localStorage.getItem("token");

  if (!token) {
    alert("Tu sesión ha expirado. Vuelve a iniciar sesión.");
    return;
  }

  const tareaDTO = {
    nomTarea: nombre,
    desTarea: descripcion,
    codEtapa: parseInt(etapaId),
    codPersona: codPersona,
    codCanvas: canvasId,
    numTarea: 1,
    vigente: 1
  };

  try {
    const response = await fetch("/api/sc/tarea/crear", {
      method: "POST",
      headers: { 
        "Content-Type": "application/json",
        "Authorization": "Bearer " + token  
      },
      body: JSON.stringify(tareaDTO),
    });

    const result = await response.json();

    if (!response.ok) {
      alert("Error al crear la tarea: " + (result.message || "Error desconocido"));
      return;
    }

    // Limpiar campos
    document.getElementById("nombre-tarea").value = "";
    document.getElementById("descripcion-tarea").value = "";
    document.getElementById("etapa-tarea").selectedIndex = 0;

    cerrarModal();
    await cargarCanvasDetalle(canvasId);
//historial
    const etapaNombre = etapasGlobal.find(e => e.codEtapa == etapaId)?.nomEtapa || "Desconocida";

    await registrarHistorialTarea(
        canvasId,
        "Crear Tarea",
        `Se creó la tarea "${nombre}" en la etapa "${etapaNombre}".`
    );


  } catch (err) {
    console.error("Error de conexión:", err);
    alert("Error al conectar con el servidor");

    

  }
}

/* === ACTUALIZAR ETAPA AL MOVER === */
async function actualizarEtapaTarea(codTarea, nuevoCodEtapa) {
    const token = localStorage.getItem("token");

    const response = await fetch(`/api/sc/tarea/${codTarea}/mover/${nuevoCodEtapa}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}` 
        }
    });

    if (!response.ok) {
        console.error("Error al mover tarea:", await response.text());
    }
    //historial
    const data = await response.json();
    
    // 🔥 Buscar nombre de la tarea movida
    const tarea = etapasGlobal.flatMap(e => e.tareas).find(t => t.codTarea == codTarea);
    const nombreTarea = tarea?.nomTarea || "(desconocida)";

    // 🔥 Buscar nombres de etapas
    const etapaOrigen = etapasGlobal.find(e => e.codEtapa == tarea.codEtapa)?.nomEtapa || "Desconocida";
    const etapaDestinoNom = etapasGlobal.find(e => e.codEtapa == nuevoCodEtapa)?.nomEtapa || "Desconocida";

    // 🔥 Registro de historial
    await registrarHistorialTarea(
        canvasActual.codCanvas,
        "Mover Tarea",
        `La tarea "${nombreTarea}" fue movida de "${etapaOrigen}" a "${etapaDestinoNom}".`
    );



    return data;

}

async function cambiarEstadoTarea(codTarea, estaCompleta) {
  const token = localStorage.getItem("token");

  //Datos para historial
  const tarea = etapasGlobal.flatMap(e => e.tareas).find(t => t.codTarea == codTarea);
  const nombreTarea = tarea?.nomTarea || "(desconocida)";

  // Obtener usuario
  const user = getCurrentUser();
  const codPersona = user?.codPersona ?? "(desconocido)";

  // Obtener datos del usuario (nombre y apellido)
  const persona = await obtenerPersonaPorCodigo(codPersona);
  const nombreCompleto = persona
      ? `${persona.nombre} ${persona.apellido}`
      : `Usuario ${codPersona}`;

  const body = {
    completar: !estaCompleta   // true = completar, false = reabrir
  };

  try {
    const res = await fetch(`/api/sc/tarea/${codTarea}/estado`, {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`
      },
      body: JSON.stringify(body)
    });

    if (!res.ok) {
      mostrarToast("Error al actualizar estado de la tarea");
      return;
    }

    const data = await res.json();

    //  HISTORIAL
    await registrarHistorialTarea(
        canvasActual.codCanvas,
        "Cambio de estado de tarea",
        `El usuario ${nombreCompleto} ${!estaCompleta ? "completó" : "reabrió"} la tarea "${nombreTarea}".`
    );


    mostrarToast(
      !estaCompleta ? "Tarea completada 🎉" : "Tarea reabierta ↩️",
      "success"
    );

    const canvasId = new URLSearchParams(window.location.search).get("id");
    await cargarCanvasDetalle(canvasId);

  } catch (error) {
    console.error("❌ Error al cambiar estado:", error);
  }
}

function actualizarProgresoCanvasDesdeBackend(canvas) {
    const porcentaje = Math.round(canvas.porcentajeProgreso ?? 0);
    const total = canvas.totalTareas ?? 0;
    const completadas = canvas.tareasCompletadas ?? 0;

    document.getElementById("progreso-porcentaje").textContent = porcentaje + "%";
    document.getElementById("progress-fill-global").style.width = porcentaje + "%";

    document.getElementById("tareas-total").textContent = total;
    document.getElementById("tareas-completadas-total").textContent = completadas;
}

/* === VOLVER A INICIO === */
function volverAInicio() {
  window.location.href = "/html/inicio-analista.html";
}


/* === HISTORIAL === */

async function verHistorial(codCanvas) {
    try {
        const token = localStorage.getItem("token");

        const res = await fetch(`/api/sc/historial?codCanvas=${codCanvas}`, {
            headers: {
                "Authorization": `Bearer ${token}`
            }
        });

        if (!res.ok) {
            console.error("Error al cargar historial:", await res.text());
            AlertManager.error("Error al cargar historial");
            return;
        }

        const historial = await res.json();

        mostrarModalHistorial(historial);

    } catch (err) {
        console.error("Error:", err);
        AlertManager.error("Error en servidor");
    }
}

function abrirModalHistorial() {
    const codCanvas = obtenerIdCanvasActual();
    if (!codCanvas) return AlertManager.error("No hay canvas seleccionado");
    verHistorial(codCanvas);
}

function mostrarModalHistorial(historial) {
    const modal = document.getElementById('modal-historial');
    const container = document.getElementById('historial-container');

    container.innerHTML = '';

    if (!historial.length) {
        container.innerHTML = '<p class="text-center text-gray">Sin cambios registrados</p>';
    } else {
        historial.forEach(item => {
            const div = document.createElement('div');
            div.className = 'card p-3 mb-2';
            div.innerHTML = `
                <div class="flex-between mb-1">
                    <strong>${item.accion}</strong>
                    <span class="text-sm text-gray">${new Date(item.fecAccion).toLocaleString()}</span>
                </div>
                <p class="text-sm">${item.detalle}</p>
                <p class="text-xs text-gray">Usuario: ${item.codPersona}</p>
            `;
            container.appendChild(div);
        });
    }

    modal.classList.remove("hidden");
    modal.classList.add("show");
}

function cerrarModalHistorial() {
    const modal = document.getElementById("modal-historial");
    modal.classList.add("hidden");
    modal.classList.remove("show");
}




//Funcion para registrar historial
async function registrarHistorialTarea(codCanvas, accion, detalle) {
    const user = getCurrentUser(); 

    const payload = {
        codPersona: user.codPersona,
        accion: accion,
        detalle: detalle,
        canvas: { codCanvas: codCanvas }
    };

    return await HttpClient.post("/sc/historial", payload);
}

function getCurrentUser() {
    const userStr = localStorage.getItem("user");
    return userStr ? JSON.parse(userStr) : null;
}


// Funcion para obtener nombre
async function obtenerPersonaPorCodigo(codPersona) {
    const token = localStorage.getItem("token");

    const res = await fetch(`/api/t/empleado/${codPersona}`, {
        headers: { "Authorization": `Bearer ${token}` }
    });

    if (!res.ok) return null;

    return await res.json();
}
