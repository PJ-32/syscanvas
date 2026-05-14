package com.syscanvas.syscanvas.dto.SC;

import lombok.*;
import java.time.*;
import jakarta.persistence.*;
import jakarta.validation.*;
import jakarta.validation.constraints.*;
import java.util.List;
import com.syscanvas.syscanvas.model.SC.SC_Etapa;

/*
 * DTO para Canvas con validaciones Jakarta completas.
 */

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SC_CanvasDTO {
    /*
     * Código del canvas (solo lectura en creación).
     */
    private Long codCanvas;
    
    /*
     * Nombre del canvas.
     */
    @NotBlank(message = "El nombre del canvas es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nomCanvas;
    
    /*
     * Descripción detallada del canvas.
     */
    @Size(max = 1000, message = "La descripción no puede exceder 1000 caracteres")
    private String desCanvas;
    
    /*
     * Fecha de creación (generada automáticamente).
     */
    private LocalDateTime fecCreacion;
    
    /*
     * Fecha de última modificación (actualizada automáticamente).
     */
    private LocalDateTime fecModificacion;
    
    /*
     * Indica si el canvas está editable para analistas.
     */
    private Boolean editable;

    /*
     * Proyecto al que pertenece el canvas.
     */
    //@NotNull(message = "El proyecto es obligatorio")  //ya no necesario
    @Valid // ← Valida también los campos dentro de T_ProyectoDTO
    private Long codPyto;
    //private T_ProyectoDTO proyecto;
    
    /*
     * Persona creadora del canvas (Jefe de Proyecto).
     */
    @Valid
    private Long codPersona;
    //private T_PersonaDTO persona; 
    
    /*
     * Estado actual del canvas (Activo, Cerrado, etc.).
     */
    @Valid
    private SC_EstadoDTO estado;
    
    /*
     * Tipo de canvas (Plantilla o Libre).
     */
    @NotNull(message = "El tipo de canvas es obligatorio")
    @Valid
    private SC_Tipo_CanvasDTO tipoCanvas;
    
    // ========== ESTADÍSTICAS CALCULADAS (Solo lectura) ==========
    
    /*
     * Total de tareas en todas las etapas.
     * Calculado dinámicamente por el servicio.
     */
    @Min(value = 0, message = "El total de tareas no puede ser negativo")
    private Integer totalTareas;
    
    /*
     * Tareas completadas (vigencia = 0).
     * Calculado dinámicamente por el servicio.
     */
    @Min(value = 0, message = "Las tareas completadas no pueden ser negativas")
    private Integer tareasCompletadas;
    
    /*
     * Porcentaje de progreso (0-100).
     * Calculado dinámicamente por el servicio.
     */
    @DecimalMin(value = "0.0", message = "El porcentaje no puede ser negativo")
    @DecimalMax(value = "100.0", message = "El porcentaje no puede exceder 100")
    private Double porcentajeProgreso;
    
    /*
     * Total de comentarios en el canvas.
     * Calculado dinámicamente por el servicio.
     */
    @Min(value = 0, message = "El total de comentarios no puede ser negativo")
    private Integer totalComentarios;

    private List<SC_EtapaDTO> etapas;
    private List<SC_Etapa> etapasPersonalizadas;
    @Transient
    private Object empleadoInfo;

    @Transient
    private Object proyectoInfo;
}
