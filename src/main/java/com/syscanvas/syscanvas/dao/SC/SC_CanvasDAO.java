package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.util.*;
import java.time.*;


@Repository
public interface SC_CanvasDAO extends JpaRepository<SC_Canvas, Long> {
    @Query("""
    SELECT c FROM SC_Canvas c
    LEFT JOIN FETCH c.etapas e
    LEFT JOIN FETCH e.tareas t
    WHERE c.codCanvas = :id
    """)
    Optional<SC_Canvas> findByIdConEtapas(Long id);
    Optional<SC_Canvas> findByCodCanvas(Long codCanvas);
    Optional<SC_Canvas> findByNomCanvas(String nomCanvas);
    List<SC_Canvas> findByEstado(SC_Estado estado);
    List<SC_Canvas> findByTipoCanvas(SC_Tipo_Canvas tipoCanvas);
    List<SC_Canvas> findByCodPyto(Long codPyto);
    List<SC_Canvas> findByCodPersona(Long codPersona);
    List<SC_Canvas> findByFecCreacionBetween(LocalDateTime inicio, LocalDateTime fin);
    List<SC_Canvas> findByFecModificacionBetween(LocalDateTime inicio, LocalDateTime fin);
}
