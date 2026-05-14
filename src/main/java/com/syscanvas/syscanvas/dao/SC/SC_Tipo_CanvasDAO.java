package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.util.*;

@Repository
public interface SC_Tipo_CanvasDAO extends JpaRepository<SC_Tipo_Canvas, String> {
    List<SC_Tipo_Canvas> findByTipCanvas(String tipCanvas);
    List<SC_Tipo_Canvas> findByVigente(Integer vigente);
}