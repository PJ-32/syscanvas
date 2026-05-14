package com.syscanvas.syscanvas.controller.T;

// import com.syscanvas.syscanvas.dto.request.*;
// import com.syscanvas.syscanvas.dto.response.*;
import com.syscanvas.syscanvas.model.T.*;
import com.syscanvas.syscanvas.service.T.*;
//import jakarta.validation.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.*;

/**
 * Controlador REST para gestión de Proyectos.
 * 
 * Endpoints:
 * - GET    /api/t/proyecto                 → Lista con paginación
 * - GET    /api/t/proyecto/activos         → Lista solo activos
 * - GET    /api/t/proyecto/{id}            → Buscar por ID
 * - GET    /api/t/proyecto/cliente/{id}    → Buscar por cliente
 * - POST   /api/t/proyecto                 → Crear
 * - PUT    /api/t/proyecto/{id}            → Actualizar
 * - PUT    /api/t/proyecto/{id}/vigencia   → Cambiar vigencia
 */

@RestController
@RequestMapping("/api/t/proyecto")
@CrossOrigin(origins = "*")
public class T_ProyectoController {
    private static final Logger logger = LoggerFactory.getLogger(T_ProyectoController.class);
    private final T_ProyectoService proyectoService;

    public T_ProyectoController(T_ProyectoService proyectoService) {
        this.proyectoService = proyectoService;
    }

    /*
     * Lista todos los proyectos con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<T_Proyecto>> listarTodos(
            @PageableDefault(size = 10, sort = "nomPyto", direction = Sort.Direction.ASC) 
            Pageable pageable
    ) {
        logger.debug("GET /api/t/proyecto - Listando proyectos");
        
        Page<T_Proyecto> proyectos = proyectoService.listarTodos(pageable);
        
        return ResponseEntity.ok(proyectos);
    }

    /*
     * Lista solo proyectos activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<Page<T_Proyecto>> listarActivos(
            @PageableDefault(size = 10) Pageable pageable
    ) {
        logger.debug("GET /api/t/proyecto/activos - Listando proyectos activos");
        
        Page<T_Proyecto> proyectos = proyectoService.listarActivos(pageable);
        
        return ResponseEntity.ok(proyectos);
    }

    /*
     * Busca un proyecto por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<T_Proyecto> buscarPorId(@PathVariable Long id) {
        logger.debug("GET /api/t/proyecto/{} - Buscando proyecto", id);
        
        T_Proyecto proyecto = proyectoService.buscarPorId(id);
        
        return ResponseEntity.ok(proyecto);
    }

    /*
     * Crea un nuevo proyecto.
     */
    /*@PostMapping
    public ResponseEntity<RespuestaExito<T_Proyecto>> guardar(
            @Valid @RequestBody T_Proyecto proyecto
    ) {
        logger.info("POST /api/t/proyecto - Creando nuevo proyecto");
        
        T_Proyecto nuevoProyecto = proyectoService.guardar(proyecto);
        
        RespuestaExito<T_Proyecto> respuesta = new RespuestaExito<>(
            "Proyecto creado exitosamente",
            nuevoProyecto
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }*/

    /*
     * Actualiza un proyecto existente.
     */
    /*@PutMapping("/{id}")
    public ResponseEntity<RespuestaExito<T_Proyecto>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody T_Proyecto proyecto
    ) {
        logger.info("PUT /api/t/proyecto/{} - Actualizando proyecto", id);
        
        T_Proyecto proyectoActualizado = proyectoService.actualizar(id, proyecto);
        
        RespuestaExito<T_Proyecto> respuesta = new RespuestaExito<>(
            "Proyecto actualizado exitosamente",
            proyectoActualizado
        );
        
        return ResponseEntity.ok(respuesta);
    }*/

    /*
     * Cambia la vigencia de un proyecto.
     */
    /*@PutMapping("/{id}/vigencia")
    public ResponseEntity<RespuestaExito<T_Proyecto>> cambiarVigencia(
            @PathVariable Long id,
            @RequestBody CambiarVigenciaRequest request
    ) {
        logger.info("PUT /api/t/proyecto/{}/vigencia - Cambiando vigencia", id);
        
        T_Proyecto proyectoActualizado = proyectoService.cambiarVigencia(id, request.activar());
        
        String mensaje = request.activar() 
            ? "Proyecto activado" 
            : "Proyecto desactivado";
        
        RespuestaExito<T_Proyecto> respuesta = new RespuestaExito<>(
            mensaje,
            proyectoActualizado
        );
        
        return ResponseEntity.ok(respuesta);
    }*/
}