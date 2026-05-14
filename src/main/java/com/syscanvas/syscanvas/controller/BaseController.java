package com.syscanvas.syscanvas.controller;
import org.springframework.security.core.Authentication;

import com.syscanvas.syscanvas.dto.response.*;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;

/*
 * Controlador base con métodos helper para respuestas HTTP.
 * 
 * Proporciona métodos reutilizables para construir respuestas
 * estandarizadas en toda la aplicación.
 * 
 * Todos los controladores REST deben extender esta clase.
 */

public abstract class BaseController {
    /*
     * Construye respuesta exitosa con datos.
     */
    protected <T> ResponseEntity<RespuestaExito<T>> respuestaExito(String mensaje, T datos) {
        return ResponseEntity.ok(new RespuestaExito<>(mensaje, datos));
    }

    /*
     * Construye respuesta exitosa sin datos.
     */
    protected <T> ResponseEntity<RespuestaExito<T>> respuestaExito(String mensaje) {
        return ResponseEntity.ok(new RespuestaExito<>(mensaje));
    }

    /*
     * Construye respuesta de recurso creado con datos.
     */
    protected <T> ResponseEntity<RespuestaExito<T>> respuestaCreada(String mensaje, T datos) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new RespuestaExito<>(mensaje, datos));
    }

    /*
     * Construye respuesta sin contenido.
     */
    protected ResponseEntity<Void> respuestaSinContenido() {
        return ResponseEntity.noContent().build();
    }

    /*
     * Construye respuesta aceptada (procesamiento asíncrono).
     */
    protected <T> ResponseEntity<RespuestaExito<T>> respuestaAceptada(String mensaje) {
        return ResponseEntity
                .accepted()
                .body(new RespuestaExito<>(mensaje));
    }
    /**
     * Obtiene el codPersona del usuario autenticado desde el JWT.
     */
    protected Long obtenerCodPersonaActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null)
            return null;

        try {
            return Long.parseLong(auth.getName()); // << tu JWT usa el codPersona como "username"
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Verifica si el usuario autenticado tiene rol de JEFE
     */
    protected boolean esJefe() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_JEFE"));
    }

    /**
     * Verifica si el usuario autenticado tiene rol de ANALISTA
     */
    protected boolean esAnalista() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return false;

        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ANALISTA"));
    }
}
