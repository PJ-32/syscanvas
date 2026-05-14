package com.syscanvas.syscanvas.dto.SC;

import java.time.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_HistorialDTO {
    private Long codHistorial;
    private String accion;
    private LocalDateTime fecAccion;
    private String detalle;
    private Long codPersona;
    //private T_PersonaDTO persona; 
    private SC_CanvasDTO canvas;
}