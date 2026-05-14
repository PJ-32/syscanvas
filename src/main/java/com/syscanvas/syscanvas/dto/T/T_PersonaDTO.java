package com.syscanvas.syscanvas.dto.T;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_PersonaDTO {
    private Long codPersona;
    private T_Tipo_PersonaDTO tipoPersona;
    private String desPersona;
    private String desCorta;
}
