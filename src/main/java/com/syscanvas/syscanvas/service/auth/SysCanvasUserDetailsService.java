package com.syscanvas.syscanvas.service.auth;

import com.syscanvas.syscanvas.dao.T.*;
import com.syscanvas.syscanvas.dao.SC.*;
import com.syscanvas.syscanvas.model.SC.*;
import com.syscanvas.syscanvas.model.T.*;
import org.springframework.security.core.authority.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import java.util.*;

@Service
public class SysCanvasUserDetailsService implements UserDetailsService {
    private final T_EmpleadoDAO empleadoDAO;
    private final SC_CargoDAO cargoDAO;

    public SysCanvasUserDetailsService(T_EmpleadoDAO empleadoDAO, SC_CargoDAO cargoDAO) {
        this.empleadoDAO = empleadoDAO;
        this.cargoDAO = cargoDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String codPersona) throws UsernameNotFoundException {
        Long codigo;
        
        try {
            codigo = Long.parseLong(codPersona);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Código de persona inválido: " + codPersona);
        }

        // Buscar empleado por código de persona
        Optional<T_Empleado> empleadoOpt = empleadoDAO.findById(codigo);
        
        if (empleadoOpt.isEmpty()) {
            throw new UsernameNotFoundException("Usuario no encontrado con código: " + codPersona);
        }

        T_Empleado empleado = empleadoOpt.get();
        
        // Validar que el empleado esté vigente
        if (empleado.getVigente() != null && empleado.getVigente() == 0) {
            throw new UsernameNotFoundException("Usuario no vigente: " + codPersona);
        }

        String rol = determinarRolEmpleado(empleado.getCodCargo());

        return User.builder()
                .username(String.valueOf(empleado.getCodPersona()))
                .password(empleado.getPassword() != null ? empleado.getPassword() : "")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority(rol)))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(empleado.getVigente() != null && empleado.getVigente() == 0)
                .build();
    }

    /*
     * Determina el rol del empleado consultando SC_Cargo.
     */
    private String determinarRolEmpleado(Long codCargo) {
        if (codCargo == null) {
            return "ROLE_ANALISTA";
        }
        
        Optional<SC_Cargo> cargoOpt;        
        try {
            cargoOpt = cargoDAO.findById(codCargo);
        } catch (Exception e) {
            return "ROLE_ANALISTA";
        }

        if (cargoOpt.isEmpty()) {
            return "ROLE_ANALISTA";
        }

        SC_Cargo cargo = cargoOpt.get();
        
        // Verificar vigencia del cargo
        if (cargo.getVigente() != null && cargo.getVigente() == 0) {
            return "ROLE_ANALISTA";
        }

        String nombreCargo = (cargo.getNomCargo() != null ? cargo.getNomCargo() : "").toLowerCase();
        String descripcionCargo = (cargo.getDesCargo() != null ? cargo.getDesCargo() : "").toLowerCase();
        
        String textoCompleto = nombreCargo + " " + descripcionCargo;

        if (textoCompleto.contains("jefe") || textoCompleto.contains("gerente")
            || textoCompleto.contains("director") || textoCompleto.contains("coordinador")
            || textoCompleto.contains("líder") || textoCompleto.contains("supervisor")) {
        return "ROLE_JEFE";
        }
        return "ROLE_ANALISTA";
    }
}
