package com.syscanvas.syscanvas.mapper.SC;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import com.syscanvas.syscanvas.dto.SC.SC_CanvasDTO;
import com.syscanvas.syscanvas.dto.SC.SC_EstadoDTO;
import com.syscanvas.syscanvas.dto.SC.SC_Tipo_CanvasDTO;
import com.syscanvas.syscanvas.model.SC.SC_Canvas;
import com.syscanvas.syscanvas.model.SC.SC_Estado;
import com.syscanvas.syscanvas.model.SC.SC_Tipo_Canvas;

/**
 * Mapper responsable de convertir entre SC_Canvas (Entity) y SC_CanvasDTO.
 * 
 * Responsabilidades:
 * - Convertir Entity → DTO (con relaciones completas)
 * - Convertir DTO → Entity (para persistencia)
 * - Mapear relaciones SC (Estado, TipoCanvas)
 */
@Component
@Mapper(componentModel = "spring")
public class SC_CanvasMapper {

    /**
     * Convierte una entidad SC_Canvas a DTO.
     */
    public SC_CanvasDTO toDTO(SC_Canvas entity) {
        if (entity == null) {
            return null;
        }

        return SC_CanvasDTO.builder()
                .codCanvas(entity.getCodCanvas())
                .nomCanvas(entity.getNomCanvas())
                .desCanvas(entity.getDesCanvas())
                .fecCreacion(entity.getFecCreacion())
                .fecModificacion(entity.getFecModificacion())
                .editable(entity.getEditable())
                .codPyto(entity.getCodPyto())
                .codPersona(entity.getCodPersona())
                .estado(mapearEstado(entity.getEstado()))
                .tipoCanvas(mapearTipoCanvas(entity.getTipoCanvas()))
                .proyectoInfo(entity.getProyectoInfo())
                .empleadoInfo(entity.getEmpleadoInfo())
                .build();
    }

    /**
     * Convierte un DTO a entidad SC_Canvas.
     */
    public SC_Canvas toEntity(SC_CanvasDTO dto) {
        if (dto == null) {
            return null;
        }

        SC_Canvas entity = new SC_Canvas();
        entity.setCodCanvas(dto.getCodCanvas());
        entity.setNomCanvas(dto.getNomCanvas());
        entity.setDesCanvas(dto.getDesCanvas());
        entity.setFecCreacion(dto.getFecCreacion());
        entity.setFecModificacion(dto.getFecModificacion());
        entity.setEditable(dto.getEditable());
        entity.setCodPyto(dto.getCodPyto());
        entity.setCodPersona(dto.getCodPersona());

        entity.setEstado(crearReferenciaEstado(dto.getEstado()));
        entity.setTipoCanvas(crearReferenciaTipoCanvas(dto.getTipoCanvas()));

        return entity;
    }

    // ========== MÉTODOS PRIVADOS - MAPEO ENTITY → DTO ==========

    /**
     * Mapea SC_Estado a DTO.
     */
    private SC_EstadoDTO mapearEstado(SC_Estado estado) {
        if (estado == null) {
            return null;
        }
        
        return SC_EstadoDTO.builder()
                .codEstado(estado.getCodEstado())
                .nomEstado(estado.getNomEstado())
                .desEstado(estado.getDesEstado())
                .vigente(estado.getVigente())
                .build();
    }

    /**
     * Mapea SC_Tipo_Canvas a DTO.
     */
    private SC_Tipo_CanvasDTO mapearTipoCanvas(SC_Tipo_Canvas tipoCanvas) {
        if (tipoCanvas == null) {
            return null;
        }
        
        return SC_Tipo_CanvasDTO.builder()
                .tipCanvas(tipoCanvas.getTipCanvas())
                .desTipCanvas(tipoCanvas.getDesTipCanvas())
                .vigente(tipoCanvas.getVigente())
                .build();
    }

    // ========== MÉTODOS PRIVADOS - MAPEO DTO → ENTITY ==========

    /**
     * Crea referencia a SC_Estado.
     */
    private SC_Estado crearReferenciaEstado(SC_EstadoDTO dto) {
        if (dto == null || dto.getCodEstado() == null) {
            return null;
        }
        
        SC_Estado estado = new SC_Estado();
        estado.setCodEstado(dto.getCodEstado());
        return estado;
    }

    /**
     * Crea referencia a SC_Tipo_Canvas.
     */
    private SC_Tipo_Canvas crearReferenciaTipoCanvas(SC_Tipo_CanvasDTO dto) {
        if (dto == null || dto.getTipCanvas() == null) {
            return null;
        }
        
        SC_Tipo_Canvas tipoCanvas = new SC_Tipo_Canvas();
        tipoCanvas.setTipCanvas(dto.getTipCanvas());
        return tipoCanvas;
    }
}