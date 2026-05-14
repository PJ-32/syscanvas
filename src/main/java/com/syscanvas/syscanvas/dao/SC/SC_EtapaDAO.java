package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.util.*;

@Repository
public interface SC_EtapaDAO extends JpaRepository<SC_Etapa, Long> {
    Optional<SC_Etapa> findByCodEtapa(Long codEtapa);
    List<SC_Etapa> findByNomEtapa(String nomEtapa);
    List<SC_Etapa> findByCanvas(SC_Canvas canvas);
    @EntityGraph(attributePaths = {"tareas"})
    List<SC_Etapa> findByCanvas_CodCanvas(Long codCanvas);
}
