package com.syscanvas.syscanvas.dto.T;

import java.util.*;
import com.syscanvas.syscanvas.dto.SC.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_EmpleadoDTO {
    private Long codPersona;
    //private T_PersonaDTO persona; no sé si va
    private SC_CargoDTO cargo;
    private String direccion;
    private String hobby;
    private Date fecNac;
    private String dni;
    private String nombre;
    private String apellido;
    private String email;
    private String celular;
    private Integer vigente;
    private String fotoBase64;
    
}