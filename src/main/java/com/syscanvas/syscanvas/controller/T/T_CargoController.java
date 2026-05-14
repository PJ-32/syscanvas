package com.syscanvas.syscanvas.controller.T;

import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/t/cargo")
@CrossOrigin(origins = "*")
public class T_CargoController {
    private final SC_CargoService cargoService;

    public T_CargoController(SC_CargoService cargoService) {
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