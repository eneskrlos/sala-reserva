package com.reuniones.salareserva.auth.dto;

/** Respuesta del login: el token JWT y su metadata. */
public record AuthResponse(String token, String tokenType, long expiresInMs) {

    public static AuthResponse bearer(String token, long expiresInMs) {
        return new AuthResponse(token, "Bearer", expiresInMs);
    }
}
