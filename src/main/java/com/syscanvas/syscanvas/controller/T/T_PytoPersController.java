package com.syscanvas.syscanvas.controller.T;

import com.syscanvas.syscanvas.model.T.T_PytoPers;
import com.syscanvas.syscanvas.service.T.T_PytoPersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/t/pytopers")
@CrossOrigin(origins = "*")
public class T_PytoPersController {

    private static final Logger logger = LoggerFactory.getLogger(T_PytoPersController.class);

    private final T_PytoPersService pytoPersService;

    public T_PytoPersController(T_PytoPersService pytoPersService) {
        this.pytoPersService = pytoPersService;
    }

    @GetMapping
    public ResponseEntity<Page<T_PytoPers>> listarTodos(
            @RequestParam(required = false) Long codPyto,
            @PageableDefault(size = 100) Pageable pageable
    ) {
        logger.debug("GET /api/t/pytopers - codPyto: {}", codPyto);

        if (codPyto != null) {
            Page<T_PytoPers> page = pytoPersService.buscarPorProyecto(codPyto, pageable);
            return ResponseEntity.ok(page);
        }

        Page<T_PytoPers> page = pytoPersService.listarTodos(pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/proyecto/{codPyto}")
    public ResponseEntity<List<T_PytoPers>> buscarPorProyecto(@PathVariable Long codPyto) {
        logger.debug("GET /api/t/pytopers/proyecto/{}", codPyto);

        List<T_PytoPers> asignaciones = pytoPersService.buscarPorProyecto(codPyto);

        return ResponseEntity.ok(asignaciones);
    }

    @GetMapping("/empleado/{codPersona}")
    public ResponseEntity<List<T_PytoPers>> buscarPorEmpleado(@PathVariable Long codPersona) {
        logger.debug("GET /api/t/pytopers/empleado/{}", codPersona);

        List<T_PytoPers> asignaciones = pytoPersService.buscarPorEmpleado(codPersona);

        return ResponseEntity.ok(asignaciones);
    }
}
