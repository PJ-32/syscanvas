package com.syscanvas.syscanvas.service.SC;

import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.model.SC.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import java.util.*;


@Service
@Transactional(transactionManager = "scTransactionManager")
public class SC_Tipo_CanvasService {
    private final SC_Tipo_CanvasDAO tipoCanvasDAO;

    public SC_Tipo_CanvasService(SC_Tipo_CanvasDAO tipoCanvasDAO) {
        this.tipoCanvasDAO = tipoCanvasDAO;
    }

    @Transactional(readOnly = true)
    public List<SC_Tipo_Canvas> listarTodos() {
        // Traer solo los activos
        List<SC_Tipo_Canvas> tipos = tipoCanvasDAO.findByVigente(1);

        // Filtrar para excluir el tipo Libre (F)
        return tipos.stream()
                .filter(tipo -> !tipo.getTipCanvas().equalsIgnoreCase("F"))
                .toList();
    }
}
