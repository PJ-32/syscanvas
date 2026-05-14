// =====================================================
// CARGAR PERFIL DEL JEFE
// =====================================================

async function cargarPerfilJefe() {
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

    // mostrar modal con la clase correcta
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
    const token = localStorage.getItem("token");

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
    cargarPerfilJefe();
}

/*FOto */
document.getElementById("input-foto").addEventListener("change", async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    // Validaciones básicas (tamaño, tipo, etc.)
    if (!file.type.startsWith("image/")) {
        alert("Por favor seleccione un archivo de imagen válido.");
        return;
    }

    const reader = new FileReader();

    reader.onload = async () => {
        const base64Full = reader.result; // data:image/png;base64,AAAA...
        const base64 = base64Full.split(",")[1]; // nos quedamos solo con la parte Base64

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
                const txt = await res.text();
                console.error("Error al actualizar foto:", txt);
                alert("Error al subir la foto");
                return;
            }

            // Actualizamos la imagen en pantalla
            document.getElementById("foto-perfil").src = base64Full;
            document.getElementById("mensaje-foto").textContent = "Foto actualizada correctamente";
            
            cargarFotoTopbar();
            
        } catch (err) {
            console.error("Error de red al subir foto:", err);
            alert("Error de conexión al subir la foto");
        }
    };

    reader.readAsDataURL(file);
});

async function cargarFotoPerfil() {
    const user = getCurrentUser();
    if (!user || !user.codPersona) return;

    try {
        const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
            headers: {
                "Authorization": `Bearer ${user.token}`
            }
        });

        if (!res.ok) {
            console.error("Error obteniendo foto:", await res.text());
            return;
        }

        const data = await res.json();
        const img = document.getElementById("foto-perfil");
        if (!img) return;

        if (data.fotoBase64) {
            // Puedes ajustar image/png si guardas también JPG
            img.src = `data:image/png;base64,${data.fotoBase64}`;
        } else {
            img.src = "/uploads/default-foto.png";
        }
    } catch (err) {
        console.error("Error de red al obtener la foto:", err);
    }
}

async function eliminarFoto() {
    const user = getCurrentUser();
    if (!user || !user.codPersona) {
        alert("Sesión no válida.");
        return;
    }

    if (!confirm("¿Seguro que deseas eliminar tu foto de perfil?")) return;

    try {
        const res = await fetch(`/api/t/empleado/${user.codPersona}/foto`, {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${user.token}`
            }
        });

        if (!res.ok) {
            alert("No se pudo eliminar la foto.");
            return;
        }

        // Reemplazar por la imagen por defecto
        document.getElementById("foto-perfil").src = "/uploads/default-foto.png";

        const fotoTop = document.getElementById("user-photo-topbar");
        if (fotoTop) fotoTop.src = "/uploads/default-foto.png";


        alert("Foto eliminada con éxito.");

    } catch (err) {
        console.error("Error eliminando la foto:", err);
        alert("Error inesperado.");
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
        if (!fotoTop) return;

        if (!res.ok) {
            console.warn("No hay foto, usando default.");
            fotoTop.src = "/uploads/default-foto.png";
            return;
        }

        const data = await res.json();

        if (data.fotoBase64) {
            fotoTop.src = `data:image/png;base64,${data.fotoBase64}`;
        } else {
            fotoTop.src = "/uploads/default-foto.png";
        }

    } catch (err) {
        console.error("Error cargando foto del topbar:", err);
        document.getElementById("user-photo-topbar").src = "/uploads/default-foto.png";
    }
}




// =====================================================
// INICIALIZACIÓN (SOLO UNA VEZ)
// =====================================================

document.addEventListener("DOMContentLoaded", () => {
    checkAuth();
    cargarPerfilJefe();
    setTimeout(cargarFotoPerfil, 150);
    cargarFotoTopbar();

    // Editar datos
    document.getElementById("btn-editar-datos").addEventListener("click", habilitarEdicion);

    // Cancelar → volver al inicio
    document.getElementById("btn-cancelar-edicion").addEventListener("click", () => {
        window.location.href = "/html/inicio-jefe.html";
    });

    // Guardar modal
    document.getElementById("form-editar-datos").addEventListener("submit", guardarCambios);

    // Foto (a futuro)
    document.getElementById("btn-modificar-foto").addEventListener("click", () =>
        document.getElementById("input-foto").click()
    );

    document.getElementById("input-foto").addEventListener("change", () =>
        document.getElementById("mensaje-foto").textContent = "Foto seleccionada"
    );

    document.getElementById("btn-eliminar-foto").addEventListener("click", eliminarFoto);

}

);
