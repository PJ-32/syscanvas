package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.model.SC.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.util.*;

@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_ComentarioService {
    private final SC_ComentarioDAO comentarioDAO;

    public SC_ComentarioService (SC_ComentarioDAO comentarioDAO) {
        this.comentarioDAO = comentarioDAO;
    }

    @Transactional(readOnly = true)
    public List<SC_Comentario> listarTodos() {
        return comentarioDAO.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<SC_Comentario> buscarPorCodComentario(Long codcomentario) {
        return comentarioDAO.findByCodComentario(codcomentario);
    }
    
    @Transactional(readOnly = true)
    public List<SC_Comentario> buscarPorCanvas(SC_Canvas canvas) {
        return comentarioDAO.findByCanvas(canvas);
    }
    
    @Transactional(readOnly = true)
    public List<SC_Comentario> buscarPorEmpleado(Long codPersona) {
        return comentarioDAO.findByCodPersona(codPersona);
    }
    
    public SC_Comentario guardar(SC_Comentario comentario) {
        try {
            return comentarioDAO.save(comentario);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar Comentario: " + e.getMessage(), e);
        }
    }
    
    public void eliminar(Long codComentario) {
        comentarioDAO.deleteById(codComentario);
    }
}
