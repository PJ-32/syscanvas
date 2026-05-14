package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sc/comentario")
@CrossOrigin(origins = "*")
public class SC_ComentarioController extends BaseController {
    private final SC_ComentarioService comentarioService;

    public SC_ComentarioController(SC_ComentarioService comentarioService) {
        this.comentarioService = comentarioService;
    }

    @GetMapping
    public List<SC_Comentario> listarTodos() {
        return comentarioService.listarTodos();
    }

    @PostMapping
    public SC_Comentario guardar(@RequestBody SC_Comentario comentario) {
        return comentarioService.guardar(comentario);
    }
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable("id") Long codComentario) {
        comentarioService.eliminar(codComentario);
    }
}
