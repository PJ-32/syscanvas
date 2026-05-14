package com.syscanvas.syscanvas.mapper.T;

import com.syscanvas.syscanvas.dto.T.*;
import com.syscanvas.syscanvas.model.T.*;
import org.springframework.stereotype.*;

/*
 * Mapper para convertir entre T_Proyecto (Entity) y T_ProyectoDTO.
 * 
 * Responsabilidades:
 * - Convertir Entity → DTO (solo datos básicos, sin jefeProyecto por ahora)
 * - Mantener separación de responsabilidades (SoC)
 */
@Component
public class T_ProyectoMapper {
    /*
     * Convierte una entidad T_Proyecto a DTO.
     */
    public T_ProyectoDTO toDTO(T_Proyecto entity) {
        if (entity == null) {
            return null;
        }

        return T_ProyectoDTO.builder()
                .codPyto(entity.getCodPyto())
                .nomPyto(entity.getNomPyto())
                .vigente(entity.getVigente())
                .annoIni(entity.getAnnoIni())
                .annoFin(entity.getAnnoFin())
                // jefeProyecto se deja null por ahora (evita cargar relaciones innecesarias)
                .build();
    }
}
