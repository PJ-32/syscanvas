package com.syscanvas.syscanvas.service.T;

import com.syscanvas.syscanvas.dao.T.*;
import com.syscanvas.syscanvas.exception.*;
import com.syscanvas.syscanvas.model.T.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;
import org.slf4j.*;
import java.util.*;

//import java.util.regex.*;

/**
 * Servicio para gestionar Empleados.
 * 
 * Responsabilidades:
 * - CRUD de empleados
 * - Validación de DNI, email, celular
 * - Consultas por cargo
 * - Gestión de vigencia
 */

@Service
//@Transactional(transactionManager = "tTransactionManager", readOnly = true)
public class T_EmpleadoService {
    private static final Logger logger = LoggerFactory.getLogger(T_EmpleadoService.class);
    private static final Integer VIGENTE_ACTIVO = 1;
    // private static final Integer VIGENTE_INACTIVO = 0;
    // private static final Pattern EMAIL_PATTERN = Pattern.compile(
    //     "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    // );
    // private static final Pattern DNI_PATTERN = Pattern.compile("^[0-9]{8}$");
    // private static final Pattern CELULAR_PATTERN = Pattern.compile("^9[0-9]{8}$");
    private final T_EmpleadoDAO empleadoDAO;

    public T_EmpleadoService(T_EmpleadoDAO empleadoDAO) {
        this.empleadoDAO = empleadoDAO;
    }

    /*
     * Lista todos los empleados con paginación.
     */
    @Transactional(transactionManager = "tTransactionManager", readOnly = true)
    public Page<T_Empleado> listarTodos(Pageable pageable) {
        logger.debug("Listando empleados - Página: {}", pageable.getPageNumber());

        return empleadoDAO.findAll(pageable);
    }

    /*
     * Lista empleados activos (vigente = 1).
     */
    @Transactional(transactionManager = "tTransactionManager", readOnly = true)
    public Page<T_Empleado> listarActivos(Pageable pageable) {
        logger.debug("Listando empleados activos");

        List<T_Empleado> activos = empleadoDAO.findByVigente(VIGENTE_ACTIVO);
        
        int inicio = (int) pageable.getOffset();
        int fin = Math.min((inicio + pageable.getPageSize()), activos.size());
        
        List<T_Empleado> empleadosPaginados = activos.subList(inicio, fin);
        
        return new PageImpl<>(empleadosPaginados, pageable, activos.size());
    }

    /*
     * Busca un empleado por ID.
     */
    @Transactional(transactionManager = "tTransactionManager", readOnly = true)
    public T_Empleado buscarPorId(Long codPersona) {
        logger.debug("Buscando empleado ID: {}", codPersona);

        return empleadoDAO.findById(codPersona)
                .orElseThrow(() -> new ExcepcionRecursoNoEncontrado(
                    "Empleado", "codPersona", codPersona
                ));
    }

    /*
     * Busca un empleado por ID (devuelve Optional).
     */
    @Transactional(transactionManager = "tTransactionManager", readOnly = true)
    public Optional<T_Empleado> buscarPorCodPersona(Long codPersona) {
        return empleadoDAO.findById(codPersona);
    }

    /*
     * Busca empleados por DNI.
     */
    @Transactional(transactionManager = "tTransactionManager", readOnly = true)
    public Optional<T_Empleado> buscarPorDni(String dni) {
        logger.debug("Buscando empleado por DNI: {}", dni);

        return empleadoDAO.findByDni(dni);
    }

    /*
     * Busca empleados por cargo.
     */
    @Transactional(transactionManager = "tTransactionManager", readOnly = true)
    public List<T_Empleado> buscarPorCargo(Long codCargo) {
        logger.debug("Buscando empleados del cargo: {}", codCargo);

        return empleadoDAO.findByCodCargo(codCargo);
    }

    /*
     * Guarda un nuevo empleado .
     */
    /*@Transactional
    public T_Empleado guardar(T_Empleado empleado) {
        logger.info("Guardando nuevo empleado: {} {}", 
            empleado.getNombre(), empleado.getApellido());
        
        validarEmpleado(empleado);
        validarDniUnico(empleado);
        validarEmailUnico(empleado);
        
        empleado.setVigente(VIGENTE_ACTIVO);
        
        T_Empleado guardado = empleadoDAO.save(empleado);
        
        logger.info("Empleado guardado con ID: {}", guardado.getCodPersona());
        
        return guardado;
    }*/

    /*
     * Actualiza un empleado existente.
     */
    /*
    @Transactional
    public T_Empleado actualizar(Long codPersona, T_Empleado empleadoActualizado) {
        logger.info("Actualizando empleado ID: {}", codPersona);
        
        T_Empleado existente = buscarPorId(codPersona);
        
        validarEmpleado(empleadoActualizado);
        
        actualizarCamposEmpleado(existente, empleadoActualizado);
        
        T_Empleado actualizado = empleadoDAO.save(existente);
        
        logger.info("Empleado actualizado exitosamente");
        
        return actualizado;
    }*/
    /*Metodo para actualizar mas simple */
    @Transactional(transactionManager = "tTransactionManager")
    public T_Empleado actualizar(Long codPersona, T_Empleado datos) {

        T_Empleado empleado = buscarPorId(codPersona);

        if (datos.getNombre() != null) empleado.setNombre(datos.getNombre());
        if (datos.getApellido() != null) empleado.setApellido(datos.getApellido());
        if (datos.getDni() != null) empleado.setDni(datos.getDni());
        if (datos.getEmail() != null) empleado.setEmail(datos.getEmail());
        if (datos.getCelular() != null) empleado.setCelular(datos.getCelular());
        if (datos.getDireccion() != null) empleado.setDireccion(datos.getDireccion());
        if (datos.getHobby() != null) empleado.setHobby(datos.getHobby());
        if (datos.getFecNac() != null) empleado.setFecNac(datos.getFecNac());

        return empleadoDAO.save(empleado);
    }

    @Transactional(transactionManager = "tTransactionManager")
    public void actualizarFoto(Long codPersona, String fotoBase64) {
        logger.info("Actualizando foto de empleado ID: {}", codPersona);

        T_Empleado empleado = buscarPorId(codPersona);

        if (fotoBase64 == null || fotoBase64.isBlank()) {
            // Si llega vacío, interpretamos como "eliminar foto"
            empleado.setFoto(null);
        } else {
            byte[] bytes = Base64.getDecoder().decode(fotoBase64);
            empleado.setFoto(bytes);
        }

        empleadoDAO.save(empleado);

        logger.info("Foto actualizada correctamente para empleado {}", codPersona);
    }

    @Transactional("tTransactionManager")
    public void eliminarFoto(Long codPersona) {
        T_Empleado empleado = buscarPorId(codPersona);

        empleado.setFoto(null);

        empleadoDAO.save(empleado);
    }





    /*
     * Cambia la vigencia de un empleado.
     */
    /*
    @Transactional
    public T_Empleado cambiarVigencia(Long codPersona, boolean activar) {
        logger.info("Cambiando vigencia de empleado {} a: {}", 
            codPersona, activar ? "ACTIVO" : "INACTIVO");
        
        T_Empleado empleado = buscarPorId(codPersona);
        
        empleado.setVigente(activar ? VIGENTE_ACTIVO : VIGENTE_INACTIVO);
        
        T_Empleado actualizado = empleadoDAO.save(empleado);
        
        logger.info("Vigencia de empleado cambiada exitosamente");
        
        return actualizado;
    }*/

    // ========== MÉTODOS PRIVADOS ==========
/*
    private void validarEmpleado(T_Empleado empleado) {
        List<String> errores = new ArrayList<>();
        
        if (empleado.getNombre() == null || empleado.getNombre().isBlank()) {
            errores.add("El nombre es obligatorio");
        }
        
        if (empleado.getApellido() == null || empleado.getApellido().isBlank()) {
            errores.add("El apellido es obligatorio");
        }
        
        if (empleado.getDni() == null || !DNI_PATTERN.matcher(empleado.getDni()).matches()) {
            errores.add("El DNI debe tener exactamente 8 dígitos numéricos");
        }
        
        if (empleado.getEmail() != null && 
            !EMAIL_PATTERN.matcher(empleado.getEmail()).matches()) {
            errores.add("El email no tiene un formato válido");
        }
        
        if (empleado.getCelular() != null && 
            !CELULAR_PATTERN.matcher(empleado.getCelular()).matches()) {
            errores.add("El celular debe tener 9 dígitos y comenzar con 9");
        }
        
        if (empleado.getFecNac() == null) {
            errores.add("La fecha de nacimiento es obligatoria");
        }
        
        if (!errores.isEmpty()) {
            throw new ExcepcionValidacion("Errores de validación", errores);
        }
    }*/
/*
    private void validarDniUnico(T_Empleado empleado) {
        Optional<T_Empleado> existente = buscarPorDni(empleado.getDni());
        
        if (existente.isPresent() && 
            !existente.get().getCodPersona().equals(empleado.getCodPersona())) {
            throw new ExcepcionValidacion(
                "Ya existe un empleado con el DNI: " + empleado.getDni()
            );
        }
    }

    private void validarEmailUnico(T_Empleado empleado) {
        if (empleado.getEmail() == null) return;

        Optional<T_Empleado> existente = empleadoDAO.findAll().stream()
                .filter(e -> empleado.getEmail().equals(e.getEmail()))
                .findFirst();
        
        if (existente.isPresent() && 
            !existente.get().getCodPersona().equals(empleado.getCodPersona())) {
            throw new ExcepcionValidacion(
                "Ya existe un empleado con el email: " + empleado.getEmail()
            );
        }
    }
*/ /*
    private void actualizarCamposEmpleado(T_Empleado empleado, T_Empleado actualizado) {
        if (actualizado.getNombre() != null) {
            empleado.setNombre(actualizado.getNombre());
        }
        if (actualizado.getApellido() != null) {
            empleado.setApellido(actualizado.getApellido());
        }
        if (actualizado.getDni() != null) {
            empleado.setDni(actualizado.getDni());
        }
        if (actualizado.getEmail() != null) {
            empleado.setEmail(actualizado.getEmail());
        }
        if (actualizado.getCelular() != null) {
            empleado.setCelular(actualizado.getCelular());
        }
        if (actualizado.getDireccion() != null) {
            empleado.setDireccion(actualizado.getDireccion());
        }
        if (actualizado.getHobby() != null) {
            empleado.setHobby(actualizado.getHobby());
        }
        if (actualizado.getFecNac() != null) {
            empleado.setFecNac(actualizado.getFecNac());
        }
        if (actualizado.getCodCargo() != null) {
            empleado.setCodCargo(actualizado.getCodCargo());
        }
    }*/
}
