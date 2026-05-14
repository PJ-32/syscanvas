package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sc/estado")
@CrossOrigin(origins = "*")
public class SC_EstadoController extends BaseController {
    private final SC_EstadoService estadoService;

    public SC_EstadoController(SC_EstadoService estadoService) {
        this.estadoService = estadoService;
    }

    @GetMapping
    public List<SC_Estado> listarTodos() {
        return estadoService.listarTodos();
    }
    @PostMapping
    public SC_Estado guardar(@RequestBody SC_Estado estado) {
        return estadoService.guardar(estado);
    }
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable("id") Integer codEstado) {
        estadoService.eliminar(codEstado);
    }
}