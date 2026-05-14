package com.syscanvas.syscanvas.model.T;

import java.util.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_PYTOPERS", schema = "SYSCANVAS") 
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@IdClass(T_PytoPersId.class)
public class T_PytoPers {
    @Id
    @Column(name = "CODPYTO", nullable = false)
    private Long codPyto;
    @Id
    @Column(name = "CORREMPL", nullable = false)
    private Long corrEmpl;
    @Column(name = "FLGEMPLJEF", length = 1)
    private String flgEmplJef;
    @Column(name = "VIGENTE", length = 1)
    private Integer vigente;
    @Column(name = "CODPERS", nullable = false)
    private Long codPers;


    @ManyToOne
    @JoinColumn(name = "CODPYTO", insertable = false, updatable = false)
    private T_Proyecto proyecto;
    @ManyToOne
    @JoinColumn(name = "CODPERS", insertable = false, updatable = false)
    private T_Empleado empleado;

    @Column(name = "CODCARGO")
    private Long codCargo;
    @Temporal(TemporalType.DATE)
    @Column(name = "FECINI")
    private Date fecIni;
    @Temporal(TemporalType.DATE)
    @Column(name = "FECFIN")
    private Date fecFin;
    @Column(name = "COSTO")
    private Double costo;
    @Column(name = "DESQTRAB")
    private String desQtrab;
    @Column(name = "OBSERVAC")
    private String observac;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "CELULAR")
    private Long celular;
    @Column(name = "FLGPYEMPIE")
    private String flgPyEmpie;
}
