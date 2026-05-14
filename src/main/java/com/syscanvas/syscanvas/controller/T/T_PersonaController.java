package com.syscanvas.syscanvas.controller.T;

//import com.syscanvas.syscanvas.dto.response.*;
import com.syscanvas.syscanvas.model.T.*;
import com.syscanvas.syscanvas.service.T.*;
//import jakarta.validation.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.slf4j.*;

/**
 * Controlador REST para gestión de Personas.
 * 
 * Nota: T_Persona es la entidad base. Para operaciones específicas
 * de Empleados o Clientes, usar sus controladores dedicados.
 * 
 * Endpoints:
 * - GET    /api/t/persona           → Lista con paginación
 * - GET    /api/t/persona/{id}      → Buscar por ID
 * - POST   /api/t/persona           → Crear
 * - PUT    /api/t/persona/{id}      → Actualizar
 */

@RestController
@RequestMapping("/api/t/persona")
@CrossOrigin(origins = "*")
public class T_PersonaController {
    private static final Logger logger = LoggerFactory.getLogger(T_PersonaController.class);
    private final T_PersonaService personaService;

    public T_PersonaController(T_PersonaService personaService) {
        this.personaService = personaService;
    }

    /*
     * Lista todas las personas con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<T_Persona>> listarTodos(
            @PageableDefault(size = 20, sort = "desPersona", direction = Sort.Direction.ASC) 
            Pageable pageable
    ) {
        logger.debug("GET /api/t/persona - Listando personas");
        
        Page<T_Persona> personas = personaService.listarTodos(pageable);
        
        return ResponseEntity.ok(personas);
    }

    /*
     * Busca una persona por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<T_Persona> buscarPorId(@PathVariable Long id) {
        logger.debug("GET /api/t/persona/{} - Buscando persona", id);
        
        T_Persona persona = personaService.buscarPorId(id);
        
        return ResponseEntity.ok(persona);
    }

    /*
     * Crea una nueva persona.
     */
    /*@PostMapping
    public ResponseEntity<RespuestaExito<T_Persona>> guardar(
            @Valid @RequestBody T_Persona persona
    ) {
        logger.info("POST /api/t/persona - Creando nueva persona");
        
        T_Persona nuevaPersona = personaService.guardar(persona);
        
        RespuestaExito<T_Persona> respuesta = new RespuestaExito<>(
            "Persona creada exitosamente",
            nuevaPersona
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }*/

    /*
     * Actualiza una persona existente.
     */
    /*@PutMapping("/{id}")
    public ResponseEntity<RespuestaExito<T_Persona>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody T_Persona persona
    ) {
        logger.info("PUT /api/t/persona/{} - Actualizando persona", id);
        
        T_Persona personaActualizada = personaService.actualizar(id, persona);
        
        RespuestaExito<T_Persona> respuesta = new RespuestaExito<>(
            "Persona actualizada exitosamente",
            personaActualizada
        );
        
        return ResponseEntity.ok(respuesta);
    }*/
}
