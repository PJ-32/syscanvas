/*
 * SYSCANVAS - auth.js
 * Sistema de autenticación centralizado
 */

// Configuración centralizada
const CONFIG = {
    API_URL: 'http://localhost:8080/api',
    ENDPOINTS: {
        LOGIN: '/auth/login',
        CAMBIAR_PASSWORD: '/auth/cambiar-password'
    },
    STORAGE_KEYS: {
        TOKEN: 'token',
        ROL: 'rol',
        COD_PERSONA: 'codPersona',
        NOMBRE: 'nombreCompleto'
    }
};

// Generar íconos
function iconify(name, size = 20) {
    return `<span class="iconify" data-icon="${name}" style="font-size:${size}px;"></span>`;
}

// ==========================================
// UTILIDADES REUTILIZABLES
// ==========================================

/**
 * Manejo centralizado de alertas
 */
class AlertManager {
    static show(type, message, containerId = null) {
        const alertId = containerId || (type === 'error' ? 'alert-error' : 'alert-success');
        const alertEl = document.getElementById(alertId);
        
        if (!alertEl) return;
        
        alertEl.textContent = message;
        alertEl.classList.add('show');
        
        setTimeout(() => {
            alertEl.classList.remove('show');
        }, 5000);
    }
    
    static hideAll() {
        document.querySelectorAll('.alert').forEach(alert => {
            alert.classList.remove('show');
        });
    }
    
    static error(message, containerId = null) {
        this.show('error', message, containerId);
    }
    
    static success(message, containerId = null) {
        this.show('success', message, containerId);
    }
}

/**
 * Manejo del localStorage
 */
class StorageManager {
    static set(key, value) {
        try {
            localStorage.setItem(key, value);
        } catch (error) {
            console.error('Error guardando en localStorage:', error);
        }
    }

    static get(key) {
        try {
            return localStorage.getItem(key);
        } catch (error) {
            console.error('Error leyendo localStorage:', error);
            return null;
        }
    }

    static remove(key) {
        localStorage.removeItem(key);
    }

    static clear() {
        localStorage.clear();
    }
}

/**
 * Cliente HTTP con manejo de errores
 */
class HttpClient {
    static async request(endpoint, options = {}) {
        const token = StorageManager.get(CONFIG.STORAGE_KEYS.TOKEN);
        
        const defaultOptions = {
            headers: {
                'Content-Type': 'application/json',
                ...(token && { 'Authorization': `Bearer ${token}` })
            }
        };
        
        const config = { ...defaultOptions, ...options };
        
        try {
            const response = await fetch(`${CONFIG.API_URL}${endpoint}`, config);

            let data = null;
            const contentType = response.headers.get('content-type');
            
            if (contentType && contentType.includes('application/json')) {
                const text = await response.text();
                if (text && text.length > 0) {
                    try {
                        data = JSON.parse(text);
                    } catch (e) {
                        console.error('Error parsing JSON:', e);
                        data = { mensaje: text };
                    }
                }
            } else {
                // Si no es JSON, leer como texto
                data = { mensaje: await response.text() };
            }
            
            // Manejo específico por código de estado
            if (!response.ok) {
                return this.handleErrorResponse(response.status, data);
            }
            
            return { success: true, data };
        } catch (error) {
            console.error('HTTP Error:', error);
            return { 
                success: false, 
                error: error.message || 'Error de conexión con el servidor. Verifique su conexión a internet.' 
            };
        }
    }
    
    /**
     * Manejo específico de errores por código HTTP
     */
    static handleErrorResponse(status, data) {
        let errorMessage;
        
        switch (status) {
            case 400: // Bad Request
                errorMessage = data?.mensaje || 'Datos inválidos. Verifique la información ingresada.';
                break;
                
            case 401: // Unauthorized
                errorMessage = data?.mensaje || 'Credenciales inválidas. Verifique su usuario y contraseña.';
                break;
                
            case 403: // Forbidden
                errorMessage = data?.mensaje || 'No tiene permisos para realizar esta acción.';
                break;
                
            case 404: // Not Found
                errorMessage = data?.mensaje || 'Recurso no encontrado.';
                break;
                
            case 409: // Conflict
                errorMessage = data?.mensaje || 'Ya existe un registro con esos datos.';
                break;
                
            case 500: // Internal Server Error
                errorMessage = data?.mensaje || 'Error interno del servidor. Contacte al administrador.';
                break;
                
            default:
                errorMessage = data?.mensaje || `Error ${status}: ${data?.error || 'Error desconocido'}`;
        }
        
        // Si hay errores múltiples (validaciones), combinarlos
        if (data?.errores && Array.isArray(data.errores)) {
            errorMessage = data.errores.join(', ');
        }
        
        return { 
            success: false, 
            error: errorMessage,
            status: status 
        };
    }

    static async post(endpoint, body) {
        return this.request(endpoint, {
            method: 'POST',
            body: JSON.stringify(body)
        });
    }
    
    static async get(endpoint) {
        return this.request(endpoint, { method: 'GET' });
    }
}

/**
 * Manejo de botones con estado de carga
 */
class ButtonManager {
    static setLoading(buttonId, loadingText = 'Cargando...') {
        const btn = document.getElementById(buttonId);
        if (!btn) return;
        
        btn.disabled = true;
        btn.dataset.originalText = btn.textContent;
        btn.innerHTML = `<span class="spinner"></span> ${loadingText}`;
    }
    
    static reset(buttonId) {
        const btn = document.getElementById(buttonId);
        if (!btn) return;
        
        btn.disabled = false;
        btn.textContent = btn.dataset.originalText || 'Enviar';
    }
}

// ==========================================
// FUNCIONES DE AUTENTICACIÓN
// ==========================================

/**
 * Toggle de visibilidad de contraseña
 */
function togglePasswordVisibility(inputId, buttonElement) {
    const input = document.getElementById(inputId);
    if (!input) return;
    
    const button = buttonElement || input.nextElementSibling;
    const icon = button ? button.querySelector("i") : null;
    const type = input.type === 'password' ? 'text' : 'password';
    input.type = type;
    
    if (icon) {
        icon.classList.toggle('fa-eye');
        icon.classList.toggle('fa-eye-slash');
    }
}

/**
 * Cerrar sesión
 */
function logout() {
    StorageManager.clear();
    window.location.href = '/html/login.html';
}

/**
 * Verificar autenticación
 */
function checkAuth() {
    const token = StorageManager.get(CONFIG.STORAGE_KEYS.TOKEN);
    const currentPage = window.location.pathname;
    
    // Si está en login y tiene token, redirigir según rol
    if (currentPage.includes('login.html') && token) {
        const rol = StorageManager.get(CONFIG.STORAGE_KEYS.ROL);
        const redirectMap = {
            'ROLE_JEFE': '/html/inicio-jefe.html',
            'ROLE_ANALISTA': '/html/inicio-analista.html',
            'ROLE_CLIENTE': '/html/inicio-cliente.html'
        };
        window.location.href = redirectMap[rol] || '/html/login.html';
        return false;
    }
    
    // Si NO está en login y NO tiene token, redirigir a login
    if (!currentPage.includes('login.html') && !token) {
        window.location.href = '/html/login.html';
        return false;
    }
    
    return true;
}

/**
 * Obtener información del usuario actual
 */
function getCurrentUser() {
    return {
        token: StorageManager.get(CONFIG.STORAGE_KEYS.TOKEN),
        rol: StorageManager.get(CONFIG.STORAGE_KEYS.ROL),
        codPersona: StorageManager.get(CONFIG.STORAGE_KEYS.COD_PERSONA),
        nombreCompleto: StorageManager.get(CONFIG.STORAGE_KEYS.NOMBRE)
    };
}

/**
 * Mostrar nombre de usuario en header
 */
function displayUserName() {
    const userNameEl = document.getElementById('user-name');
    if (userNameEl) {
        const user = getCurrentUser();
        userNameEl.textContent = user.nombreCompleto || 'Usuario';
    }
}

// ==========================================
// LOGIN
// ==========================================

/**
 * Manejar login
 */
async function handleLogin(event) {
    event.preventDefault();
    AlertManager.hideAll();
    
    const codPersona = document.getElementById('codPersona').value.trim();
    const password = document.getElementById('password').value;
    
    // Validación
    if (!codPersona || !password) {
        AlertManager.error('Complete todos los campos');
        return;
    }
    
    ButtonManager.setLoading('btn-login', 'Iniciando sesión...');
    
    const result = await HttpClient.post(CONFIG.ENDPOINTS.LOGIN, {
        codPersona: parseInt(codPersona),
        password
    });
    
    if (result.success) {
        const { token, rol, codPersona, nombreCompleto, redirectUrl } = result.data;
        
        // Guardar en localStorage
        StorageManager.set(CONFIG.STORAGE_KEYS.TOKEN, token);
        StorageManager.set(CONFIG.STORAGE_KEYS.ROL, rol);
        StorageManager.set(CONFIG.STORAGE_KEYS.COD_PERSONA, codPersona);
        StorageManager.set(CONFIG.STORAGE_KEYS.NOMBRE, nombreCompleto);
        
        AlertManager.success(`¡Bienvenido ${nombreCompleto}!`);
        
        // Redirigir
        setTimeout(() => {
            window.location.href = redirectUrl;
        }, 1000);
    } else {
        AlertManager.error(result.error);
        ButtonManager.reset('btn-login');
    }
}

// ==========================================
// RECUPERAR CONTRASEÑA
// ==========================================

/**
 * Abrir modal de recuperación
 */
function abrirModalRecuperar() {
    document.getElementById('modal-recuperar').classList.add('show');
    document.getElementById('codPersona-recuperar').focus();
}

/**
 * Cerrar modal de recuperación
 */
function cerrarModalRecuperar() {
    document.getElementById('modal-recuperar').classList.remove('show');
    document.getElementById('form-recuperar').reset();
    AlertManager.hideAll();
}

/**
 * Toggle password en modal
 */
function togglePasswordModal(inputId) {
    const input = document.getElementById(inputId);
    if (!input) return;
    
    const wrapper = input.closest('.input-wrapper');
    const button = wrapper ? wrapper.querySelector('.password-toggle') : null;
    const icon = button ? button.querySelector("i") : null;
    const type = input.type === 'password' ? 'text' : 'password';
    input.type = type;
    
    if (icon) {
        icon.classList.toggle('fa-eye');
        icon.classList.toggle('fa-eye-slash');
    }
}

/**
 * Validar requisitos de contraseña y mostrar mensaje nativo del navegador
 */
function validarPasswordNativo(inputId) {
    const input = document.getElementById(inputId);
    if (!input) return true;
    
    const password = input.value;
    
    // Limpiar mensaje previo
    input.setCustomValidity('');
    
    // Solo validar si hay texto
    if (!password) return true;
    
    if (/\s/.test(password)) {
        input.setCustomValidity('La contraseña no puede contener espacios');
        return false;
    }

    if (password.length < 8) {
        input.setCustomValidity('La contraseña debe tener al menos 8 caracteres');
        return false;
    }
    
    if (!/[A-Z]/.test(password)) {
        input.setCustomValidity('La contraseña debe contener al menos una mayúscula (A-Z)');
        return false;
    }
    
    if (!/[a-z]/.test(password)) {
        input.setCustomValidity('La contraseña debe contener al menos una minúscula (a-z)');
        return false;
    }
    
    if (!/[@#$%^&+=!*()_\-0-9]/.test(password)) {
        input.setCustomValidity('La contraseña debe contener al menos un número o carácter especial (@#$%^&+=!*()_-)');
        return false;
    }
    
    // Si todo está bien, limpiar el mensaje
    input.setCustomValidity('');
    return true;
}

/**
 * Restringir entrada solo a números
 */
function allowOnlyNumbers(inputId, maxLength = null) {
    const input = document.getElementById(inputId);
    if (!input) return;

    input.addEventListener('input', (e) => {
        // eliminar letras o caracteres no numéricos
        e.target.value = e.target.value.replace(/\D/g, '');

        // limitar longitud si se indica
        if (maxLength && e.target.value.length > maxLength) {
            e.target.value = e.target.value.slice(0, maxLength);
        }
    });
}

/**
 * Manejar recuperación de contraseña
 */
async function handleRecuperarPassword(event) {
    event.preventDefault();
    AlertManager.hideAll();
    
    const codPersona = document.getElementById('codPersona-recuperar').value.trim();
    const dni = document.getElementById('dni-recuperar').value.trim();
    const fechaNacimiento = document.getElementById('fecha-nacimiento-recuperar').value;
    const passwordNueva = document.getElementById('password-nueva').value;
    const passwordConfirmar = document.getElementById('password-confirmar').value;
    
    // Validaciones
    if (!codPersona || !dni || !fechaNacimiento || !passwordNueva || !passwordConfirmar) {
        AlertManager.error('Complete todos los campos obligatorios', 'modal-alert-error');
        return;
    }
    
    if (!/^\d{8}$/.test(dni)) {
        AlertManager.error('El DNI debe tener 8 dígitos numéricos', 'modal-alert-error');
        return;
    }

    if (passwordNueva.length < 8) {
        AlertManager.error('La contraseña debe tener al menos 8 caracteres', 'modal-alert-error');
        return;
    }

    if (!validarPasswordNativo('password-nueva')) {
        document.getElementById('password-nueva').reportValidity();
        return;
    }
    
    if (passwordNueva !== passwordConfirmar) {
        AlertManager.error('Las contraseñas no coinciden', 'modal-alert-error');
        return;
    }
    
    // ========== MANEJO DE BOTÓN ==========
    const submitButton = event.target.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.innerHTML = `${iconify("noto:detective")} Verificando identidad...`;

    //ButtonManager.setLoading('btn-recuperar', 'Verificando identidad...');
    
    // ========== ENVIAR SOLICITUD ==========
    const payload = {
        codPersona: parseInt(codPersona),
        passwordActual: null,  // null en lugar de string vacío
        passwordNueva: passwordNueva,
        dni: dni,
        fechaNacimiento: fechaNacimiento  // Enviar como string "YYYY-MM-DD"
    };
    
    const result = await HttpClient.post(CONFIG.ENDPOINTS.CAMBIAR_PASSWORD, payload);
    
    // ========== PROCESAR RESPUESTA ==========
    if (result.success) {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
        AlertManager.success('¡Contraseña actualizada correctamente!', 'modal-alert-success');
        setTimeout(() => {
            cerrarModalRecuperar();
            AlertManager.success('Ahora puede iniciar sesión con su nueva contraseña');
        }, 2000);
    } else {
        // Restaurar botón en caso de error
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
        AlertManager.error(
            result.error || 'Los datos de verificación no coinciden', 
            'modal-alert-error'
        );
    }
}

// ==========================================
// CAMBIAR CONTRASEÑA (DESDE PANEL LOGUEADO)
// ==========================================

/**
 * Abrir modal cambiar contraseña
 */
function cambiarPassword() {
    document.getElementById('modal-password').classList.add('show');
}

/**
 * Cerrar modal cambiar contraseña
 */
function cerrarModalPassword() {
    document.getElementById('modal-password').classList.remove('show');
    document.getElementById('form-cambiar-password').reset();
    AlertManager.hideAll();
}

/**
 * Manejar cambio de contraseña (usuario logueado)
 */
async function handleCambiarPassword(event) {
    event.preventDefault();
    AlertManager.hideAll();

    const passwordActual = document.getElementById('password-actual').value;
    const passwordNueva = document.getElementById('password-nueva-modal').value;
    const passwordConfirmar = document.getElementById('password-confirmar-modal').value;
    
    // Validaciones
    if (!passwordActual || !passwordNueva || !passwordConfirmar) {
        AlertManager.error('Complete todos los campos', 'modal-password-alerts');
        return;
    }
    
    if (!validarPasswordNativo('password-nueva-modal')) {
        document.getElementById('password-nueva-modal').reportValidity();
        return;
    }
    
    if (passwordNueva !== passwordConfirmar) {
        AlertManager.error('Las contraseñas no coinciden', 'modal-password-alerts');
        return;
    }
    
    // Manejo de botón
    const submitButton = event.target.querySelector('button[type="submit"]');
    const originalButtonText = submitButton.textContent;
    submitButton.disabled = true;
    submitButton.innerHTML = `${iconify("noto:hourglass-not-done")} Actualizando...`;

    // Enviar solicitud
    
    const user = getCurrentUser();
    
    const result = await HttpClient.post(CONFIG.ENDPOINTS.CAMBIAR_PASSWORD, {
        codPersona: user.codPersona,
        passwordActual,
        passwordNueva
    });
    
    // ========== PROCESAR RESPUESTA ==========
    if (result.success) {
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
        AlertManager.success('¡Contraseña actualizada correctamente!', 'modal-password-success');
        setTimeout(() => {
            cerrarModalPassword();
            AlertManager.success('Ahora puede iniciar sesión con su nueva contraseña');
        }, 2000);
    } else {
        // Restaurar botón en caso de error
        submitButton.disabled = false;
        submitButton.textContent = originalButtonText;
        AlertManager.error(
            result.error || 'Los datos de verificación no coinciden', 
            'modal-password-alerts'
        );
    }
}

/**
 * Cerrar modal de cambio de contraseña.
 */
function cerrarModalPassword() {
    const modal = document.getElementById('modal-password');
    modal.classList.remove('show');
    
    // Limpiar formulario
    const form = document.getElementById('form-cambiar-password');
    if (form) form.reset();
    
    // Limpiar contenedor de alertas del modal
    const alertContainer = document.getElementById('modal-password-alerts');
    if (alertContainer) alertContainer.innerHTML = '';
    
    // Limpiar alertas generales
    AlertManager.hideAll();
}

// ==========================================
// INICIALIZACIÓN
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    // Verificar autenticación
    checkAuth();
    
    // Mostrar nombre de usuario si existe
    displayUserName();

    //Restringir solo números en los text
    allowOnlyNumbers('codPersona');
    allowOnlyNumbers('codPersona-recuperar');
    allowOnlyNumbers('dni-recuperar', 8);
    
    // Login form
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
        
        // Toggle password login
        const toggleBtn = document.getElementById('toggle-password');
        if (toggleBtn) {
            toggleBtn.addEventListener('click', () => {
                togglePasswordVisibility('password', toggleBtn);
            });
        }
    }
    
    // Recuperar password form
    const formRecuperar = document.getElementById('form-recuperar');
    if (formRecuperar) {
        formRecuperar.addEventListener('submit', handleRecuperarPassword);
    }
    
    // Cambiar password form (usuario logueado)
    const formCambiar = document.getElementById('form-cambiar-password');
    if (formCambiar) {
        formCambiar.addEventListener('submit', handleCambiarPassword);
    }
    
    // Cerrar modales al hacer clic fuera
    document.querySelectorAll('.modal').forEach(modal => {
        modal.addEventListener('click', (e) => {
            if (e.target === modal) {
                modal.classList.remove('show');
            }
        });
    });
});