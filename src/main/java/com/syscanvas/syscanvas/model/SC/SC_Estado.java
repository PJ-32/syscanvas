package com.syscanvas.syscanvas.model.SC;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.*;
import com.fasterxml.jackson.annotation.*;

@Entity
@Table(name = "SC_ESTADO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Estado {
    @Id
    @Column(name = "CODESTADO", length = 1)
    private Integer codEstado;
    
    @Column(name = "NOMESTADO", nullable = false, length = 20)
    private String nomEstado;
    
    @Column(name = "DESESTADO", length = 200)
    private String desEstado;
    
    @Column(name = "FECMODIFICACION", nullable = false)
    private LocalDateTime fecModificacion;
    
    @Column(name = "VIGENTE", length = 1, nullable = false)
    private Integer vigente;
    
    @JsonIgnore
    @OneToMany(mappedBy = "estado")
    private List<SC_Canvas> canvas;
}
