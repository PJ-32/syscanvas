package com.syscanvas.syscanvas.controller.T;

import com.syscanvas.syscanvas.model.T.*;
import com.syscanvas.syscanvas.service.T.*;
import org.springframework.data.domain.*;
import org.springframework.data.web.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import com.syscanvas.syscanvas.dto.request.FotoRequest;



/**
 * Controlador REST para gestión de Empleados.
 * 
 * Endpoints:
 * - GET    /api/t/empleado                 → Lista con paginación
 * - GET    /api/t/empleado/activos         → Lista solo activos
 * - GET    /api/t/empleado/{id}            → Buscar por ID
 * - GET    /api/t/empleado/dni/{dni}       → Buscar por DNI
 * - POST   /api/t/empleado                 → Crear
 * - PUT    /api/t/empleado/{id}            → Actualizar
 * - PUT    /api/t/empleado/{id}/vigencia   → Cambiar vigencia
 */

@RestController
@RequestMapping("/api/t/empleado")
@CrossOrigin(origins = "*")
public class T_EmpleadoController {
    private static final Logger logger = LoggerFactory.getLogger(T_EmpleadoController.class);
    private final T_EmpleadoService empleadoService;

    public T_EmpleadoController(T_EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }

    /*
     * Lista todos los empleados con paginación.
     */
    @GetMapping
    public ResponseEntity<Page<T_Empleado>> listarTodos(
            @PageableDefault(size = 20, sort = "apellido", direction = Sort.Direction.ASC) 
            Pageable pageable
    ) {
        logger.debug("GET /api/t/empleado - Listando empleados");
        
        Page<T_Empleado> empleados = empleadoService.listarTodos(pageable);
        
        return ResponseEntity.ok(empleados);
    }

    /*
     * Lista solo empleados activos.
     */
    @GetMapping("/activos")
    public ResponseEntity<Page<T_Empleado>> listarActivos(
            @PageableDefault(size = 20) Pageable pageable
    ) {
        logger.debug("GET /api/t/empleado/activos - Listando empleados activos");
        
        Page<T_Empleado> empleados = empleadoService.listarActivos(pageable);
        
        return ResponseEntity.ok(empleados);
    }

    /*
     * Busca un empleado por ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<T_Empleado> buscarPorId(@PathVariable Long id) {
        logger.debug("GET /api/t/empleado/{} - Buscando empleado", id);
        
        T_Empleado empleado = empleadoService.buscarPorId(id);
        
        return ResponseEntity.ok(empleado);
    }

    /*
     * Buscar Foto
     */
    @GetMapping("/{id}/foto")
    public ResponseEntity<Map<String, String>> obtenerFoto(@PathVariable Long id) {

        T_Empleado empleado = empleadoService.buscarPorId(id);
        byte[] foto = empleado.getFoto();

        Map<String, String> respuesta = new HashMap<>();

        if (foto == null || foto.length == 0) {
            respuesta.put("fotoBase64", null);
            return ResponseEntity.ok(respuesta);
        }

        respuesta.put("fotoBase64", Base64.getEncoder().encodeToString(foto));
        return ResponseEntity.ok(respuesta);
    }


    


    /*
     * Busca un empleado por DNI.
     */
    @GetMapping("/dni/{dni}")
    public ResponseEntity<T_Empleado> buscarPorDni(@PathVariable String dni) {
        logger.debug("GET /api/t/empleado/dni/{} - Buscando empleado", dni);
        
        T_Empleado empleado = empleadoService.buscarPorDni(dni)
                .orElseThrow(() -> new com.syscanvas.syscanvas.exception.ExcepcionRecursoNoEncontrado(
                    "Empleado", "dni", dni
                ));
        
        return ResponseEntity.ok(empleado);
    }

    /*
     * Crea un nuevo empleado.
     */
    /*@PostMapping
    public ResponseEntity<RespuestaExito<T_Empleado>> guardar(
            @Valid @RequestBody T_Empleado empleado
    ) {
        logger.info("POST /api/t/empleado - Creando nuevo empleado");
        
        T_Empleado nuevoEmpleado = empleadoService.guardar(empleado);
        
        RespuestaExito<T_Empleado> respuesta = new RespuestaExito<>(
            "Empleado creado exitosamente",
            nuevoEmpleado
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }*/

    /*
     * Actualiza un empleado existente.
     */
    @PutMapping("/{id}")
    public ResponseEntity<T_Empleado> actualizar(
            @PathVariable Long id,
            @RequestBody T_Empleado empleado
    ) {
        logger.info("PUT /api/t/empleado/{} - Actualizando empleado", id);

        T_Empleado actualizado = empleadoService.actualizar(id, empleado);

        return ResponseEntity.ok(actualizado);
    }


    /*
     * Cambia la vigencia de un empleado.
     */
    /*@PutMapping("/{id}/vigencia")
    public ResponseEntity<RespuestaExito<T_Empleado>> cambiarVigencia(
            @PathVariable Long id,
            @RequestBody CambiarVigenciaRequest request
    ) {
        logger.info("PUT /api/t/empleado/{}/vigencia - Cambiando vigencia", id);
        
        T_Empleado empleadoActualizado = empleadoService.cambiarVigencia(id, request.activar());
        
        String mensaje = request.activar() 
            ? "Empleado activado" 
            : "Empleado desactivado";
        
        RespuestaExito<T_Empleado> respuesta = new RespuestaExito<>(
            mensaje,
            empleadoActualizado
        );
        
        return ResponseEntity.ok(respuesta);
    }*/

    @PutMapping("/{id}/foto")
    public ResponseEntity<Void> actualizarFoto(
            @PathVariable Long id,
            @RequestBody FotoRequest request
    ) {
        logger.info("PUT /api/t/empleado/{}/foto - Actualizando foto", id);

        empleadoService.actualizarFoto(id, request.getFotoBase64());
        return ResponseEntity.noContent().build();
    }


    /*@GetMapping("/{codPersona}/foto")
    public ResponseEntity<byte[]> obtenerFoto(@PathVariable Long codPersona) {
        byte[] foto = empleadoService.obtenerFoto(codPersona);

        if (foto == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG); // PNG o JPEG
        return new ResponseEntity<>(foto, headers, HttpStatus.OK);
    }*/

    @DeleteMapping("/{id}/foto")
    public ResponseEntity<?> eliminarFoto(@PathVariable Long id) {
        empleadoService.eliminarFoto(id);
        return ResponseEntity.ok().build();
    }




}