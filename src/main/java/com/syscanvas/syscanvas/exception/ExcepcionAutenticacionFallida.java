package com.syscanvas.syscanvas.exception;

/*
 * Excepción lanzada cuando falla la autenticación de un usuario.
 * 
 * Casos de uso:
 * - Credenciales inválidas
 * - Contraseña incorrecta
 * - Validación de identidad fallida
 * - Token JWT inválido o expirado
 */

public class ExcepcionAutenticacionFallida extends RuntimeException{
    /**
     * Constructor con mensaje personalizado.
     */
    public ExcepcionAutenticacionFallida(String mensaje) {
        super(mensaje);
    }
    
    /**
     * Constructor con mensaje y causa raíz.
     */
    public ExcepcionAutenticacionFallida(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

}
