/*
 * SYSCANVAS - sidebar.js
 * Lógica reutilizable del sidebar
 */

// Generar íconos
function fontAwesome(name, size = 18) {
    return `<i class="fa-solid fa-${name}" style="font-size:${size}px;"></i>`;
}

// ==========================================
// SIDEBAR - Toggle y Funcionalidades
// ==========================================

/*
 * Toggle sidebar (expandir/contraer)
 */
function toggleSidebar() {
    const sidebar = document.getElementById('sidebar');
    const toggle = document.getElementById('sidebar-toggle');
    
    if (!sidebar || !toggle) return;
    
    sidebar.classList.toggle('collapsed');
    toggle.innerHTML = sidebar.classList.contains('collapsed') ? fontAwesome("chevron-right") : fontAwesome("chevron-left");
    
    localStorage.setItem('sidebarCollapsed', sidebar.classList.contains('collapsed'));
}

/*
 * Restaurar estado del sidebar desde localStorage
 */
function restaurarEstadoSidebar() {
    const sidebar = document.getElementById('sidebar');
    const toggle = document.getElementById('sidebar-toggle');
    
    if (!sidebar || !toggle) return;
    
    const collapsed = localStorage.getItem('sidebarCollapsed') === 'true';
    
    if (collapsed) {
        sidebar.classList.add('collapsed');
        toggle.innerHTML = fontAwesome("chevron-right");
    }
}

// ==========================================
// INICIALIZACIÓN
// ==========================================

document.addEventListener('DOMContentLoaded', () => {
    // Restaurar estado del sidebar
    restaurarEstadoSidebar();
});