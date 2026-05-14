package com.syscanvas.syscanvas.model.SC;

import jakarta.persistence.*;
import java.time.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import lombok.*;

@Entity
@Table(name = "SC_TAREA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Tarea {
@Id
@SequenceGenerator(
    name = "SC_TAREA_SEQ",
    sequenceName = "SC_TAREA_SEQ",
    allocationSize = 1
)
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SC_TAREA_SEQ")
@Column(name = "CODTAREA")
private Long codTarea;
    
    @Column(name = "NOMTAREA", nullable = false, length = 200)
    private String nomTarea;
    
    @Column(name = "DESTAREA", length = 500)
    private String desTarea;
    
    @Column(name = "FECCREACION", nullable = false)
    private LocalDateTime fecCreacion;
    
    @Column(name = "FECMODIFICACION", nullable = false)
    private LocalDateTime fecModificacion;
    
    @Column(name = "NUMTAREA", nullable = false)
    private Long numTarea;
    
    @Column(name = "VIGENTE", length = 1, nullable = false)
    private Long vigente;

    @Column(name = "ESTADO", length = 50)
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CODETAPA")
    @JsonBackReference
    private SC_Etapa etapa;

    
    @Column(name = "CODPERSONA", nullable = false)
    private Long codPersona; 

    @Transient
    private Object empleadoInfo;
}