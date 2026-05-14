package com.syscanvas.syscanvas.dao.SC;

import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.*;
import com.syscanvas.syscanvas.model.SC.*;
import java.time.*;
import java.util.*;

@Repository
public interface SC_ComentarioDAO extends JpaRepository<SC_Comentario, Long> {
    Optional<SC_Comentario> findByCodComentario(Long codComentario);
    List<SC_Comentario> findByCanvas(SC_Canvas canvas);
    List<SC_Comentario> findByCodPersona(Long codPersona);
    List<SC_Comentario> findByFecComentarioBetween(LocalDateTime inicio, LocalDateTime fin);
}
