package com.syscanvas.syscanvas.service.auth;

import com.syscanvas.syscanvas.dao.T.*;
import com.syscanvas.syscanvas.dto.auth.*;
import com.syscanvas.syscanvas.exception.*;
import com.syscanvas.syscanvas.security.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;

/**
 * Servicio de autenticación y gestión de credenciales.
 *
 * Delegaciones:
 * - Cambio de contraseña → PasswordService 
 * - Validación de identidad → IdentityValidator 
 */

@Service
public class AuthService {
    private final T_EmpleadoDAO empleadoDAO;
    private final AuthenticationManager authenticationManager;
    private final SysCanvasUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    public AuthService(
            T_EmpleadoDAO empleadoDAO,
            AuthenticationManager authenticationManager,
            SysCanvasUserDetailsService userDetailsService,
            JwtUtil jwtUtil
    ) {
        this.empleadoDAO = empleadoDAO;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    /*
     * Autentica un usuario y genera el token JWT.
     */
    public LoginResponse login(LoginRequest request) {
        UserDetails userDetails = autenticarYObtenerDetalles(request);
        
        String rol = extraerRol(userDetails);
        String token = jwtUtil.generateToken(request.getCodPersona().toString(), rol);
        
        return construirLoginResponse(request.getCodPersona(), token, rol);
    }

    // ========== MÉTODOS PRIVADOS ==========

    /*
     * Autentica las credenciales del usuario y obtiene sus detalles.
     */
    private UserDetails autenticarYObtenerDetalles(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    String.valueOf(request.getCodPersona()), 
                    request.getPassword()
                )
            );

            return userDetailsService.loadUserByUsername(
                request.getCodPersona().toString()
            );
        } catch (BadCredentialsException e) {
            throw new ExcepcionAutenticacionFallida("Usuario o contraseña incorrectos");
        }
    }

    /*
     * Obtiene el rol del usuario.
     */
    private String extraerRol(UserDetails userDetails) {
        return userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();
    }

    /*
     * Construye el DTO de respuesta (LoginResponse) con token y datos de usuario.
     */
    private LoginResponse construirLoginResponse(Long codPersona, String token, String rol) {
        return LoginResponse.builder()
                .token(token)
                .rol(rol)
                .redirectUrl(determinarRedirectUrl(rol))
                .codPersona(codPersona)
                .nombreCompleto(obtenerNombreCompleto(codPersona))
                .build();
    }

    /*
     * Determina la URL de redirección según el rol.
     */
    private String determinarRedirectUrl(String rol) {
        return switch (rol) {
            case "ROLE_JEFE" -> "/html/inicio-jefe.html";
            case "ROLE_ANALISTA" -> "/html/inicio-analista.html";
            default -> "/html/login.html";
        };
    }

    /*
     * Obtiene el nombre completo del usuario autenticado.
     */
    private String obtenerNombreCompleto(Long codPersona) {
        return empleadoDAO.findById(codPersona)
                .map(emp -> emp.getPersona() != null
                        ? emp.getPersona().getDesPersona()
                        : emp.getNombre() + " " + emp.getApellido())
                .orElse("Usuario");
    }
}
