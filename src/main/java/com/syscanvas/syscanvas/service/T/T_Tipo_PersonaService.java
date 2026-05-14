package com.syscanvas.syscanvas.service.T;

import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

@Service
@Transactional(transactionManager = "tTransactionManager", readOnly = true)
public class T_Tipo_PersonaService {
    
}