package com.syscanvas.syscanvas.config;

import java.time.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.model.SC.*;
import jakarta.annotation.*;
import lombok.*;

@Component
@RequiredArgsConstructor
public class SC_DataInitializer {
    private final SC_CargoDAO cargoDAO;
    private final SC_EstadoDAO estadoDAO;
    private final SC_Tipo_CanvasDAO tipoCanvasDAO;

    @PostConstruct
    @Transactional("scTransactionManager")
    public void initializerData() {
        initializerCargos();
        initializerEstados();
        initializerTiposCanvas();
    }

    /*
     * Insertar Cargos de ejemplo
     */
    private void initializerCargos() {
        if (cargoDAO.count() == 0) {
            cargoDAO.save(new SC_Cargo(2L, "Jefe de Proyecto", "Responsable de gestión de proyectos", 1));
            cargoDAO.save(new SC_Cargo(3L, "Analista", "Analista Senior de sistemas", 1));

            System.out.println("✅ Cargos iniciales insertados.");
        }
    }

    /*
     * Insertar Estados de ejemplo
     */
    private void initializerEstados() {
        if (estadoDAO.count() == 0) {
            LocalDateTime now = LocalDateTime.now();
            estadoDAO.save(new SC_Estado(1, "Borrador", "Canvas en estado de borrador", now, 1, null));
            estadoDAO.save(new SC_Estado(2, "En Progreso", "Canvas en desarrollo activo", now, 1, null));
            estadoDAO.save(new SC_Estado(3, "Completado", "Canvas finalizado", now, 1, null));
        
            System.out.println("✅ Estados de Canvas insertados.");
        }
    }

    /*
     * Insertar Tipos de Canvas de ejemplo
     */
    private void initializerTiposCanvas() {
        if (tipoCanvasDAO.count() == 0) {
            tipoCanvasDAO.save(new SC_Tipo_Canvas("K", "Kanban Board", 1, null));
            tipoCanvasDAO.save(new SC_Tipo_Canvas("S", "Scrum Board", 1, null));
            tipoCanvasDAO.save(new SC_Tipo_Canvas("L", "Lean Canvas", 1, null));
        
            System.out.println("✅ Tipos de Canvas insertados.");
        }
    }
}
