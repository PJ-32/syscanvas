package com.syscanvas.syscanvas.model.T;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_TIPO_PERSONA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_Tipo_Persona {
    @Id
    @Column(name = "TIPPERSONA", length = 1)
    private String tipPersona;
    
    @Column(name = "DENTIPPERSONA", length = 30)
    private String denTipPersona;
    
    @Column(name = "VIGENTE", length = 1)
    private Integer vigente;
}
