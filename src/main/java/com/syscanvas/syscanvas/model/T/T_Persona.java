package com.syscanvas.syscanvas.model.T;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_PERSONA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_Persona {
    @Id
    @Column(name = "CODPERSONA", length = 6)
    private Long codPersona;
    
    @Column(name = "DESPERSONA", length = 100)
    private String desPersona;
    
    @Column(name = "DESCORTA", length = 30)
    private String desCorta;

    @ManyToOne
    @JoinColumn(name = "TIPPERSONA", referencedColumnName = "TIPPERSONA", nullable = false)
    private T_Tipo_Persona tipoPersona;
}
