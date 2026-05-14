package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.dto.SC.SC_TareaDTO;
import com.syscanvas.syscanvas.exception.*;
import com.syscanvas.syscanvas.model.SC.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.slf4j.*;
import java.time.*;
import java.util.*;

/*
 * Servicio para gestionar Tareas del sistema.
 * 
 * Responsabilidades:
 * - CRUD de tareas
 * - Cambio de estado (completar/reabrir)
 * - Asignación de tareas a analistas
 * - Validaciones de negocio
 */

@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_TareaService {
    private static final Logger logger = LoggerFactory.getLogger(SC_TareaService.class);
    private static final Long VIGENCIA_COMPLETADA = 0L;
    private static final Long VIGENCIA_PENDIENTE = 1L;
    private final SC_TareaDAO tareaDAO;
    private final SC_EtapaDAO etapaDAO;

    public SC_TareaService(SC_TareaDAO tareaDAO, SC_EtapaDAO etapaDAO) {
        this.tareaDAO = tareaDAO;
        this.etapaDAO = etapaDAO;
    }

    /*
     * Lista todas las tareas con paginación.
     */
    @Transactional(readOnly = true)
    public Page<SC_Tarea> listarTodos(Pageable pageable) {
    return tareaDAO.findAllConEtapaYCanvas(pageable);
}


    /*
     * Busca tareas por persona (analista) con paginación.
     */
    @Transactional(readOnly = true)
    public Page<SC_Tarea> buscarPorPersona(Long codPersona, Pageable pageable) {

    List<SC_Tarea> tareas = tareaDAO.findByCodPersonaConCanvas(codPersona);

    int inicio = (int) pageable.getOffset();
    int fin = Math.min(inicio + pageable.getPageSize(), tareas.size());

    return new PageImpl<>(tareas.subList(inicio, fin), pageable, tareas.size());
}


    /*
     * Busca tareas por etapa.
     */
    @Transactional(readOnly = true)
    public List<SC_Tarea> buscarPorEtapa(Long codEtapa) {
    return tareaDAO.findByEtapaConCanvas(codEtapa);
}


    /*
     * Busca una tarea por ID.
     */
    @Transactional(readOnly = true)
    public SC_Tarea buscarPorId(Long codTarea) {
        logger.debug("Buscando tarea ID: {}", codTarea);

        return tareaDAO.findById(codTarea)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                    "Tarea", "codTarea", codTarea
                ));
    }

    /*
     * Guarda una nueva tarea.
     */
    public SC_Tarea guardar(SC_Tarea tarea) {
        logger.info("Guardando nueva tarea: {}", tarea.getNomTarea());
        
        validarTarea(tarea);
        
        tarea.setFecCreacion(LocalDateTime.now());
        tarea.setFecModificacion(LocalDateTime.now());
        tarea.setVigente(VIGENCIA_PENDIENTE);
        
        SC_Tarea guardada = tareaDAO.save(tarea);
        
        logger.info("Tarea guardada con ID: {}", guardada.getCodTarea());
        
        return guardada;
    }

    /*
     * Actualiza una tarea existente.
     */
    public SC_Tarea actualizar(Long codTarea, SC_Tarea tareaActualizada) {
        logger.info("Actualizando tarea ID: {}", codTarea);
        
        SC_Tarea existente = buscarPorId(codTarea);
        
        validarTarea(tareaActualizada);
        
        // Verificar que el canvas esté editable
        if (existente.getEtapa() != null && 
            existente.getEtapa().getCanvas() != null && 
            !existente.getEtapa().getCanvas().getEditable()) {
            throw new ExcepcionValidacion(
                "No se puede editar la tarea porque el canvas está bloqueado"
            );
        }
        
        actualizarCamposTarea(existente, tareaActualizada);
        existente.setFecModificacion(LocalDateTime.now());
        
        SC_Tarea actualizada = tareaDAO.save(existente);
        
        logger.info("Tarea actualizada exitosamente");
        
        return actualizada;
    }

    @Transactional
    public SC_Tarea crearDesdeDTO(SC_TareaDTO dto) {
    logger.info("Creando tarea desde DTO: {}", dto.getNomTarea());

    SC_Etapa etapa = etapaDAO.findById(dto.getCodEtapa())
            .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                "Etapa", "codEtapa", dto.getCodEtapa()
            ));

    SC_Tarea tarea = new SC_Tarea();
    tarea.setNomTarea(dto.getNomTarea());
    tarea.setDesTarea(dto.getDesTarea());
    tarea.setEtapa(etapa);
    tarea.setCodPersona(dto.getCodPersona());
    tarea.setNumTarea(dto.getNumTarea() != null ? dto.getNumTarea() : 1L);
    tarea.setVigente(1L);
    tarea.setFecCreacion(LocalDateTime.now());
    tarea.setFecModificacion(LocalDateTime.now());

    return tareaDAO.save(tarea);
}
public SC_TareaDTO convertirADTO(SC_Tarea tarea) {
    return SC_TareaDTO.builder()
        .codTarea(tarea.getCodTarea())
        .nomTarea(tarea.getNomTarea())
        .desTarea(tarea.getDesTarea())
        .fecCreacion(tarea.getFecCreacion())
        .fecModificacion(tarea.getFecModificacion())
        .numTarea(tarea.getNumTarea())
        .vigente(tarea.getVigente())
        .codPersona(tarea.getCodPersona())

        .codEtapa(tarea.getEtapa().getCodEtapa())
        .codCanvas(tarea.getEtapa().getCanvas().getCodCanvas())

        .build();
}

    /*
     * Cambia el estado de una tarea (completar/reabrir).
     */
    public SC_Tarea cambiarEstado(Long codTarea, boolean completar) {
        logger.info("Cambiando estado de tarea {} a: {}", 
            codTarea, completar ? "COMPLETADA" : "PENDIENTE");
        
        SC_Tarea tarea = buscarPorId(codTarea);
        
        // Verificar que el canvas esté editable
        if (tarea.getEtapa() != null && 
            tarea.getEtapa().getCanvas() != null && 
            !tarea.getEtapa().getCanvas().getEditable()) {
            throw new ExcepcionValidacion(
                "No se puede cambiar el estado de la tarea porque el canvas está bloqueado"
            );
        }
        
        tarea.setVigente(completar ? VIGENCIA_COMPLETADA : VIGENCIA_PENDIENTE);
        tarea.setFecModificacion(LocalDateTime.now());
        
        SC_Tarea actualizada = tareaDAO.save(tarea);
        
        logger.info("Estado de tarea cambiado exitosamente");
        
        return actualizada;
    }

    /*
     * Elimina una tarea.
     */
    public void eliminar(Long codTarea) {
        logger.info("Eliminando tarea ID: {}", codTarea);
        
        SC_Tarea tarea = buscarPorId(codTarea);
        
        // Verificar que el canvas esté editable
        if (tarea.getEtapa() != null && 
            tarea.getEtapa().getCanvas() != null && 
            !tarea.getEtapa().getCanvas().getEditable()) {
            throw new ExcepcionValidacion(
                "No se puede eliminar la tarea porque el canvas está bloqueado"
            );
        }

        tareaDAO.deleteById(codTarea);
        
        logger.info("Tarea eliminada exitosamente");
    }
    
    @Transactional
    public SC_Tarea moverTarea(Long codTarea, Long nuevoCodEtapa) {
    SC_Tarea tarea = buscarPorId(codTarea);
    SC_Etapa nuevaEtapa = etapaDAO.findById(nuevoCodEtapa)
            .orElseThrow(() -> new ExcepcionRecursoNoEncontrado("Etapa", "codEtapa", nuevoCodEtapa));

    tarea.setEtapa(nuevaEtapa);
    tarea.setFecModificacion(LocalDateTime.now());

    return tareaDAO.save(tarea);
    }
    // ========== MÉTODOS PRIVADOS ==========

    private void validarTarea(SC_Tarea tarea) {
        List<String> errores = new ArrayList<>();
        
        if (tarea.getNomTarea() == null || tarea.getNomTarea().isBlank()) {
            errores.add("El nombre de la tarea es obligatorio");
        }
        
        if (tarea.getNomTarea() != null && tarea.getNomTarea().length() > 200) {
            errores.add("El nombre no puede exceder 200 caracteres");
        }
        
        if (tarea.getEtapa() == null) {
            errores.add("La etapa es obligatoria");
        }
        
        if (!errores.isEmpty()) {
            throw new ExcepcionValidacion("Errores de validación", errores);
        }
    }

    private void actualizarCamposTarea(SC_Tarea tarea, SC_Tarea actualizada) {
        if (actualizada.getNomTarea() != null) {
            tarea.setNomTarea(actualizada.getNomTarea());
        }
        if (actualizada.getDesTarea() != null) {
            tarea.setDesTarea(actualizada.getDesTarea());
        }
        if (actualizada.getVigente() != null) {
            tarea.setVigente(actualizada.getVigente());
        }
        if (actualizada.getCodPersona() != null) {
            tarea.setCodPersona(actualizada.getCodPersona());
        }
    }
}
