package com.syscanvas.syscanvas.model.SC;

import java.time.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SC_COMENTARIO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Comentario {
    @Id
    @Column(name = "CODCOMENTARIO", length = 6)
    private Long codComentario;
    
    @Column(name = "CONTENIDO", length = 2000)
    private String contenido;
    
    @Column(name = "FECCOMENTARIO", nullable = false)
    private LocalDateTime fecComentario;

    @ManyToOne
    @JoinColumn(name = "CODCANVAS")
    private SC_Canvas canvas;
    
    @Column(name = "CODPERSONA", nullable = false)
    private Long codPersona;

    @Transient
    private Object empleadoInfo;
}
