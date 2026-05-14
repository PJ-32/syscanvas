package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.dao.SC.SC_HistorialDAO;
import com.syscanvas.syscanvas.model.SC.SC_Historial;
import com.syscanvas.syscanvas.model.SC.SC_Canvas;
import com.syscanvas.syscanvas.exception.ExcepcionRecursoNoEncontrado;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Servicio para gestionar el Historial de acciones en Canvas.
 * 
 * Responsabilidades:
 * - Registrar acciones (exportación, importación, modificaciones)
 * - Consultar historial por canvas o persona
 * - Proporcionar auditoría del sistema
 */
@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_HistorialService {
    
    private static final Logger logger = LoggerFactory.getLogger(SC_HistorialService.class);
    
    private final SC_HistorialDAO historialDAO;

    public SC_HistorialService(SC_HistorialDAO historialDAO) {
        this.historialDAO = historialDAO;
    }

    // ========== CONSULTAS ==========

    @Transactional(readOnly = true)
    public List<SC_Historial> listarTodos() {
        logger.debug("Listando todo el historial");
        return historialDAO.findAll();
    }
    
    @Transactional(readOnly = true)
    public SC_Historial buscarPorId(Long codHistorial) {
        logger.debug("Buscando historial ID: {}", codHistorial);
        
        return historialDAO.findById(codHistorial)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                    "Historial", "codHistorial", codHistorial
                ));
    }
    
    @Transactional(readOnly = true)
    public List<SC_Historial> buscarPorPersona(Long codPersona) {
        logger.debug("Buscando historial de la persona: {}", codPersona);
        return historialDAO.findByCodPersona(codPersona);
    }
    
    @Transactional(readOnly = true)
    public List<SC_Historial> buscarPorCanvas(SC_Canvas canvas) {
        logger.debug("Buscando historial del canvas: {}", canvas.getCodCanvas());
        return historialDAO.findByCanvas(canvas);
    }
    
    @Transactional(readOnly = true)
    public List<SC_Historial> buscarPorRangoFechas(LocalDateTime inicio, LocalDateTime fin) {
        logger.debug("Buscando historial entre {} y {}", inicio, fin);
        return historialDAO.findByFecAccionBetween(inicio, fin);
    }

    // ========== OPERACIONES ==========

    /**
     * Registra una acción en el historial.
     * Usado internamente por otros servicios para auditoría.
     */
    public SC_Historial registrar(SC_Canvas canvas, Long codPersona, 
                                   String accion, String detalle) {
        logger.info("Registrando historial: {} - Canvas: {} - Usuario: {}", 
            accion, canvas.getCodCanvas(), codPersona);
        
        SC_Historial historial = new SC_Historial();
        historial.setCanvas(canvas);
        historial.setCodPersona(codPersona);
        historial.setAccion(accion);
        historial.setDetalle(detalle);
        historial.setFecAccion(LocalDateTime.now());
        
        SC_Historial guardado = historialDAO.save(historial);
        
        logger.debug("Historial registrado con ID: {}", guardado.getCodHistorial());
        
        return guardado;
    }
    
    /**
     * Guarda un historial (uso genérico).
     */
    public SC_Historial guardar(SC_Historial historial) {
        logger.info("Guardando historial: {}", historial.getAccion());
        
        if (historial.getFecAccion() == null) {
            historial.setFecAccion(LocalDateTime.now());
        }
        
        SC_Historial guardado = historialDAO.save(historial);
        
        logger.debug("Historial guardado con ID: {}", guardado.getCodHistorial());
        
        return guardado;
    }
    
    /**
     * Elimina un registro de historial.
     * Nota: Normalmente el historial NO se elimina (auditoría).
     * Este método existe solo para casos excepcionales.
     */
    public void eliminar(Long codHistorial) {
        logger.warn("Eliminando registro de historial ID: {} - ACCIÓN EXCEPCIONAL", codHistorial);
        
        SC_Historial historial = buscarPorId(codHistorial);
        
        historialDAO.delete(historial);
        
        logger.info("Historial eliminado");
    }
}