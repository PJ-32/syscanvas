package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.util.*;
import java.time.*;

@Repository
public interface SC_TareaDAO extends JpaRepository<SC_Tarea, Long> {

    Optional<SC_Tarea> findByCodTarea(Long codTarea);
    List<SC_Tarea> findByEtapa(SC_Etapa etapa);
    List<SC_Tarea> findByCodPersona(Long codPersona);
    List<SC_Tarea> findByNomTarea(String nomTarea);
    List<SC_Tarea> findByFecCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<SC_Tarea> findByFecModificacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<SC_Tarea> findByEtapa_CodEtapa(Long codEtapa);


    // Listar todas las tareas cargando etapa y canvas
    @Query("""
        SELECT t FROM SC_Tarea t
        JOIN FETCH t.etapa e
        JOIN FETCH e.canvas c
    """)
    Page<SC_Tarea> findAllConEtapaYCanvas(Pageable pageable);


    // Buscar tareas por analista (codPersona)
    @Query("""
        SELECT t FROM SC_Tarea t
        JOIN FETCH t.etapa e
        JOIN FETCH e.canvas c
        WHERE t.codPersona = :codPersona
    """)
    List<SC_Tarea> findByCodPersonaConCanvas(Long codPersona);


    // Buscar tareas por etapa (con canvas incluido)
    @Query("""
        SELECT t FROM SC_Tarea t
        JOIN FETCH t.etapa e
        JOIN FETCH e.canvas c
        WHERE e.codEtapa = :codEtapa
    """)
    List<SC_Tarea> findByEtapaConCanvas(Long codEtapa);
}
