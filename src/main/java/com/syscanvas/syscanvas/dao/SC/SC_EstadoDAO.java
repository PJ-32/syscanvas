package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.util.*;

@Repository
public interface SC_EstadoDAO extends JpaRepository<SC_Estado, Integer> {
    Optional<SC_Estado> findByCodEstado(Integer codEstado);
    Optional<SC_Estado> findByNomEstado(String nomEstado);
}
