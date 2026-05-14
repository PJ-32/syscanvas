package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.dto.SC.SC_TareaDTO;
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
 * Controlador REST para gestión de Tareas.
 * 
 * Endpoints:
 * - GET    /api/sc/tarea                    → Lista con paginación
 * - GET    /api/sc/tarea/{id}               → Buscar por ID
 * - GET    /api/sc/tarea/persona/{id}       → Buscar por analista
 * - GET    /api/sc/tarea/etapa/{id}         → Buscar por etapa
 * - POST   /api/sc/tarea                    → Crear
 * - PUT    /api/sc/tarea/{id}               → Actualizar
 * - PUT    /api/sc/tarea/{id}/estado        → Cambiar estado
 * - DELETE /api/sc/tarea/{id}               → Eliminar
 */

@RestController
@RequestMapping("/api/sc/tarea")
@CrossOrigin(origins = "*")
public class SC_TareaController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SC_TareaController.class);
    private final SC_TareaService tareaService;

    public SC_TareaController(SC_TareaService tareaService) {
        this.tareaService = tareaService;
    }

    /*
     * Lista todas las tareas con paginación.
     */
    @GetMapping
public ResponseEntity<Page<SC_TareaDTO>> listarTodos(
        @PageableDefault(size = 20, sort = "fecCreacion", direction = Sort.Direction.DESC)
        Pageable pageable
) {
    logger.debug("GET /api/sc/tarea - Listando tareas");

    Page<SC_TareaDTO> tareas = tareaService.listarTodos(pageable)
            .map(tareaService::convertirADTO);

    return ResponseEntity.ok(tareas);
}

    /*
     * Busca tareas por persona (analista).
     */
    @GetMapping("/persona/{codPersona}")
public ResponseEntity<Page<SC_TareaDTO>> buscarPorPersona(
        @PathVariable Long codPersona,
        @PageableDefault(size = 20) Pageable pageable
) {
    logger.debug("GET /api/sc/tarea/persona/{} - Buscando tareas", codPersona);

    Page<SC_TareaDTO> tareas = tareaService.buscarPorPersona(codPersona, pageable)
            .map(tareaService::convertirADTO);

    return ResponseEntity.ok(tareas);
}

    /*
     * Busca tareas por etapa.
     */
    @GetMapping("/etapa/{codEtapa}")
public ResponseEntity<List<SC_TareaDTO>> buscarPorEtapa(@PathVariable Long codEtapa) {
    logger.debug("GET /api/sc/tarea/etapa/{} - Buscando tareas", codEtapa);

    List<SC_TareaDTO> tareas = tareaService.buscarPorEtapa(codEtapa)
            .stream()
            .map(tareaService::convertirADTO)
            .toList();

    return ResponseEntity.ok(tareas);
}

    /*
     * Busca una tarea por ID.
     */
    @GetMapping("/{id}")
public ResponseEntity<SC_TareaDTO> buscarPorId(@PathVariable Long id) {
    logger.debug("GET /api/sc/tarea/{} - Buscando tarea", id);

    SC_Tarea tarea = tareaService.buscarPorId(id);
    return ResponseEntity.ok(tareaService.convertirADTO(tarea));
}

    /*
     * Crea una nueva tarea.
     */
    @PostMapping
    public ResponseEntity<RespuestaExito<SC_Tarea>> guardar(
            @Valid @RequestBody SC_Tarea tarea
    ) {
        logger.info("POST /api/sc/tarea - Creando nueva tarea");
        
        SC_Tarea nuevaTarea = tareaService.guardar(tarea);
        
        RespuestaExito<SC_Tarea> respuesta = new RespuestaExito<>(
            "Tarea creada exitosamente",
            nuevaTarea
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
    
    /*
     * Actualiza una tarea existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaExito<SC_Tarea>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SC_Tarea tarea
    ) {
        logger.info("PUT /api/sc/tarea/{} - Actualizando tarea", id);
        
        SC_Tarea tareaActualizada = tareaService.actualizar(id, tarea);
        
        RespuestaExito<SC_Tarea> respuesta = new RespuestaExito<>(
            "Tarea actualizada exitosamente",
            tareaActualizada
        );
        
        return ResponseEntity.ok(respuesta);
    }
    @PostMapping("/crear")
public ResponseEntity<RespuestaExito<SC_Tarea>> crearTareaDesdeDTO(
        @Valid @RequestBody com.syscanvas.syscanvas.dto.SC.SC_TareaDTO tareaDTO
) {
    logger.info("POST /api/sc/tarea/crear - Creando nueva tarea desde DTO");

    SC_Tarea nuevaTarea = tareaService.crearDesdeDTO(tareaDTO);

    RespuestaExito<SC_Tarea> respuesta = new RespuestaExito<>(
            "Tarea creada exitosamente",
            nuevaTarea
    );

    return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
}
    @PutMapping("/{id}/mover/{nuevoCodEtapa}")
public ResponseEntity<RespuestaExito<SC_Tarea>> moverTarea(
        @PathVariable Long id,
        @PathVariable Long nuevoCodEtapa) {

    logger.info("PUT /api/sc/tarea/{}/mover/{} - Moviendo tarea", id, nuevoCodEtapa);

    SC_Tarea tareaActualizada = tareaService.moverTarea(id, nuevoCodEtapa);

    RespuestaExito<SC_Tarea> respuesta = new RespuestaExito<>(
            "Tarea movida correctamente",
            tareaActualizada
    );

    return ResponseEntity.ok(respuesta);
}

    /*
     * Cambia el estado de una tarea (completar/reabrir).
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<RespuestaExito<SC_Tarea>> cambiarEstado(
            @PathVariable Long id,
            @RequestBody CambiarEstadoTareaRequest request
    ) {
        logger.info("PUT /api/sc/tarea/{}/estado - Cambiando estado", id);
        
        SC_Tarea tareaActualizada = tareaService.cambiarEstado(id, request.completar());
        
        String mensaje = request.completar() 
            ? "Tarea marcada como completada" 
            : "Tarea reabierta";
        
        RespuestaExito<SC_Tarea> respuesta = new RespuestaExito<>(
            mensaje,
            tareaActualizada
        );
        
        return ResponseEntity.ok(respuesta);
    }

    /*
     * Elimina una tarea.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RespuestaExito<Void>> eliminar(@PathVariable Long id) {
        logger.info("DELETE /api/sc/tarea/{} - Eliminando tarea", id);
        
        tareaService.eliminar(id);
        
        RespuestaExito<Void> respuesta = new RespuestaExito<>(
            "Tarea eliminada exitosamente",
            null
        );
        
        return ResponseEntity.ok(respuesta);
    }
}