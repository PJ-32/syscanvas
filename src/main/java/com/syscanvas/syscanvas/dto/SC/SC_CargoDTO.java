package com.syscanvas.syscanvas.dto.SC;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_CargoDTO {
    private Long codCargo;
    private String desCargo;
    private Integer vigente;
}
