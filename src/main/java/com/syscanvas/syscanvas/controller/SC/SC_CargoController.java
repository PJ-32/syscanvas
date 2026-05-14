package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/sc/cargo")
@CrossOrigin(origins = "*")
public class SC_CargoController extends BaseController {
    private final SC_CargoService cargoService;

    public SC_CargoController(SC_CargoService cargoService) {
        this.cargoService = cargoService;
    }

    @GetMapping
    public List<SC_Cargo> listarTodos() {
        return cargoService.listarTodos();
    }

    @PostMapping
    public SC_Cargo guardar(@RequestBody SC_Cargo cargo) {
        return cargoService.guardar(cargo);
    }
}
