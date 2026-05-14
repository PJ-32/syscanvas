package com.syscanvas.syscanvas.security;

import org.springframework.context.annotation.*;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.method.configuration.*;
import org.springframework.security.config.annotation.web.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.*;
import org.springframework.security.crypto.bcrypt.*;
import org.springframework.security.crypto.password.*;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import com.syscanvas.syscanvas.service.auth.*;
import java.util.*;

/*
 * Configuración de seguridad de la aplicación.
 */

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final SysCanvasUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                        SysCanvasUserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }

    /*
     * Cadena de filtros de seguridad.
     * Configuración stateless con JWT.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(this::configureAuthorization)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .userDetailsService(userDetailsService) 
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /*
     * Configuración de reglas de autorización.
     * Extrae lógica de permisos para mejor mantenibilidad.
     */
    private void configureAuthorization(
            org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer
            <HttpSecurity>.AuthorizationManagerRequestMatcherRegistry auth) {
        
        auth
            // Recursos públicos
            .requestMatchers("/favicon.ico", "/static/**", "/images/**", "/videos/**", "/uploads/**").permitAll()
            .requestMatchers("/html/**", "/css/**", "/js/**").permitAll()
            
            // Endpoints de autenticación
            .requestMatchers("/api/auth/**").permitAll()
            
            // API SC - Solo usuarios autenticados
            .requestMatchers(HttpMethod.GET, "/api/sc/**").hasAnyRole("JEFE", "ANALISTA")
            .requestMatchers(HttpMethod.POST, "/api/sc/**").hasAnyRole("JEFE", "ANALISTA")
            .requestMatchers(HttpMethod.PUT, "/api/sc/**").hasAnyRole("JEFE", "ANALISTA")
            .requestMatchers(HttpMethod.DELETE, "/api/sc/**").hasAnyRole("JEFE", "ANALISTA")
            
            // API T - Solo lectura para todos los autenticados
            .requestMatchers(HttpMethod.GET, "/api/t/**").hasAnyRole("JEFE", "ANALISTA")
            
            // Resto requiere autenticación
            .anyRequest().authenticated();
    }

    /*
     * Configuración de CORS segura.
     * Solo permite orígenes específicos en producción.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        
        //En producción, reemplazar "*" con dominios específicos
        config.setAllowedOrigins(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setMaxAge(3600L); // Cache preflight 1 hora
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /*
     * BCrypt con fuerza 12 (balance seguridad/performance).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        //return new BCryptPasswordEncoder();
        return new PasswordEncoder() {
            private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

            @Override
            public String encode(CharSequence rawPassword) {
                return bcrypt.encode(rawPassword);
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                if (encodedPassword.startsWith("$2a$")) {
                    // Comparación BCrypt normal
                    return bcrypt.matches(rawPassword, encodedPassword);
                } else {
                    // Comparación de texto plano (solo temporal)
                    return rawPassword.toString().equals(encodedPassword);
                }
            }
        };
    }
}