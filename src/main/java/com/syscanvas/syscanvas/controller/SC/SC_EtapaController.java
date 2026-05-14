package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.dto.request.*;
import com.syscanvas.syscanvas.dto.response.*;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;
import jakarta.validation.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.*;
import java.util.*;

/*
 * Controlador REST para gestión de Etapas.
 * 
 * Endpoints:
 * - GET    /api/sc/etapa                   → Lista con paginación
 * - GET    /api/sc/etapa/{id}              → Buscar por ID
 * - GET    /api/sc/etapa/canvas/{id}       → Buscar por canvas
 * - POST   /api/sc/etapa                   → Crear
 * - PUT    /api/sc/etapa/{id}              → Actualizar
 * - PUT    /api/sc/etapa/{id}/vigencia     → Cambiar vigencia
 * - POST   /api/sc/etapa/reordenar         → Reordenar etapas
 * - DELETE /api/sc/etapa/{id}              → Eliminar
 */

@RestController
@RequestMapping("/api/sc/etapa")
@CrossOrigin(origins = "*")
public class SC_EtapaController  extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SC_EtapaController.class);
    private final SC_EtapaService etapaService;

    public SC_EtapaController(SC_EtapaService etapaService) {
        this.etapaService = etapaService;
    }

    /*
     * Lista todas las etapas con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<SC_Etapa>> listarTodos(
            @PageableDefault(size = 20, sort = "numEtapa", direction = Sort.Direction.ASC) 
            Pageable pageable
    ) {
        logger.debug("GET /api/sc/etapa - Listando etapas");
        
        Page<SC_Etapa> etapas = etapaService.listarTodos(pageable);
        
        return ResponseEntity.ok(etapas);
    }

    /*
     * Busca etapas por canvas.
     */
    @GetMapping("/canvas/{codCanvas}")
    public ResponseEntity<List<SC_Etapa>> buscarPorCanvas(@PathVariable Long codCanvas) {
        logger.debug("GET /api/sc/etapa/canvas/{} - Buscando etapas", codCanvas);
        
        List<SC_Etapa> etapas = etapaService.buscarPorCanvas(codCanvas);
        
        return ResponseEntity.ok(etapas);
    }

    /*
     * Busca una etapa por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<SC_Etapa> buscarPorId(@PathVariable Long id) {
        logger.debug("GET /api/sc/etapa/{} - Buscando etapa", id);
        
        SC_Etapa etapa = etapaService.buscarPorId(id);
        
        return ResponseEntity.ok(etapa);
    }

    /*
     * Crea una nueva etapa.
     */
    @PostMapping
    public ResponseEntity<RespuestaExito<SC_Etapa>> guardar(
            @Valid @RequestBody SC_Etapa etapa
    ) {
        logger.info("POST /api/sc/etapa - Creando nueva etapa");
        
        SC_Etapa nuevaEtapa = etapaService.guardar(etapa);
        
        RespuestaExito<SC_Etapa> respuesta = new RespuestaExito<>(
            "Etapa creada exitosamente",
            nuevaEtapa
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    /*
     * Actualiza una etapa existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaExito<SC_Etapa>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SC_Etapa etapa
    ) {
        logger.info("PUT /api/sc/etapa/{} - Actualizando etapa", id);
        
        SC_Etapa etapaActualizada = etapaService.actualizar(id, etapa);
        
        RespuestaExito<SC_Etapa> respuesta = new RespuestaExito<>(
            "Etapa actualizada exitosamente",
            etapaActualizada
        );
        
        return ResponseEntity.ok(respuesta);
    }

    /*
     * Cambia la vigencia de una etapa.
     */
    @PutMapping("/{id}/vigencia")
    public ResponseEntity<RespuestaExito<SC_Etapa>> cambiarVigencia(
            @PathVariable Long id,
            @RequestBody CambiarVigenciaRequest request
    ) {
        logger.info("PUT /api/sc/etapa/{}/vigencia - Cambiando vigencia", id);
        
        SC_Etapa etapaActualizada = etapaService.cambiarVigencia(id, request.activar());
        
        String mensaje = request.activar() 
            ? "Etapa activada" 
            : "Etapa desactivada";
        
        RespuestaExito<SC_Etapa> respuesta = new RespuestaExito<>(
            mensaje,
            etapaActualizada
        );
        
        return ResponseEntity.ok(respuesta);
    }

    /*
     * Reordena las etapas de un canvas.
     */
    @PostMapping("/reordenar")
    public ResponseEntity<RespuestaExito<List<SC_Etapa>>> reordenarEtapas(
            @RequestBody ReordenarEtapasRequest request
    ) {
        logger.info("POST /api/sc/etapa/reordenar - Reordenando etapas del canvas {}", 
            request.codCanvas());
        
        List<SC_Etapa> etapasReordenadas = etapaService.reordenarEtapas(
            request.codCanvas(), 
            request.ordenEtapas()
        );
        
        RespuestaExito<List<SC_Etapa>> respuesta = new RespuestaExito<>(
            "Etapas reordenadas exitosamente",
            etapasReordenadas
        );
        
        return ResponseEntity.ok(respuesta);
    }

    /*
     * Elimina una etapa.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RespuestaExito<Void>> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/sc/etapa/{} - Eliminando etapa", id);
        
        etapaService.eliminar(id);
        
        RespuestaExito<Void> respuesta = new RespuestaExito<>(
            "Etapa eliminada exitosamente",
            null
        );
        
        return ResponseEntity.ok(respuesta);
    }
}