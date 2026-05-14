package com.syscanvas.syscanvas.dto;

/**
 * Record inmutable para encapsular estadísticas de canvas.
 */

public record EstadisticasCanvas (
    int totalTareas, int tareasCompletadas, double porcentajeProgreso, int totalComentarios
) {
    /**
     * Constructor compacto con validaciones.
     * 
     * Se ejecuta automáticamente antes de asignar los valores.
     */
    
    public EstadisticasCanvas {
        if (totalTareas < 0) {
            throw new IllegalArgumentException("totalTareas no puede ser negativo");
        }
        if (tareasCompletadas < 0) {
            throw new IllegalArgumentException("tareasCompletadas no puede ser negativo");
        }
        if (tareasCompletadas > totalTareas) {
            throw new IllegalArgumentException(
                "tareasCompletadas no puede ser mayor que totalTareas"
            );
        }
        if (porcentajeProgreso < 0.0 || porcentajeProgreso > 100.0) {
            throw new IllegalArgumentException(
                "porcentajeProgreso debe estar entre 0 y 100"
            );
        }
    }
}
