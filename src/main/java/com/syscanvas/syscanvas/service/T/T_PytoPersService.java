package com.syscanvas.syscanvas.service.T;

import com.syscanvas.syscanvas.dao.T.T_PytoPersDAO;
import com.syscanvas.syscanvas.dao.T.T_ProyectoDAO;
import com.syscanvas.syscanvas.dao.T.T_EmpleadoDAO;
import com.syscanvas.syscanvas.exception.ExcepcionRecursoNoEncontrado;
import com.syscanvas.syscanvas.model.T.T_PytoPers;
import com.syscanvas.syscanvas.model.T.T_Proyecto;
import com.syscanvas.syscanvas.model.T.T_Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
@Transactional(transactionManager = "tTransactionManager", readOnly = true)
public class T_PytoPersService {

    private static final Logger logger = LoggerFactory.getLogger(T_PytoPersService.class);

    private final T_PytoPersDAO pytoPersDAO;
    private final T_ProyectoDAO proyectoDAO;
    private final T_EmpleadoDAO empleadoDAO;

    public T_PytoPersService(
            T_PytoPersDAO pytoPersDAO,
            T_ProyectoDAO proyectoDAO,
            T_EmpleadoDAO empleadoDAO
    ) {
        this.pytoPersDAO = pytoPersDAO;
        this.proyectoDAO = proyectoDAO;
        this.empleadoDAO = empleadoDAO;
    }

    public Page<T_PytoPers> listarTodos(Pageable pageable) {
        List<T_PytoPers> todas = pytoPersDAO.findAll();

        int inicio = (int) pageable.getOffset();
        int fin = Math.min((inicio + pageable.getPageSize()), todas.size());

        return new PageImpl<>(todas.subList(inicio, fin), pageable, todas.size());
    }

    public Page<T_PytoPers> buscarPorProyecto(Long codPyto, Pageable pageable) {
        T_Proyecto proyecto = proyectoDAO.findById(codPyto)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                        "Proyecto", "codPyto", codPyto));

        List<T_PytoPers> asignaciones = pytoPersDAO.findByProyecto(proyecto);

        int inicio = (int) pageable.getOffset();
        int fin = Math.min((inicio + pageable.getPageSize()), asignaciones.size());

        return new PageImpl<>(asignaciones.subList(inicio, fin), pageable, asignaciones.size());
    }

    public List<T_PytoPers> buscarPorProyecto(Long codPyto) {
        return pytoPersDAO.findByProyecto_CodPyto(codPyto);
    }

    public List<T_PytoPers> buscarPorEmpleado(Long codPersona) {
        T_Empleado empleado = empleadoDAO.findById(codPersona)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                        "Empleado", "codPersona", codPersona));

        return pytoPersDAO.findByEmpleado(empleado);
    }

    public List<T_PytoPers> buscarActivas() {
        return pytoPersDAO.findByVigente(1);
    }
}
