package com.syscanvas.syscanvas.exception;

/*
 * Excepción lanzada cuando no se encuentra un recurso solicitado.
 * 
 * Proporciona información detallada sobre qué recurso no se encontró
 * y qué identificador se utilizó en la búsqueda.
 */

public class ExcepcionRecursoNoEncontrado extends RuntimeException {
    private final String nombreRecurso;
    private final String nombreCampo;
    private final Object valorCampo;
    
    /*
     * Constructor completo con detalles del recurso no encontrado.
     */
    public ExcepcionRecursoNoEncontrado(
            String nombreRecurso, 
            String nombreCampo, 
            Object valorCampo
    ) {
        super(String.format(
            "%s no encontrado con %s: '%s'", 
            nombreRecurso, 
            nombreCampo, 
            valorCampo
        ));
        this.nombreRecurso = nombreRecurso;
        this.nombreCampo = nombreCampo;
        this.valorCampo = valorCampo;
    }
    
    /*
     * Constructor simplificado con mensaje personalizado.
     */
    public ExcepcionRecursoNoEncontrado(String mensaje) {
        super(mensaje);
        this.nombreRecurso = null;
        this.nombreCampo = null;
        this.valorCampo = null;
    }
    
    public String getNombreRecurso() {
        return nombreRecurso;
    }
    
    public String getNombreCampo() {
        return nombreCampo;
    }
    
    public Object getValorCampo() {
        return valorCampo;
    }
}
