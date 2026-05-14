package com.syscanvas.syscanvas.dto.response;

import com.fasterxml.jackson.annotation.*;
import java.time.*;

/*
 * DTO genérico para respuestas exitosas de la API.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RespuestaExito<T> {
    private final String mensaje;
    private final T datos;
    private final LocalDateTime timestamp;
    private final boolean exito;
    
    /*
     * Constructor para respuesta con datos.
     */
    public RespuestaExito(String mensaje, T datos) {
        this.mensaje = mensaje;
        this.datos = datos;
        this.timestamp = LocalDateTime.now();
        this.exito = true;
    }
    
    /*
     * Constructor para respuesta sin datos (solo confirmación).
     */
    public RespuestaExito(String mensaje) {
        this(mensaje, null);
    }
    
    // Getters
    public String getMensaje() {
        return mensaje;
    }
    
    public T getDatos() {
        return datos;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public boolean isExito() {
        return exito;
    }
}
