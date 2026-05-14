package com.syscanvas.syscanvas.model.T;

import java.math.*;
import java.sql.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_PROYECTO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_Proyecto {
    @Id
    @Column(name = "CODPYTO", length = 6)
    private Long codPyto;
    @Column(name = "NOMPYTO", length = 1000)
    private String nomPyto;
    @Column(name = "VIGENTE", length = 1)
    private Integer vigente;

    @ManyToOne
    @JoinColumn(name = "EMPLJEFEPROJ", insertable = false, updatable = false)
    private T_Empleado jefeProyecto;

    @Column(name = "CODSNIP")
    private String codSnip;
    @Column(name = "FECREG")
    private Date fecReg;
    @Column(name = "CODFASE")
    private Integer codFase;
    @Column(name = "CODNIVEL")
    private Integer codNivel;
    @Column(name = "CODFUNCION")
    private String codFuncion;
    @Column(name = "CODTAXONOMIA")
    private Integer codTaxonomia;
    @Column(name = "CODSITUACION")
    private Integer codSituacion;
    @Column(name = "NUMINFOR")
    private Integer numInfor;
    @Column(name = "NUMINFORENTRG")
    private Integer numInforEntrg;
    @Column(name = "ESTPYTO")
    private Integer estPyto;
    @Column(name = "FECESTADO")
    private Date fecEstado;
    @Column(name = "CODCONSORCIO")
    private Long codConsorcio;
    @Column(name = "CODCLIENTE")
    private Long codCliente;
    @Column(name = "COSTOTOTAL")
    private BigDecimal costoTotal;
    @Column(name = "COSTODIRECTO")
    private BigDecimal costoDirecto;
    @Column(name = "COSTOGGEN")
    private BigDecimal costoGgen;
    @Column(name = "COSTOIMP")
    private BigDecimal costoImp;
    @Column(name = "COSTOPENALID")
    private BigDecimal costoPenalid;
    @Column(name = "CODDPTO")
    private String codDpto;
    @Column(name = "CODPROV")
    private String codProv;
    @Column(name = "CODDIST")
    private String codDist;
    @Column(name = "FECVIAB")
    private Date fecViab;
    @Column(name = "OBSERVAC")
    private String observac;
    @Column(name = "RUTADOC")
    private String rutaDoc;
    @Column(name = "CODOBJC")
    private Integer codObjc;
    @Lob
    @Column(name = "LOGOPROY")
    private Blob logoProy;
    @Column(name = "ANNOINI")
    private Integer annoIni;
    @Column(name = "ANNOFIN")
    private Integer annoFin;
}
