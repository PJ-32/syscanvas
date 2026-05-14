package com.syscanvas.syscanvas.model.SC;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "SC_ETAPA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Etapa {
    
    @Id
    @SequenceGenerator(name = "SC_ETAPA_SEQ", sequenceName = "SC_ETAPA_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SC_ETAPA_SEQ")
    @Column(name = "CODETAPA")
    private Long codEtapa;

    
    @Column(name = "NOMETAPA", nullable = false, length = 100)
    private String nomEtapa; //Nombre de etapa
    
    @Column(name = "DESETAPA", length = 500)
    private String desEtapa; //Descripción de etapa
    
    @Column(name = "FECCREACION", nullable = false)
    private LocalDateTime fecCreacion;
    
    @Column(name = "FECMODIFICACION", nullable = false)
    private LocalDateTime fecModificacion;
    
    @Column(name = "NUMETAPA", nullable = false)
    private Integer numEtapa; //Orden de etapa
    
    @Column(name = "VIGENTE", nullable = false)
    private Integer vigente;

    @ManyToOne
    @JoinColumn(name = "CODCANVAS")
    @JsonBackReference
    private SC_Canvas canvas;

    @OneToMany(mappedBy = "etapa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<SC_Tarea> tareas;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.fecCreacion = now;
        this.fecModificacion = now;
        if (this.vigente == null) this.vigente = 1;
    }

    @PreUpdate
    public void preUpdate() {
        this.fecModificacion = LocalDateTime.now();
    }
}
