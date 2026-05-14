package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.model.SC.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.util.*;

@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_EstadoService {
    private final SC_EstadoDAO estadoDAO;

    public SC_EstadoService (SC_EstadoDAO repository) {
        this.estadoDAO = repository;
    }

    @Transactional(readOnly = true)
    public List<SC_Estado> listarTodos() {
        return estadoDAO.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<SC_Estado> buscarPorCodigo(Integer codEstado) {
        return estadoDAO.findByCodEstado(codEstado);
    }
    
    @Transactional(readOnly = true)
    public Optional<SC_Estado> buscarPorNomEstado(String nomEstado) {
        return estadoDAO.findByNomEstado(nomEstado);
    }
    
    public SC_Estado guardar(SC_Estado estado) {
        try {
            return estadoDAO.save(estado);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar Estado: " + e.getMessage(), e);
        }
    }
    
    public void eliminar(Integer codEstado) {
        estadoDAO.deleteById(codEstado);
    }
}
