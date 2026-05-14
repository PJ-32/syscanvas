package com.syscanvas.syscanvas.service.helper;

import com.syscanvas.syscanvas.dto.*;
import com.syscanvas.syscanvas.model.SC.*;
import org.springframework.stereotype.*;

/*
 * Calculador de estadísticas de canvas.
 * 
 * Responsabilidad única: Calcular métricas y estadísticas de progreso
 * de un canvas basándose en sus etapas y tareas.
 */

@Component
public class CalculadorEstadisticasCanvas {
    private static final Long VIGENCIA_COMPLETADA = 0L;
    
    /*
     * Calcula las estadísticas completas de un canvas.
     */
    public EstadisticasCanvas calcular(SC_Canvas canvas) {
        if (canvas == null) {
            return new EstadisticasCanvas(0, 0, 0.0, 0);
        }
        
        int totalTareas = calcularTotalTareas(canvas);
        int tareasCompletadas = calcularTareasCompletadas(canvas);
        double porcentajeProgreso = calcularPorcentajeProgreso(totalTareas, tareasCompletadas);
        int totalComentarios = calcularTotalComentarios(canvas);
        
        return new EstadisticasCanvas(
            totalTareas, 
            tareasCompletadas, 
            porcentajeProgreso, 
            totalComentarios
        );
    }
    
    /*
     * Calcula el total de tareas en todas las etapas.
     */
    private int calcularTotalTareas(SC_Canvas canvas) {
        if (canvas.getEtapas() == null || canvas.getEtapas().isEmpty()) {
            return 0;
        }
        
        return canvas.getEtapas().stream()
                .filter(etapa -> etapa.getTareas() != null)
                .mapToInt(etapa -> etapa.getTareas().size())
                .sum();
    }
    
    /*
     * Calcula el total de tareas completadas (vigencia = 0).
     */
    private int calcularTareasCompletadas(SC_Canvas canvas) {
        if (canvas.getEtapas() == null || canvas.getEtapas().isEmpty()) {
            return 0;
        }
        
        return (int) canvas.getEtapas().stream()
                .filter(etapa -> etapa.getTareas() != null)
                .flatMap(etapa -> etapa.getTareas().stream())
                .filter(tarea -> VIGENCIA_COMPLETADA.equals(tarea.getVigente()))
                .count();
    }
    
    /*
     * Calcula el porcentaje de progreso.
     */
    private double calcularPorcentajeProgreso(int totalTareas, int tareasCompletadas) {
        if (totalTareas == 0) {
            return 0.0;
        }
        
        return (tareasCompletadas * 100.0) / totalTareas;
    }
    
    /*
     * Calcula el total de comentarios.
     */
    private int calcularTotalComentarios(SC_Canvas canvas) {
        if (canvas.getComentarios() == null) {
            return 0;
        }
        
        return canvas.getComentarios().size();
    }
}
