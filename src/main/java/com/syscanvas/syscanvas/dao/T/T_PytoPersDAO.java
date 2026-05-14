package com.syscanvas.syscanvas.dao.T;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.syscanvas.syscanvas.model.T.*;

@Repository
public interface T_PytoPersDAO extends JpaRepository<T_PytoPers, T_PytoPersId> {
    List<T_PytoPers> findByProyecto(T_Proyecto proyecto);
    List<T_PytoPers> findByEmpleado(T_Empleado empleado);
    List<T_PytoPers> findByVigente(Integer vigente);
    List<T_PytoPers> findByProyecto_CodPyto(Long codPyto);
}