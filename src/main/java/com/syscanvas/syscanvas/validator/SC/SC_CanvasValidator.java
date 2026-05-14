package com.syscanvas.syscanvas.validator.SC;

import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.dto.SC.*;
import com.syscanvas.syscanvas.exception.*;
import org.springframework.stereotype.*;
import java.util.*;

/*
 * Validador centralizado para Canvas.
 * 
 * Responsabilidad Única: Validar reglas de negocio de Canvas.
 */

@Component
public class SC_CanvasValidator {
    private final SC_CanvasDAO canvasDAO;

    public SC_CanvasValidator(SC_CanvasDAO canvasDAO) {
        this.canvasDAO = canvasDAO;
    }

    /*
     * Valida los datos básicos de un Canvas.
     */
    public void validarCanvas(SC_CanvasDTO dto) {
        List<String> errores = new ArrayList<>();
        
        if (dto.getNomCanvas() == null || dto.getNomCanvas().isBlank()) {
            errores.add("El nombre del canvas es obligatorio");
        }
        
        if (dto.getNomCanvas() != null && dto.getNomCanvas().length() > 100) {
            errores.add("El nombre no puede exceder 100 caracteres");
        }
        
        /*if (dto.getCodPyto() == null) {
            errores.add("El código de proyecto es obligatorio");
        }*/ //ya no necesario
        
        /*if (dto.getCodPersona() == null) {
            errores.add("El código de persona es obligatorio");
        }*/ //tampoco
        
        if (dto.getTipoCanvas() == null || dto.getTipoCanvas().getTipCanvas() == null) {
            errores.add("El tipo de canvas es obligatorio");
        }
        
        if (dto.getEstado() == null || dto.getEstado().getCodEstado() == null) {
            errores.add("El estado es obligatorio");
        }
        
        if (!errores.isEmpty()) {
            throw new ExcepcionValidacion("Errores de validación", errores);
        }
    }

    /*
     * Valida que no exista un canvas con el mismo nombre.
     */
    public void validarNombreUnico(String nombre, Long codCanvasActual) {
        canvasDAO.findByNomCanvas(nombre).ifPresent(existente -> {
            if (codCanvasActual == null || !existente.getCodCanvas().equals(codCanvasActual)) {
                throw new ExcepcionValidacion(
                    "Ya existe un canvas con el nombre: " + nombre
                );
            }
        });
    }
}
