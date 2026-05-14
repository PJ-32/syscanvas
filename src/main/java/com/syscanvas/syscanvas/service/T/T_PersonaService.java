package com.syscanvas.syscanvas.service.T;

import com.syscanvas.syscanvas.dao.T.*;
import com.syscanvas.syscanvas.exception.*;
import com.syscanvas.syscanvas.model.T.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.slf4j.*;
import java.util.*;

/*
 * Servicio para gestionar Personas (base de Empleados y Clientes).
 * 
 * Responsabilidades:
 * - CRUD de personas
 * - Validación de datos básicos
 * - Consultas generales
 */

@Service
@Transactional(transactionManager = "tTransactionManager", readOnly = true)
public class T_PersonaService {
    private static final Logger logger = LoggerFactory.getLogger(T_PersonaService.class);
    private final T_PersonaDAO personaDAO;

    public T_PersonaService(T_PersonaDAO personaDAO) {
        this.personaDAO = personaDAO;
    }

    /*
     * Lista todas las personas con paginación.
     */
    public Page<T_Persona> listarTodos(Pageable pageable) {
        logger.debug("Listando personas - Página: {}", pageable.getPageNumber());

        return personaDAO.findAll(pageable);
    }

    /*
     * Busca personas por tipo de persona.
     */
    public List<T_Persona> buscarPorTipoPersona(T_Tipo_Persona tipoPersona) {
        logger.debug("Buscando personas del tipo: {}", tipoPersona.getTipPersona());

        return personaDAO.findByTipoPersona(tipoPersona);
    }

    /*
     * Busca personas por tipo de persona (con paginación).
     */
    public Page<T_Persona> buscarPorTipoPersona(T_Tipo_Persona tipoPersona, Pageable pageable) {
        logger.debug("Buscando personas del tipo: {} (paginado)", tipoPersona.getTipPersona());
        
        List<T_Persona> personas = personaDAO.findByTipoPersona(tipoPersona);
        
        int inicio = (int) pageable.getOffset();
        int fin = Math.min((inicio + pageable.getPageSize()), personas.size());
        
        List<T_Persona> personasPaginadas = personas.subList(inicio, fin);
        
        return new PageImpl<>(personasPaginadas, pageable, personas.size());
    }

    /*
     * Busca una persona por ID.
     */
    public T_Persona buscarPorId(Long codPersona) {
        logger.debug("Buscando persona ID: {}", codPersona);

        return personaDAO.findById(codPersona)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                    "Persona", "codPersona", codPersona
                ));
    }

    /*
     * Busca una persona por ID (devuelve Optional).
     */
    public Optional<T_Persona> buscarPorCodPersona(Long codPersona) {
        logger.debug("Buscando persona ID: {}", codPersona);

        return personaDAO.findById(codPersona);
    }


    /*
     * Guarda una nueva persona.
     */
    /*@Transactional
    public T_Persona guardar(T_Persona persona) {
        logger.info("Guardando nueva persona: {}", persona.getDesPersona());
        
        validarPersona(persona);
        
        T_Persona guardada = personaDAO.save(persona);
        
        logger.info("Persona guardada con ID: {}", guardada.getCodPersona());
        
        return guardada;
    }*/

    /*
     * Actualiza una persona existente.
     */
    /*@Transactional
    public T_Persona actualizar(Long codPersona, T_Persona personaActualizada) {
        logger.info("Actualizando persona ID: {}", codPersona);
        
        T_Persona existente = buscarPorId(codPersona);
        
        validarPersona(personaActualizada);
        
        actualizarCamposPersona(existente, personaActualizada);
        
        T_Persona actualizada = personaDAO.save(existente);
        
        logger.info("Persona actualizada exitosamente");
        
        return actualizada;
    }*/

    // ========== MÉTODOS PRIVADOS ==========

    /*private void validarPersona(T_Persona persona) {
        List<String> errores = new ArrayList<>();
        
        if (persona.getCodPersona() == null || persona.getCodPersona() <= 0) {
            errores.add("El código de persona es obligatorio y debe ser positivo");
        }
        
        if (persona.getTipoPersona() == null) {
            errores.add("El tipo de persona es obligatorio");
        }
        
        if (persona.getDesPersona() != null && persona.getDesPersona().length() > 100) {
            errores.add("La descripción no puede exceder 100 caracteres");
        }
        
        if (persona.getDesCorta() != null && persona.getDesCorta().length() > 30) {
            errores.add("La descripción corta no puede exceder 30 caracteres");
        }
        
        if (!errores.isEmpty()) {
            throw new ExcepcionValidacion("Errores de validación", errores);
        }
    }

    private void actualizarCamposPersona(T_Persona persona, T_Persona actualizada) {
        if (actualizada.getTipoPersona() != null) {
            persona.setTipoPersona(actualizada.getTipoPersona());
        }
        if (actualizada.getDesPersona() != null) {
            persona.setDesPersona(actualizada.getDesPersona());
        }
        if (actualizada.getDesCorta() != null) {
            persona.setDesCorta(actualizada.getDesCorta());
        }
    }*/
}
