package com.syscanvas.syscanvas.dao.T;

import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.T.*;

@Repository
public interface T_Tipo_PersonaDAO extends JpaRepository<T_Tipo_Persona, String> {
    List<T_Tipo_Persona> findByVigente(Integer vigente);
}
