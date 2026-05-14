package com.syscanvas.syscanvas.dto.SC;

import java.time.*;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_ComentarioDTO {
    private Long codComentario;
    
    @NotBlank(message = "El contenido del comentario es obligatorio")
    @Size(min = 2, max = 1000, message = "El comentario debe tener entre 2 y 1000 caracteres")
    private String contenido;
    
    @PastOrPresent(message = "La fecha del comentario no puede ser futura")
    private LocalDateTime fecComentario;
    
    @NotNull(message = "Debe especificarse el canvas del comentario")
    @Valid
    private SC_CanvasDTO canvas;
    
    @NotNull(message = "Debe especificarse la persona que realizó el comentario")
    @Valid
    private Long codPersona;
    
    @Transient
    private Object empleadoInfo;

    //private T_PersonaDTO persona;
}
