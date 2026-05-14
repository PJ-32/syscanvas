package com.syscanvas.syscanvas.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.*;
import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.*;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.*;
import javax.crypto.*;
import java.nio.charset.*;
import java.util.*;
import java.util.function.*;

@Component
public class JwtUtil {
    @Value("${jwt.secret:syscanvas-secret-key-must-be-32chars-minimum-2025}")
    private String secret;

    // Tiempo de expiración: 24 horas por defecto
    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    public String generateToken(String codPersona, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        claims.put("codPersona", codPersona);
        return createToken(claims, codPersona);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractCodPersona(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRol(String token) {
        return extractClaim(token, claims -> claims.get("rol", String.class));
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()                
                    .verifyWith(getSigningKey()) 
                    .build()
                    .parseSignedClaims(token)    
                    .getPayload();            
        } catch (JwtException e) {
            throw new IllegalArgumentException("Token JWT inválido o alterado", e);
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractCodPersona(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    @PostConstruct
public void printSecret() {
    System.out.println("🔑 JWT SECRET ACTUAL: " + secret);
}

}
