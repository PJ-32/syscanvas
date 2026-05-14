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
 * Servicio para gestionar Proyectos.
 * 
 * Responsabilidades:
 * - CRUD de proyectos
 * - Validación de fechas (annoIni <= annoFin)
 * - Consultas por cliente
 * - Gestión de vigencia
 */

@Service
@Transactional(transactionManager = "tTransactionManager", readOnly = true)
public class T_ProyectoService {
    private static final Logger logger = LoggerFactory.getLogger(T_ProyectoService.class);
    private static final Integer VIGENTE_ACTIVO = 1;
    //private static final Integer VIGENTE_INACTIVO = 0;
    private final T_ProyectoDAO proyectoDAO;

    public T_ProyectoService(T_ProyectoDAO proyectoDAO) {
        this.proyectoDAO = proyectoDAO;
    }

    /*
     * Lista todos los proyectos con paginación.
     */
    public Page<T_Proyecto> listarTodos(Pageable pageable) {
        logger.debug("Listando proyectos - Página: {}", pageable.getPageNumber());
        
        return proyectoDAO.findAll(pageable);
    }

    /*
     * Lista proyectos activos (vigente = 1).
     */
    public Page<T_Proyecto> listarActivos(Pageable pageable) {
        logger.debug("Listando proyectos activos");
        
        List<T_Proyecto> activos = proyectoDAO.findByVigente(VIGENTE_ACTIVO);
        
        int inicio = (int) pageable.getOffset();
        int fin = Math.min((inicio + pageable.getPageSize()), activos.size());
        
        List<T_Proyecto> proyectosPaginados = activos.subList(inicio, fin);
        
        return new PageImpl<>(proyectosPaginados, pageable, activos.size());
    }

    /*
     * Busca un proyecto por ID.
     */
    public T_Proyecto buscarPorId(Long codPyto) {
        logger.debug("Buscando proyecto ID: {}", codPyto);
        
        return proyectoDAO.findByCodPyto(codPyto)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                    "Proyecto", "codPyto", codPyto
                ));
    }

    /*
     * Guarda un nuevo proyecto.
     */
    /*@Transactional
    public T_Proyecto guardar(T_Proyecto proyecto) {
        logger.info("Guardando nuevo proyecto: {}", proyecto.getNomPyto());
        
        validarProyecto(proyecto);
        
        proyecto.setVigente(VIGENTE_ACTIVO);
        
        T_Proyecto guardado = proyectoDAO.save(proyecto);
        
        logger.info("Proyecto guardado con ID: {}", guardado.getCodPyto());
        
        return guardado;
    }*/

    /*
     * Actualiza un proyecto existente.
     */
    /*@Transactional(transactionManager = "tTransactionManager")
    public T_Proyecto actualizar(Long codPyto, T_Proyecto proyectoActualizado) {
        logger.info("Actualizando proyecto ID: {}", codPyto);
        
        T_Proyecto existente = buscarPorId(codPyto);
        
        validarProyecto(proyectoActualizado);
        
        actualizarCamposProyecto(existente, proyectoActualizado);
        
        T_Proyecto actualizado = proyectoDAO.save(existente);
        
        logger.info("Proyecto actualizado exitosamente");
        
        return actualizado;
    }*/

    /*
     * Cambia la vigencia de un proyecto.
     */
    /*@Transactional(transactionManager = "tTransactionManager")
     public T_Proyecto cambiarVigencia(Long codPyto, boolean activar) {
        logger.info("Cambiando vigencia de proyecto {} a: {}", 
            codPyto, activar ? "ACTIVO" : "INACTIVO");
        
        T_Proyecto proyecto = buscarPorId(codPyto);
        
        proyecto.setVigente(activar ? VIGENTE_ACTIVO : VIGENTE_INACTIVO);
        
        T_Proyecto actualizado = proyectoDAO.save(proyecto);
        
        logger.info("Vigencia de proyecto cambiada exitosamente");
        
        return actualizado;
    }*/

    // ========== MÉTODOS PRIVADOS ==========

    /*private void validarProyecto(T_Proyecto proyecto) {
        List<String> errores = new ArrayList<>();
        
        if (proyecto.getNomPyto() == null || proyecto.getNomPyto().isBlank()) {
            errores.add("El nombre del proyecto es obligatorio");
        }
        
        if (proyecto.getNomPyto() != null && proyecto.getNomPyto().length() > 1000) {
            errores.add("El nombre no puede exceder 1000 caracteres");
        }
        
        if (proyecto.getAnnoIni() != null && proyecto.getAnnoFin() != null) {
            if (proyecto.getAnnoIni() > proyecto.getAnnoFin()) {
                errores.add("El año inicial no puede ser mayor al año final");
            }
            
            if (proyecto.getAnnoIni() < 2000 || proyecto.getAnnoIni() > 2100) {
                errores.add("El año inicial debe estar entre 2000 y 2100");
            }
            
            if (proyecto.getAnnoFin() < 2000 || proyecto.getAnnoFin() > 2100) {
                errores.add("El año final debe estar entre 2000 y 2100");
            }
        }
        
        if (!errores.isEmpty()) {
            throw new ExcepcionValidacion("Errores de validación", errores);
        }
    }

    private void actualizarCamposProyecto(T_Proyecto proyecto, T_Proyecto actualizado) {
        if (actualizado.getNomPyto() != null) {
            proyecto.setNomPyto(actualizado.getNomPyto());
        }
        if (actualizado.getAnnoIni() != null) {
            proyecto.setAnnoIni(actualizado.getAnnoIni());
        }
        if (actualizado.getAnnoFin() != null) {
            proyecto.setAnnoFin(actualizado.getAnnoFin());
        }
        if (actualizado.getJefeProyecto() != null) {
            proyecto.setJefeProyecto(actualizado.getJefeProyecto());
        }
    }*/
}
