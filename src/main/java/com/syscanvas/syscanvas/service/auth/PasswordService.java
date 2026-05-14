package com.syscanvas.syscanvas.service.auth;

import com.syscanvas.syscanvas.dao.T.*;
import com.syscanvas.syscanvas.dto.auth.*;
import com.syscanvas.syscanvas.exception.*;
import com.syscanvas.syscanvas.model.T.*;
import com.syscanvas.syscanvas.validator.auth.*;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

/*
 * Servicio para gestión de contraseñas.
 * 
 * Responsabilidades:
 * - Cambiar contraseña (usuario autenticado)
 * - Recuperar contraseña (validación de identidad)
 */

@Service
public class PasswordService {
    private final T_EmpleadoDAO empleadoDAO;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final IdentityValidator identityValidator;

    public PasswordService(
            T_EmpleadoDAO empleadoDAO,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,
            IdentityValidator identityValidator
    ) {
        this.empleadoDAO = empleadoDAO;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.identityValidator = identityValidator;
    }

    /*
     * Cambia la contraseña de un usuario.
     * 
     * Soporta dos modos:
     * 1. Usuario autenticado (con passwordActual)
     * 2. Recuperación (con DNI + fechaNacimiento)
     */
    @Transactional(transactionManager = "tTransactionManager")
    public void cambiarPassword(CambiarPasswordRequest request) {
        validarModoAutenticacion(request);
        
        T_Empleado empleado = buscarEmpleadoOLanzarExcepcion(request.getCodPersona());
        
        String nuevaPasswordEncriptada = passwordEncoder.encode(request.getPasswordNueva());
        empleado.setPassword(nuevaPasswordEncriptada);
        
        empleadoDAO.save(empleado);
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void validarModoAutenticacion(CambiarPasswordRequest request) {
        boolean modoUsuarioAutenticado = request.getPasswordActual() != null 
                && !request.getPasswordActual().isEmpty();

        if (modoUsuarioAutenticado) {
            autenticarUsuario(request.getCodPersona(), request.getPasswordActual());
        } else {
            validarIdentidadParaRecuperacion(request);
        }
    }

    private void autenticarUsuario(Long codPersona, String passwordActual) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    codPersona.toString(),
                    passwordActual
                )
            );
        } catch (BadCredentialsException e) {
            throw new ExcepcionAutenticacionFallida("Contraseña actual incorrecta");
        }
    }

    private void validarIdentidadParaRecuperacion(CambiarPasswordRequest request) {
        boolean identidadValida = identityValidator.validarIdentidad(
            request.getCodPersona(),
            request.getDni(),
            request.getFechaNacimiento()
        );

        if (!identidadValida) {
            throw new ExcepcionAutenticacionFallida(
                "Los datos de verificación son incorrectos"
            );
        }
    }

    private T_Empleado buscarEmpleadoOLanzarExcepcion(Long codPersona) {
        return empleadoDAO.findById(codPersona)
                .orElseThrow(() -> new ExcepcionAutenticacionFallida("Usuario no encontrado"));
    }
}
