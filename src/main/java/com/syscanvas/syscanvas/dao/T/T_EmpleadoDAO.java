package com.syscanvas.syscanvas.dao.T;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.T.*;
import java.util.List;

@Repository
public interface T_EmpleadoDAO extends JpaRepository <T_Empleado, Long> {
    Optional<T_Empleado> findByDni(String dni);
    List<T_Empleado> findByCodCargo(Long codCargo);
    List<T_Empleado> findByVigente(Integer vigente);
}
