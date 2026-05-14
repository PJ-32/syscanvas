package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.model.SC.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.util.*;

@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_CargoService {
    private final SC_CargoDAO cargoDAO;

    public SC_CargoService (SC_CargoDAO cargoDAO) {
        this.cargoDAO = cargoDAO;
    }

    @Transactional(readOnly = true)
    public List<SC_Cargo> listarTodos() {
        return cargoDAO.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<SC_Cargo> buscarPorCodCargo(Long codCargo) {
        return cargoDAO.findByCodCargo(codCargo);
    }
    
    public SC_Cargo guardar(SC_Cargo cargo) {
        return cargoDAO.save(cargo);
    }
    
    public void eliminar(Long codCargo) {
        cargoDAO.deleteById(codCargo);
    }
}
