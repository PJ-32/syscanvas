package com.syscanvas.syscanvas.model.SC;

import jakarta.persistence.*;
import java.util.*;
import com.fasterxml.jackson.annotation.*;
import lombok.*;

@Entity
@Table(name = "SC_TIPO_CANVAS")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Tipo_Canvas {
    @Id
    @Column(name = "TIPCANVAS", length = 1)
    private String tipCanvas;
    
    @Column(name = "DESTIPCANVAS", length = 200)
    private String desTipCanvas;
    
    @Column(name = "VIGENTE", length = 1, nullable = false)
    private Integer vigente;

    @JsonIgnore
    @OneToMany(mappedBy = "tipoCanvas", cascade = CascadeType.ALL)
    private List<SC_Canvas> canvas;
}