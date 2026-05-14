package com.syscanvas.syscanvas.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.context.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.*;
import org.springframework.stereotype.*;
import org.springframework.web.filter.*;

import com.syscanvas.syscanvas.service.auth.*;

import java.io.*;
import java.util.*;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final SysCanvasUserDetailsService userDetailsService;
    private final Set<String> publicPaths;

    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   SysCanvasUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;

        this.publicPaths = Set.of(
                "/login.html",
                "/html/",
                "/css/",
                "/js/",
                "/images/",
                "/videos/",
                "/uploads/",
                "/favicon.ico",
                "/api/auth/login",
                "/api/auth/validate",
                "/.well-known/"
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getServletPath();

        // 🔓 Rutas públicas
        if (publicPaths.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authorizationHeader =
        Optional.ofNullable(request.getHeader("Authorization"))
        .orElse(request.getHeader("authorization"));

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"mensaje\": \"Token no proporcionado\"}");
            return;
        }


        try {
            String jwt = authorizationHeader.substring(7);

            String codPersonaStr = jwtUtil.extractCodPersona(jwt);

            if (codPersonaStr == null) {
                throw new RuntimeException("Token sin codPersona");
            }

            Long codPersona = Long.valueOf(codPersonaStr);

            request.setAttribute("codPersona", codPersona);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(codPersonaStr);

                if (jwtUtil.validateToken(jwt, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"mensaje\": \"Token inválido\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
