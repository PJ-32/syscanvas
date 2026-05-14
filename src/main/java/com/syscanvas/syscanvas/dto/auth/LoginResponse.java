package com.syscanvas.syscanvas.dto.auth;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponse {
    private String token;
    private String rol;
    private String redirectUrl;
    private Long codPersona;
    private String nombreCompleto;
}
