package com.syscanvas.syscanvas.controller.SC;

import com.syscanvas.syscanvas.controller.BaseController;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.service.SC.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sc/historial")
@CrossOrigin(origins = "*")
public class SC_HistorialController extends BaseController {
    private final SC_HistorialService historialService;

    public SC_HistorialController(SC_HistorialService historialService) {
        this.historialService = historialService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarTodos(
            @RequestParam(required = false) Long codCanvas
    ) {
        List<SC_Historial> historial;
        
        if (codCanvas != null) {
            // Buscar por canvas específico
            SC_Canvas canvas = new SC_Canvas();
            canvas.setCodCanvas(codCanvas);
            historial = historialService.buscarPorCanvas(canvas);
        } else {
            historial = historialService.listarTodos();
        }
        
        // Mapear manualmente para evitar lazy loading
        List<Map<String, Object>> resultado = historial.stream()
            .map(h -> {
                Map<String, Object> map = new HashMap<>();
                map.put("codHistorial", h.getCodHistorial());
                map.put("accion", h.getAccion());
                map.put("fecAccion", h.getFecAccion());
                map.put("detalle", h.getDetalle());
                map.put("codPersona", h.getCodPersona());
                
                // Solo incluir codCanvas, NO el objeto completo
                if (h.getCanvas() != null) {
                    map.put("codCanvas", h.getCanvas().getCodCanvas());
                    map.put("nomCanvas", h.getCanvas().getNomCanvas());
                }
                
                return map;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(resultado);
    }

    @PostMapping
    public SC_Historial guardar(@RequestBody SC_Historial historial) {
        return historialService.guardar(historial);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable("id") Long codHistorial) {
        historialService.eliminar(codHistorial);
    }
}