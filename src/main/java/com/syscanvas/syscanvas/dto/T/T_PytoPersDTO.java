package com.syscanvas.syscanvas.dto.T;

import java.time.*;
import com.syscanvas.syscanvas.dto.SC.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_PytoPersDTO {
    private T_PytoPersIdDTO id;
    private T_ProyectoDTO proyecto;
    private T_PersonaDTO persona;
    private SC_CargoDTO cargo;
    private LocalDate fecIni;
    private LocalDate fecFin;
    private Double costo;
    private Integer vigente;
}
