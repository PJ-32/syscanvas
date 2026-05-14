package com.syscanvas.syscanvas.validator.auth;

import com.syscanvas.syscanvas.dao.T.*;
import com.syscanvas.syscanvas.model.T.*;
import org.springframework.stereotype.*;
import java.time.*;
import java.util.*;

/*
 * Validador de identidad para recuperación de contraseña.
 * 
 * Responsabilidad Única: Verificar identidad mediante DNI y fecha de nacimiento.
 */

@Component
public class IdentityValidator {
    private final T_EmpleadoDAO empleadoDAO;

    public IdentityValidator(T_EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }

    /*
     * Valida la identidad de un usuario mediante DNI y fecha de nacimiento.
     */
    public boolean validarIdentidad(Long codPersona, String dni, LocalDate fechaNacimiento) {
        if (dni == null || dni.trim().isEmpty() || fechaNacimiento == null) {
            return false;
        }

        Optional<T_Empleado> empleadoOpt = empleadoDAO.findById(codPersona);
        if (empleadoOpt.isEmpty()) {
            return false;
        }

        T_Empleado empleado = empleadoOpt.get();

        // Validar DNI
        if (!empleado.getDni().trim().equals(dni.trim())) {
            return false;
        }

        // Validar fecha de nacimiento
        LocalDate fecNacBD = convertirFecha(empleado.getFecNac());
        return fecNacBD.equals(fechaNacimiento);
    }

    private LocalDate convertirFecha(java.util.Date fecha) {
        if (fecha instanceof java.sql.Date) {
            return ((java.sql.Date) fecha).toLocalDate();
        }
        return fecha.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
