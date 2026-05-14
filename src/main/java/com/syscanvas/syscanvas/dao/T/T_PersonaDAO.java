package com.syscanvas.syscanvas.dao.T;

import org.springframework.stereotype.*;
import org.springframework.data.jpa.repository.*;
import com.syscanvas.syscanvas.model.T.*;
import java.util.*;
import java.util.List;


@Repository
public interface T_PersonaDAO extends JpaRepository<T_Persona, Long> {
    Optional<T_Persona> findByCodPersona(Long codPersona);
    List<T_Persona> findByTipoPersona(T_Tipo_Persona tipoPersona);  
}
