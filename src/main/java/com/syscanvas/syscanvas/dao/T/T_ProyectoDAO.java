package com.syscanvas.syscanvas.dao.T;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.T.*;
import java.util.*;

@Repository
public interface T_ProyectoDAO extends JpaRepository<T_Proyecto, Long> {
    Optional<T_Proyecto> findByCodPyto(Long codPyto);
    List<T_Proyecto> findByNomPytoContainingIgnoreCase(String nomPyto);
    List<T_Proyecto> findByVigente(Integer vigente);
    List<T_Proyecto> findByJefeProyecto(T_Empleado jefeProyecto);
}
