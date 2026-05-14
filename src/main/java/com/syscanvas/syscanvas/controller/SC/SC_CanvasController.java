package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.dto.response.*;
import com.syscanvas.syscanvas.dto.SC.*;
import com.syscanvas.syscanvas.dto.request.*;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;
import jakarta.validation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/*
 * Controlador REST para gestión de Canvas con paginación.
 * 
* Responsabilidad: Exponer endpoints REST y delegar lógica al servicio.
 * 
 * Endpoints:
 * - GET    /api/sc/canvas               → Lista con paginación
 * - GET    /api/sc/canvas/{id}          → Buscar por ID
 * - GET    /api/sc/canvas/proyecto/{id} → Buscar por proyecto
 * - POST   /api/sc/canvas               → Crear
 * - PUT    /api/sc/canvas/{id}          → Actualizar
 * - DELETE /api/sc/canvas/{id}          → Eliminar
 * - GET    /api/sc/canvas/{id}/etapas   → Obtener etapas
 * - POST   /api/sc/canvas/{id}/toggle-edit → Cambiar estado editable
 */

@RestController
@RequestMapping("/api/sc/canvas")
@CrossOrigin(origins = "*")
public class SC_CanvasController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SC_CanvasController.class);
    private final SC_CanvasService canvasService;

    public SC_CanvasController(SC_CanvasService canvasService) {
        this.canvasService = canvasService;
    }

    /*
     * Lista todos los canvas con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<SC_CanvasDTO>> listarTodos(
            @PageableDefault(size = 10, sort = "fecCreacion", direction = Sort.Direction.DESC) 
            Pageable pageable) 
    {
        return ResponseEntity.ok(canvasService.listarTodos(pageable));
    }

    /*
     * Busca canvas por proyecto con paginación.
     */
    @GetMapping("/proyecto/{codProyecto}")
    public ResponseEntity<Page<SC_CanvasDTO>> buscarPorProyecto(
            @PathVariable Long codProyecto,
            @PageableDefault(size = 10) Pageable pageable)
    {
        return ResponseEntity.ok(canvasService.buscarPorProyecto(codProyecto, pageable));
    }

    /*
     * Busca un canvas por ID.
     */
    @GetMapping("/{id}")
public ResponseEntity<SC_CanvasDTO> buscarPorId(@PathVariable("id") Long codCanvas) {
    return ResponseEntity.ok(canvasService.buscarPorId(codCanvas));
}

    /*
     * Crea un nuevo canvas.
     */
    @PostMapping
    public ResponseEntity<RespuestaExito<SC_CanvasDTO>> guardar(
            @Valid @RequestBody SC_CanvasDTO canvasDTO)
    {
        logger.info("POST /api/sc/canvas - Creando nuevo canvas: {}", canvasDTO.getNomCanvas());
        
        SC_CanvasDTO nuevoCanvas = canvasService.guardar(canvasDTO);
        
        return respuestaCreada("Canvas creado exitosamente", nuevoCanvas);
    }

    // ✅ EXPORTAR CANVAS
    @GetMapping("/{id}/exportar")
    public ResponseEntity<?> exportarCanvas(@PathVariable Long id) {
    try {
        String json = canvasService.exportarCanvas(id);

        if (json == null || json.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("No se pudo exportar: el canvas está vacío o no existe.");
        }

        ByteArrayResource resource = new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8));

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=canvas_" + id + ".json");
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .body(resource);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al exportar canvas: " + e.getMessage());
    }
}

@PostMapping("/importar")
public ResponseEntity<Map<String, Object>> importarCanvas(
        @RequestParam("file") MultipartFile file,
        @RequestAttribute("codPersona") Long usuarioActualId // ← lo trae tu filtro JWT
) {
    Map<String, Object> response = new HashMap<>();

    try {
        SC_CanvasDTO nuevoCanvas = canvasService.importarCanvas(file, usuarioActualId);

        response.put("message", "Canvas importado exitosamente 🎉");
        response.put("data", nuevoCanvas);

        return ResponseEntity.ok(response);

    } catch (Exception e) {
        e.printStackTrace();
        response.put("error", "Error al importar el canvas");
        response.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}

    /*
     * Actualiza un canvas existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RespuestaExito<SC_CanvasDTO>> actualizar(
            @PathVariable("id") Long codCanvas,
            @Valid @RequestBody SC_CanvasDTO canvasDTO)
    {
        logger.info("PUT /api/sc/canvas/{} - Actualizando canvas", codCanvas);
        
        SC_CanvasDTO canvasActualizado = canvasService.actualizar(codCanvas, canvasDTO);
        
        return respuestaExito("Canvas actualizado exitosamente", canvasActualizado);
    }

    /*
     * Elimina un canvas.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<RespuestaExito<Void>> eliminar(@PathVariable("id") Long codCanvas) {
        logger.info("DELETE /api/sc/canvas/{} - Eliminando canvas", codCanvas);
        
        canvasService.eliminar(codCanvas);
        
        return respuestaExito("Canvas eliminado exitosamente");
    }

    /*
     * Obtiene las etapas de un canvas con sus tareas.
     */
    @GetMapping("/{id}/etapas")
    public ResponseEntity<List<SC_Etapa>> obtenerEtapas(@PathVariable("id") Long codCanvas) {
        return ResponseEntity.ok(canvasService.obtenerEtapasConTareas(codCanvas));
    }

    /*
     * Cambia el estado editable de un canvas (bloquear/desbloquear). AGREGAR ESTO AL NUEVO ARCHIVO
     */
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleEditable(@PathVariable Long id) {
        try {
        Long codPersonaActual = obtenerCodPersonaActual();
        boolean esJefe = esJefe();

        logger.info("Usuario {} intenta cambiar estado editable del canvas {} -> esJefe={}", 
                    codPersonaActual, id, esJefe);

        if (!esJefe) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                Map.of(
                    "success", false,
                    "error", "Solo un JEFE puede bloquear o desbloquear un canvas"
                )
            );
        }

        SC_CanvasDTO actualizado = canvasService.cambiarEstadoEditable(id, null);

        return ResponseEntity.ok(
            Map.of(
                "success", true,
                "data", actualizado
            )
        );

        } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            Map.of(
                "success", false,
                "error", "No se pudo cambiar estado editable"
            )
        );
    }
}

}
