package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.model.SC.SC_Canvas;
import com.syscanvas.syscanvas.model.SC.SC_Etapa;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SC_EtapaPlantillaFactory {
    
    
    public static List<SC_Etapa> crearEtapasPorTipo(SC_Canvas canvas) {

        List<SC_Etapa> etapas = new ArrayList<>();
        LocalDateTime ahora = LocalDateTime.now();

        switch (canvas.getTipoCanvas().getTipCanvas()) {

            // ===================== KANBAN =====================
            case "K":
                etapas.add(nueva("Planificado", 1, canvas, ahora));
                etapas.add(nueva("Ejecutado", 2, canvas, ahora));
                etapas.add(nueva("Terminado", 3, canvas, ahora));
                break;

            // ===================== SCRUM =====================
            case "S":
                etapas.add(nueva("Por hacer", 1, canvas, ahora));
                etapas.add(nueva("En progreso", 2, canvas, ahora));
                etapas.add(nueva("En revisión", 3, canvas, ahora));
                etapas.add(nueva("Completado", 4, canvas, ahora));
                break;

            // ===================== LEAN CANVAS =====================
            case "L":
                etapas.add(nueva("Problema", 1, canvas, ahora));
                etapas.add(nueva("Segmentos de clientes", 2, canvas, ahora));
                etapas.add(nueva("Propuesta de valor", 3, canvas, ahora));
                etapas.add(nueva("Solución", 4, canvas, ahora));
                etapas.add(nueva("Canales", 5, canvas, ahora));
                etapas.add(nueva("Flujo de ingresos", 6, canvas, ahora));
                etapas.add(nueva("Estructura de costos", 7, canvas, ahora));
                etapas.add(nueva("Métricas clave", 8, canvas, ahora));
                etapas.add(nueva("Ventaja injusta", 9, canvas, ahora));
                break;
        }

        return etapas;
    }

    private static SC_Etapa nueva(String nombre, int numero, SC_Canvas canvas, LocalDateTime fecha) {
        SC_Etapa etapa = new SC_Etapa();
        etapa.setNomEtapa(nombre);
        etapa.setDesEtapa(nombre);
        etapa.setNumEtapa(numero);
        etapa.setCanvas(canvas);
        etapa.setVigente(1);
        etapa.setFecCreacion(fecha);
        etapa.setFecModificacion(fecha);
        return etapa;
    }

}
