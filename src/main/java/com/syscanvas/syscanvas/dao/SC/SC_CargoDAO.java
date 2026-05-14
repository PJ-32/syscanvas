package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.util.*;

@Repository
public interface SC_CargoDAO extends JpaRepository<SC_Cargo, Long> {
    Optional<SC_Cargo> findByCodCargo(Long codCargo);
    List<SC_Cargo> findByNomCargo(String nomCargo);
    List<SC_Cargo> findByVigente(Integer vigente);
}
