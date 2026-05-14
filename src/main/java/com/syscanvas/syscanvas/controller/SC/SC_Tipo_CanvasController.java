package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sc/tipo-canvas")
@CrossOrigin(origins = "*")
public class SC_Tipo_CanvasController extends BaseController {
    private final SC_Tipo_CanvasService tipoCanvasService;

    public SC_Tipo_CanvasController(SC_Tipo_CanvasService tipoCanvasService) {
        this.tipoCanvasService = tipoCanvasService;
    }

    @GetMapping
    public List<SC_Tipo_Canvas> listarTodos() {
        return tipoCanvasService.listarTodos();
    }
}
