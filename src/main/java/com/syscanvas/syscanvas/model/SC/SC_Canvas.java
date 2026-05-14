package com.syscanvas.syscanvas.model.SC;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.*;
import com.fasterxml.jackson.annotation.*;

@Entity
@Table(name = "SC_CANVAS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Canvas {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_canvas")
    @SequenceGenerator(name = "seq_canvas", sequenceName = "SC_CANVAS_SEQ", allocationSize = 1)
    @Column(name = "CODCANVAS", length = 6)
    private Long codCanvas;
    
    @Column(name = "NOMCANVAS", nullable = false, length = 100)
    private String nomCanvas;
    
    @Column(name = "DESCANVAS", length = 500)
    private String desCanvas;
    
    @Column(name = "FECCREACION", nullable = false)
    private LocalDateTime fecCreacion;
    
    @Column(name = "FECMODIFICACION", nullable = false)
    private LocalDateTime fecModificacion;
    
    // 0: Bloqueado, 1: Editable
    @Column(name = "EDITABLE", nullable = false)
    private Boolean editable = false;

    @Column(name = "CODPYTO") //puede ser null para los canvas sin proyectos
    private Long codPyto;
    
    @Column(name = "CODPERSONA", nullable = false)
    private Long codPersona; 
    
    @ManyToOne
    @JoinColumn(name = "CODESTADO", nullable = false)
    private SC_Estado estado;
    
    @ManyToOne
    @JoinColumn(name = "TIPCANVAS", referencedColumnName = "TIPCANVAS", nullable = false)
    private SC_Tipo_Canvas tipoCanvas;

    @OneToMany(mappedBy = "canvas", fetch = FetchType.LAZY)
    private Set<SC_Etapa> etapas = new HashSet<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "canvas", cascade = CascadeType.ALL)
    private List<SC_Comentario> comentarios;
    
    @JsonIgnore
    @OneToMany(mappedBy = "canvas", cascade = CascadeType.ALL)
    private List<SC_Historial> historiales;

    @Transient
    private Object proyectoInfo;
    
    @Transient
    private Object empleadoInfo;
}
