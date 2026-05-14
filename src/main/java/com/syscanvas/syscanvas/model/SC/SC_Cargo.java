package com.syscanvas.syscanvas.model.SC;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "SC_CARGO")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SC_Cargo {
    @Id
    @Column(name = "CODCARGO", length = 4)
    private Long codCargo;
    
    @Column(name = "NOMCARGO", length = 100, nullable = false)
    private String nomCargo;
    
    @Column(name = "DESCARGO", length = 200)
    private String desCargo;
    
    @Column(name = "VIGENTE", length = 1, nullable = false)
    private Integer vigente;
}
