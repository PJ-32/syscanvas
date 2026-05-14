package com.syscanvas.syscanvas.model.SC;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SC_HISTORIAL")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Historial {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SC_HISTORIAL_SEQ")
    @SequenceGenerator(
        name = "SC_HISTORIAL_SEQ",
        sequenceName = "SC_HISTORIAL_SEQ",
        allocationSize = 1
    )
    @Column(name = "CODHISTORIAL")
    private Long codHistorial;
    
    @Column(name = "ACCION", length = 200)
    private String accion;
    
    @Column(name = "FECACCION", nullable = false)
    private LocalDateTime fecAccion;
    
    @Column(name = "DETALLE", length = 1000)
    private String detalle;

    @Column(name = "CODPERSONA", nullable = false)
    private Long codPersona; 
    
    @ManyToOne
    @JoinColumn(name = "CODCANVAS")
    private SC_Canvas canvas;

    @Transient
    private Object personaInfo;
}
