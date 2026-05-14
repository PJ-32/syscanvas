package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.time.*;
import java.util.*;

@Repository
public interface SC_HistorialDAO extends JpaRepository<SC_Historial, Long> {
    Optional<SC_Historial> findByCodHistorial(Long codHistorial);
    List<SC_Historial> findByCodPersona(Long codPersona);
    List<SC_Historial> findByCanvas(SC_Canvas canvas);
    List<SC_Historial> findByFecAccionBetween(LocalDateTime inicio, LocalDateTime fin);
}