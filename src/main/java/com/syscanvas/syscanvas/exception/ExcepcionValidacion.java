package com.syscanvas.syscanvas.exception;

import java.util.*;

/*
 * Excepción lanzada cuando fallan las validaciones de negocio o datos.
 * 
 * Permite acumular múltiples errores de validación en una sola excepción,
 * facilitando la respuesta al cliente con todos los problemas detectados.
 */

public class ExcepcionValidacion extends RuntimeException {
    private final List<String> errores;
    
    /*
     * Constructor con un único mensaje de error.
     */
    public ExcepcionValidacion(String mensaje) {
        super(mensaje);
        this.errores = Collections.singletonList(mensaje);
    }
    
    /*
     * Constructor con múltiples errores de validación.
     */
    public ExcepcionValidacion(String mensaje, List<String> errores) {
        super(mensaje + ": " + String.join(", ", errores));
        this.errores = new ArrayList<>(errores);
    }
    
    /*
     * Constructor con lista de errores (sin mensaje general).
     */
    public ExcepcionValidacion(List<String> errores) {
        super("Errores de validación: " + String.join(", ", errores));
        this.errores = new ArrayList<>(errores);
    }
    
    /*
     * Obtiene la lista inmutable de errores de validación.
     */
    public List<String> getErrores() {
        return Collections.unmodifiableList(errores);
    }
    
    /*
     * Verifica si hay múltiples errores de validación.
     */
    public boolean tieneMultiplesErrores() {
        return errores.size() > 1;
    }
}
