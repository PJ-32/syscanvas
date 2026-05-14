package com.syscanvas.syscanvas.controller.auth;

import com.syscanvas.syscanvas.dto.auth.*;
import com.syscanvas.syscanvas.dto.response.RespuestaExito;
import com.syscanvas.syscanvas.service.auth.AuthService;
import com.syscanvas.syscanvas.service.auth.PasswordService;

import jakarta.validation.Valid;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * Controlador REST para autenticación y gestión de contraseñas.
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final PasswordService passwordService;

    public AuthController(AuthService authService, PasswordService passwordService) {
        this.authService = authService;
        this.passwordService = passwordService;
    }

    /*
     * Autentica un usuario y genera un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        logger.info("POST /api/auth/login - Intento de login para codPersona: {}", 
            request.getCodPersona());
        
        LoginResponse response = authService.login(request);
        
        logger.info("Login exitoso para codPersona: {} con rol: {}", 
            request.getCodPersona(), response.getRol());
        
        return ResponseEntity.ok(response);
    }

    /*
     * Cambia la contraseña de un usuario.
     * 
     * Soporta dos modos:
     * 1. Usuario autenticado (con passwordActual)
     * 2. Recuperación (con DNI + fechaNacimiento)
     */
    @PostMapping("/cambiar-password")
    public ResponseEntity<RespuestaExito<Void>> cambiarPassword(
            @Valid @RequestBody CambiarPasswordRequest request
    ) {
        logger.info("POST /api/auth/cambiar-password - Solicitud para codPersona: {}", 
            request.getCodPersona());
        
        passwordService.cambiarPassword(request);
        
        logger.info("Contraseña actualizada exitosamente para codPersona: {}", 
            request.getCodPersona());
        
        RespuestaExito<Void> respuesta = new RespuestaExito<>(
            "Contraseña actualizada correctamente"
        );
        
        return ResponseEntity.ok(respuesta);
    }

    /*
     * Valida si un token JWT es válido.
     */
    @GetMapping("/validate")
    public ResponseEntity<RespuestaExito<Void>> validateToken() {
        logger.debug("GET /api/auth/validate - Validando token JWT");

        RespuestaExito<Void> respuesta = new RespuestaExito<>(
            "Token válido"
        );
        
        return ResponseEntity.ok(respuesta);
    }
}