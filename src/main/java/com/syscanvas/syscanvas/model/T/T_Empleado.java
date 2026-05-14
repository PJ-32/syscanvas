package com.syscanvas.syscanvas.model.T;

import java.util.Date;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "T_EMPLEADO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class T_Empleado {
    @Id
    @Column(name = "CODPERS", nullable = false)
    private Long codPersona;
    
    @OneToOne
    @JoinColumn(name = "CODPERS", referencedColumnName = "CODPERSONA", insertable = false, updatable = false)
    private T_Persona persona;
    
    //@JoinColumn(name = "CODCARGO")
    @Column(name = "CODCARGO")
    private Long codCargo;
    
    @Column(name = "DIRECC", length = 100)
    private String direccion;
    
    @Column(name = "HOBBY", length = 2000)
    private String hobby;
    
    @Temporal(TemporalType.DATE)
    @Column(name = "FECNAC", nullable = false)
    private Date fecNac;
    
    @Column(name = "DNI", length = 20, nullable = false)
    private String dni;
    
    @Column(name = "NOMBRE", length = 100, nullable = false)
    private String nombre;
    
    @Column(name = "APELLIDO", length = 100, nullable = false)
    private String apellido;
    
    @Column(name = "EMAIL", length = 100)
    private String email;
    
    @Column(name = "CELULAR", length = 10)
    private String celular;
    
    @Column(name = "VIGENTE", length = 1)
    private Integer vigente;

    @Column(name = "PASSWORD", length = 255)
    private String password;

    @Lob
    @Column(name = "FOTO")
    private byte[] foto;


}
