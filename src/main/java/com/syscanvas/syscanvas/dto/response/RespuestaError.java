package com.syscanvas.syscanvas.dto.response;

import com.fasterxml.jackson.annotation.*;
import java.time.*;
import java.util.*;

/*
 * DTO para respuestas de error de la API.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespuestaError {
    private final String mensaje;
    private final List<String> errores;
    private final LocalDateTime timestamp;
    private final boolean exito;
    private final String path;
    
    /*
     * Constructor para error único.
     */
    public RespuestaError(String mensaje, String path) {
        this.mensaje = mensaje;
        this.errores = null;
        this.timestamp = LocalDateTime.now();
        this.exito = false;
        this.path = path;
    }
    
    /*
     * Constructor para múltiples errores (validaciones).
     */
    public RespuestaError(String mensaje, List<String> errores, String path) {
        this.mensaje = mensaje;
        this.errores = errores;
        this.timestamp = LocalDateTime.now();
        this.exito = false;
        this.path = path;
    }
    
    // Getters
    public String getMensaje() {
        return mensaje;
    }
    
    public List<String> getErrores() {
        return errores;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public boolean isExito() {
        return exito;
    }
    
    public String getPath() {
        return path;
    }
}
