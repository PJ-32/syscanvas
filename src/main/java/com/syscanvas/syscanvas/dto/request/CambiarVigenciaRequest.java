package com.syscanvas.syscanvas.dto.request;

import jakarta.validation.constraints.*;

/**
 * Request para cambiar vigencia de entidades.
 * Record reutilizable para Proyectos, Empleados, Etapas, etc.
 */

public record CambiarVigenciaRequest (
    @NotNull(message = "El parámetro 'activar' es obligatorio")
    Boolean activar
) {
    
}