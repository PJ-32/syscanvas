package com.syscanvas.syscanvas.dto.SC;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_Tipo_CanvasDTO {
    private Long codTipoCanvas;
    private String tipCanvas;
    private String desTipCanvas;
    private Integer vigente;
}