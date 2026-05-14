// =====================================================
// CARGAR PERFIL DEL ANALISTA
// =====================================================

async function cargarPerfilAnalista() {
    const user = getCurrentUser();
    const token = localStorage.getItem("token");

    if (!user || !user.codPersona) {
        console.error("⚠ No se encontró codPersona del usuario logueado.");
        return;
    }

    const res = await fetch(`/api/t/empleado/${user.codPersona}`, {
        headers: {
            "Authorization": `Bearer ${token}`
        }
    });

    if (!res.ok) {
        console.error("Error obteniendo datos del empleado");
        return;
    }

    const emp = await res.json();

    // Llenar campos visibles
    setValue("codpers", emp.codPersona);
    setValue("nombre", emp.nombre);
    setValue("apellido", emp.apellido);
    setValue("dni", emp.dni);
    setValue("direcc", emp.direccion);
    setValue("hobby", emp.hobby);
    setValue("email", emp.email);
    setValue("celular", emp.celular);
    setValue("password", "********");

    if (emp.fecNac) {
        const f = new Date(emp.fecNac);
        setValue("fecNac", f.toISOString().split("T")[0]);
    }

    // Foto del perfil
    document.getElementById("foto-perfil").src =
        `/api/t/empleado/${user.codPersona}/foto?${Date.now()}`;
}

function setValue(id, value) {
    const el = document.getElementById(id);
    if (el) el.value = value || "";
}

// =====================================================
// MODAL: ABRIR
// =====================================================

function habilitarEdicion() {
    document.getElementById("edit-nombre").value = document.getElementById("nombre").value;
    document.getElementById("edit-apellido").value = document.getElementById("apellido").value;
    document.getElementById("edit-dni").value = document.getElementById("dni").value;
    document.getElementById("edit-direcc").value = document.getElementById("direcc").value;
    document.getElementById("edit-hobby").value = document.getElementById("hobby").value;
    document.getElementById("edit-email").value = document.getElementById("email").value;
    document.getElementById("edit-celular").value = document.getElementById("celular").value;
    document.getElementById("edit-fecNac").value = document.getElementById("fecNac").value;

    document.getElementById("modal-editar-datos").classList.add("show");
}

function cerrarModalEditarDatos() {
    document.getElementById("modal-editar-datos").classList.remove("show");
}

// =====================================================
// GUARDAR CAMBIOS (PUT)
// =====================================================

async function guardarCambios(e) {
    e.preventDefault();

    const user = getCurrentUser();

    const payload = {
        nombre: document.getElementById("edit-nombre").value,
        apellido: document.getElementById("edit-apellido").value,
        dni: document.getElementById("edit-dni").value,
        fecNac: document.getElementById("edit-fecNac").value,
        direccion: document.getElementById("edit-direcc").value,
        hobby: document.getElementById("edit-hobby").value,
        email: document.getElementById("edit-email").value,
        celular: document.getElementById("edit-celular").value
    };

    const res = await fetch(`/api/t/empleado/${user.codPersona}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${localStorage.getItem("token")}`
        },
        body: JSON.stringify(payload)
    });

    if (!res.ok) {
        alert("Error guardando los datos.");
        return;
    }

    alert("Datos actualizados correctamente.");
    cerrarModalEditarDatos();
    cargarPerfilAnalista();
}

// =====================================================
// FOTO DE PERFIL
// =====================================================

document.getElementById("input-foto").addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    if (!file.type.startsWith("image/")) {
        alert("Por favor seleccione un archivo de imagen válido.");
        return;
    }

    const reader = new FileReader();

    reader.onload = async () => {
        const base64Full = reader.result;
        const base64 = base64Full.split(",")[1];

        const user = getCurrentUser();

        try {
            const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    "Authorization": `Bearer ${user.token}`
                },
                body: JSON.stringify({ fotoBase64: base64 })
            });

            if (!res.ok) {
                console.error("Error actualizando foto:", await res.text());
                alert("Error al subir la foto");
                return;
            }

            document.getElementById("foto-perfil").src = base64Full;
            document.getElementById("mensaje-foto").textContent = "Foto actualizada correctamente";

            cargarFotoTopbar();

        } catch (err) {
            console.error("Error:", err);
            alert("Error de conexión al subir la foto");
        }
    };

    reader.readAsDataURL(file);
});

async function cargarFotoPerfil() {
    const user = getCurrentUser();
    if (!user) return;

    try {
        const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
            headers: { "Authorization": `Bearer ${user.token}` }
        });

        if (!res.ok) return;

        const data = await res.json();

        const img = document.getElementById("foto-perfil");
        if (!img) return;

        img.src = data.fotoBase64
            ? `data:image/png;base64,${data.fotoBase64}`
            : "/uploads/default-foto.png";

    } catch (err) {
        console.error("Error obteniendo foto:", err);
    }
}

async function eliminarFoto() {
    const user = getCurrentUser();
    if (!user.codPersona) {
        alert("Sesión no válida");
        return;
    }

    if (!confirm("¿Seguro que deseas eliminar tu foto?")) return;

    try {
        const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${user.token}`
            }
        });

        if (!res.ok) {
            alert("No se pudo eliminar la foto");
            return;
        }

        document.getElementById("foto-perfil").src = "/uploads/default-foto.png";
        cargarFotoTopbar();
        alert("Foto eliminada");

    } catch (err) {
        console.error("Error:", err);
        alert("Error inesperado");
    }
}

async function cargarFotoTopbar() {
    const user = getCurrentUser();
    if (!user || !user.codPersona) return;

    try {
        const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
            headers: { "Authorization": `Bearer ${user.token}` }
        });

        const fotoTop = document.getElementById("user-photo-topbar");

        if (!res.ok) {
            fotoTop.src = "/uploads/default-foto.png";
            return;
        }

        const data = await res.json();

        fotoTop.src = data.fotoBase64
            ? `data:image/png;base64,${data.fotoBase64}`
            : "/uploads/default-foto.png";

    } catch (err) {
        console.error("Error:", err);
    }
}

// =====================================================
// INICIALIZACIÓN
// =====================================================

document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    cargarPerfilAnalista();
    setTimeout(cargarFotoPerfil, 150);
    cargarFotoTopbar();

    // Editar
    document.getElementById("btn-editar-datos").addEventListener("click", habilitarEdicion);

    // Cancelar -> volver al inicio analista
    document.getElementById("btn-cancelar-edicion").addEventListener("click", () => {
        window.location.href = "/html/inicio-analista.html";
    });

    // Guardar edición
    document.getElementById("form-editar-datos").addEventListener("submit", guardarCambios);

    // Modificar foto
    document.getElementById("btn-modificar-foto").addEventListener("click", () =>
        document.getElementById("input-foto").click()
    );

    // Texto cuando se selecciona foto
    document.getElementById("input-foto").addEventListener("change", () =>
        document.getElementById("mensaje-foto").textContent = "Foto seleccionada"
    );

    // Eliminar foto
    document.getElementById("btn-eliminar-foto").addEventListener("click", eliminarFoto);
});

