package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.exception.*;
import com.syscanvas.syscanvas.model.SC.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.slf4j.*;
import java.util.*;
import java.util.stream.*;

/*
 * Servicio para gestionar Etapas de Canvas.
 * 
 * Responsabilidades:
 * - CRUD de etapas
 * - Validación de orden de etapas (numEtapa)
 * - Gestión de vigencia
 * - Consultas por canvas
 */

@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_EtapaService {
    private static final Logger logger = LoggerFactory.getLogger(SC_EtapaService.class);
    private static final Integer VIGENCIA_ACTIVA = 1;
    private static final Integer VIGENCIA_INACTIVA = 0;

    private final SC_EtapaDAO etapaDAO;
    private final SC_CanvasDAO canvasDAO;

    public SC_EtapaService(SC_EtapaDAO etapaDAO, SC_CanvasDAO canvasDAO) {
        this.etapaDAO = etapaDAO;
        this.canvasDAO = canvasDAO;
    }

    /*
     * Lista todas las etapas con paginación.
     */
    @Transactional(readOnly = true)
    public Page<SC_Etapa> listarTodos(Pageable pageable) {
        logger.debug("Listando etapas - Página: {}", pageable.getPageNumber());

        return etapaDAO.findAll(pageable);
    }

    /*
     * Busca etapas por canvas ordenadas por numEtapa.
     */
    @Transactional(readOnly = true)
    public List<SC_Etapa> buscarPorCanvas(Long codCanvas) {
        logger.debug("Buscando etapas del canvas: {}", codCanvas);

        SC_Canvas canvas = canvasDAO.findById(codCanvas)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                    "Canvas", "codCanvas", codCanvas
                ));

        List<SC_Etapa> etapas = etapaDAO.findByCanvas(canvas);
        
        // Ordenar por numEtapa
        return etapas.stream()
                .sorted(Comparator.comparing(SC_Etapa::getNumEtapa))
                .collect(Collectors.toList());
    }


    /*
     * Busca una etapa por ID.
     */
    @Transactional(readOnly = true)
    public SC_Etapa buscarPorId(Long codEtapa) {
        logger.debug("Buscando etapa ID: {}", codEtapa);

        return etapaDAO.findById(codEtapa)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                    "Etapa", "codEtapa", codEtapa
                ));
    }

    /*
     * Guarda una nueva etapa.
     */
    public SC_Etapa guardar(SC_Etapa etapa) {
        logger.info("Guardando nueva etapa: {}", etapa.getNomEtapa());
        
        validarEtapa(etapa);
        validarNumeroEtapa(etapa);
        
        etapa.setVigente(VIGENCIA_ACTIVA);
        
        SC_Etapa guardada = etapaDAO.save(etapa);
        
        logger.info("Etapa guardada con ID: {}", guardada.getCodEtapa());
        
        return guardada;
    }

    /*
     * Actualiza una etapa existente.
     */
    public SC_Etapa actualizar(Long codEtapa, SC_Etapa etapaActualizada) {
        logger.info("Actualizando etapa ID: {}", codEtapa);
        
        SC_Etapa existente = buscarPorId(codEtapa);
        
        validarEtapa(etapaActualizada);
        
        // Verificar que el canvas esté editable
        if (existente.getCanvas() != null && !existente.getCanvas().getEditable()) {
            throw new ExcepcionValidacion(
                "No se puede editar la etapa porque el canvas está bloqueado"
            );
        }
        
        actualizarCamposEtapa(existente, etapaActualizada);
        
        SC_Etapa actualizada = etapaDAO.save(existente);
        
        logger.info("Etapa actualizada exitosamente");
        
        return actualizada;
    }

    /*
     * Cambia la vigencia de una etapa.
     */
    public SC_Etapa cambiarVigencia(Long codEtapa, boolean activar) {
        logger.info("Cambiando vigencia de etapa {} a: {}", 
            codEtapa, activar ? "ACTIVA" : "INACTIVA");
        
        SC_Etapa etapa = buscarPorId(codEtapa);
        
        etapa.setVigente(activar ? VIGENCIA_ACTIVA : VIGENCIA_INACTIVA);
        
        SC_Etapa actualizada = etapaDAO.save(etapa);
        
        logger.info("Vigencia de etapa cambiada exitosamente");
        
        return actualizada;
    }

    /*
     * Elimina una etapa.
     */
    public void eliminar(Long codEtapa) {
        logger.info("Eliminando etapa ID: {}", codEtapa);
        
        SC_Etapa etapa = buscarPorId(codEtapa);
        
        // Verificar que no tenga tareas
        if (etapa.getTareas() != null && !etapa.getTareas().isEmpty()) {
            throw new ExcepcionValidacion(
                "No se puede eliminar la etapa porque tiene tareas asociadas"
            );
        }

        etapaDAO.deleteById(codEtapa);
        
        logger.info("Etapa eliminada exitosamente");
    }

    /*
     * Reordena etapas de un canvas.
     */
    public List<SC_Etapa> reordenarEtapas(Long codCanvas, List<Long> ordenEtapas) {
        logger.info("Reordenando etapas del canvas: {}", codCanvas);
        
        List<SC_Etapa> etapas = buscarPorCanvas(codCanvas);
        
        if (etapas.size() != ordenEtapas.size()) {
            throw new ExcepcionValidacion(
                "La cantidad de etapas no coincide con el orden proporcionado"
            );
        }
        
        for (int i = 0; i < ordenEtapas.size(); i++) {
            Long codEtapa = ordenEtapas.get(i);
            SC_Etapa etapa = etapas.stream()
                    .filter(e -> e.getCodEtapa().equals(codEtapa))
                    .findFirst()
                    .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                        "Etapa", "codEtapa", codEtapa
                    ));
            
            etapa.setNumEtapa(i + 1);
            etapaDAO.save(etapa);
        }
        
        logger.info("Etapas reordenadas exitosamente");
        
        return buscarPorCanvas(codCanvas);
    }

    // ========== MÉTODOS PRIVADOS ==========

    private void validarEtapa(SC_Etapa etapa) {
        List<String> errores = new ArrayList<>();
        
        if (etapa.getNomEtapa() == null || etapa.getNomEtapa().isBlank()) {
            errores.add("El nombre de la etapa es obligatorio");
        }
        
        if (etapa.getNomEtapa() != null && etapa.getNomEtapa().length() > 100) {
            errores.add("El nombre no puede exceder 100 caracteres");
        }
        
        if (etapa.getCanvas() == null) {
            errores.add("El canvas es obligatorio");
        }
        
        if (etapa.getNumEtapa() == null || etapa.getNumEtapa() < 1) {
            errores.add("El número de etapa debe ser mayor a 0");
        }
        
        if (!errores.isEmpty()) {
            throw new ExcepcionValidacion("Errores de validación", errores);
        }
    }

    private void validarNumeroEtapa(SC_Etapa etapa) {
        if (etapa.getCanvas() == null) return;
        
        List<SC_Etapa> etapasExistentes = etapaDAO.findByCanvas(etapa.getCanvas());
        
        boolean numeroYaExiste = etapasExistentes.stream()
                .anyMatch(e -> e.getNumEtapa().equals(etapa.getNumEtapa()) &&
                            !e.getCodEtapa().equals(etapa.getCodEtapa()));
        
        if (numeroYaExiste) {
            throw new ExcepcionValidacion(
                "Ya existe una etapa con el número " + etapa.getNumEtapa() + 
                " en este canvas"
            );
        }
    }

    private void actualizarCamposEtapa(SC_Etapa etapa, SC_Etapa actualizada) {
        if (actualizada.getNomEtapa() != null) {
            etapa.setNomEtapa(actualizada.getNomEtapa());
        }
        if (actualizada.getDesEtapa() != null) {
            etapa.setDesEtapa(actualizada.getDesEtapa());
        }
        if (actualizada.getNumEtapa() != null) {
            etapa.setNumEtapa(actualizada.getNumEtapa());
        }
    }
}