package com.syscanvas.syscanvas.exception;

import com.syscanvas.syscanvas.dto.response.*;
import jakarta.servlet.http.*;
import org.springframework.http.*;
import org.springframework.security.access.*;
import org.springframework.validation.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.*;
import org.apache.catalina.connector.*;
import java.io.*;
import java.util.*;
import java.util.stream.*;

/*
 * Manejador global de excepciones para toda la aplicación SysCanvas.
 *
 * Responsabilidades:
 * - Centralizar el manejo de errores de toda la aplicación
 * - Traducir excepciones a respuestas HTTP coherentes
 * - Registrar logs con contexto sin exponer información sensible
 */

@RestControllerAdvice
public class ManejadorExcepcionesGlobal {
    private static final Logger logger = LoggerFactory.getLogger(ManejadorExcepcionesGlobal.class);

    // ========== MANEJO ESPECÍFICO PARA ERORES DE CONEXIÓN DE VIDEO ==========

    /*
     * Maneja el error: Se ha anulado una conexión establecida por el software en su equipo host.
     */
    @ExceptionHandler({ClientAbortException.class, IOException.class})
    public ResponseEntity<Void> manejarClientAbort(
        ClientAbortException ex,
        HttpServletRequest request
    ) {
        if (ex instanceof ClientAbortException || ex.getMessage().contains("anulado una conexión establecida")) {
            logger.info("Conexión abortada por el cliente (comportamiento normal en videos): {}", request.getRequestURI());
            return ResponseEntity.noContent().build();
        }
        
        return null;
    }

    // ========== EXCEPCIONES PERSONALIZADAS DE DOMINIO ==========

    /*
     * Maneja recursos no encontrados (404).
     */
    @ExceptionHandler(ExcepcionRecursoNoEncontrado.class)
    public ResponseEntity<RespuestaError> manejarRecursoNoEncontrado(
            ExcepcionRecursoNoEncontrado ex,
            HttpServletRequest request
    ) {
        logger.warn("Recurso no encontrado: {}", ex.getMessage());
        return RespuestaErrorBuilder.crear()
                .conMensaje(ex.getMessage())
                .conEstado(HttpStatus.NOT_FOUND)
                .conRuta(request.getRequestURI())
                .construir();
    }

    /*
     * Maneja errores de validación de negocio (400).
     */
    @ExceptionHandler(ExcepcionValidacion.class)
    public ResponseEntity<RespuestaError> manejarValidacion(
            ExcepcionValidacion ex,
            HttpServletRequest request
    ) {
        logger.warn("Errores de validación: {}", ex.getErrores());
        return RespuestaErrorBuilder.crear()
                .conMensaje("Errores de validación")
                .conErrores(ex.getErrores())
                .conEstado(HttpStatus.BAD_REQUEST)
                .conRuta(request.getRequestURI())
                .construir();
    }

    /**
     * Maneja autenticación fallida personalizada (401).
     */
    @ExceptionHandler(ExcepcionAutenticacionFallida.class)
    public ResponseEntity<RespuestaError> manejarAutenticacionFallida(
            ExcepcionAutenticacionFallida ex,
            HttpServletRequest request
    ) {
        logger.warn("Autenticación fallida: {}", ex.getMessage());
        return RespuestaErrorBuilder.crear()
                .conMensaje(ex.getMessage())
                .conEstado(HttpStatus.UNAUTHORIZED)
                .conRuta(request.getRequestURI())
                .construir();
    }

    // ========== EXCEPCIONES TÉCNICAS (SPRING, JAKARTA, JAVA) ==========

    /*
     * Maneja errores de validación de Jakarta (@Valid) (400).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarValidacionJakarta(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        logger.warn("Errores de validación en request body");

        List<String> errores = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatearErrorCampo)
                .collect(Collectors.toList());

        return RespuestaErrorBuilder.crear()
                .conMensaje("Datos inválidos en la solicitud")
                .conErrores(errores)
                .conEstado(HttpStatus.BAD_REQUEST)
                .conRuta(request.getRequestURI())
                .construir();
    }

    /*
     * Maneja errores de acceso denegado (403).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaError> manejarAccesoDenegado(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        logger.warn("Acceso denegado: {}", ex.getMessage());
        return RespuestaErrorBuilder.crear()
                .conMensaje("No tiene permisos para acceder a este recurso")
                .conEstado(HttpStatus.FORBIDDEN)
                .conRuta(request.getRequestURI())
                .construir();
    }

    /*
     * Maneja argumentos ilegales (400).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaError> manejarArgumentoIlegal(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        logger.warn("Argumento ilegal: {}", ex.getMessage());
        return RespuestaErrorBuilder.crear()
                .conMensaje(ex.getMessage())
                .conEstado(HttpStatus.BAD_REQUEST)
                .conRuta(request.getRequestURI())
                .construir();
    }

    /*
     * Maneja cualquier otra excepción no capturada (500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarExcepcionGeneral(
            Exception ex,
            HttpServletRequest request
    ) {
        logger.error("Error interno del servidor en {}: {}", 
            request.getRequestURI(), ex.getMessage(), ex);

        return RespuestaErrorBuilder.crear()
                .conMensaje("Error interno del servidor. Por favor contacte al administrador.")
                .conEstado(HttpStatus.INTERNAL_SERVER_ERROR)
                .conRuta(request.getRequestURI())
                .construir();
    }

    // ========== MÉTODOS PRIVADOS ==========

    /*
     * Builder interno para construcción fluida de respuestas de error.
     * Implementa el patrón Builder para mejorar legibilidad.
     */
    private static class RespuestaErrorBuilder {
        private String mensaje;
        private List<String> errores;
        private HttpStatus estado;
        private String ruta;

        public static RespuestaErrorBuilder crear() {
            return new RespuestaErrorBuilder();
        }

        public RespuestaErrorBuilder conMensaje(String mensaje) {
            this.mensaje = mensaje;
            return this;
        }

        public RespuestaErrorBuilder conErrores(List<String> errores) {
            this.errores = errores;
            return this;
        }

        public RespuestaErrorBuilder conEstado(HttpStatus estado) {
            this.estado = estado;
            return this;
        }

        public RespuestaErrorBuilder conRuta(String ruta) {
            this.ruta = ruta;
            return this;
        }

        public ResponseEntity<RespuestaError> construir() {
            RespuestaError respuesta = errores != null && !errores.isEmpty()
                    ? new RespuestaError(mensaje, errores, ruta)
                    : new RespuestaError(mensaje, ruta);
            
            return ResponseEntity.status(estado).body(respuesta);
        }
    }

    /*
     * Formatea un error de campo de validación.
     */
    private String formatearErrorCampo(FieldError error) {
        return String.format("%s: %s", error.getField(), error.getDefaultMessage());
    }
}
