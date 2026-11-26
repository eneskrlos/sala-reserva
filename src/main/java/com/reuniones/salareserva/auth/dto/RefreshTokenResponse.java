package com.reuniones.salareserva.auth.dto;

import java.time.OffsetDateTime;

import com.reuniones.salareserva.auth.domain.RefreshToken;


public record RefreshTokenResponse(Long id, String token, OffsetDateTime expiraEn, String username) {
    
    public static RefreshTokenResponse from (RefreshToken refreshToken ) {
        return new RefreshTokenResponse(refreshToken.getId(), refreshToken.getToken(), refreshToken.getExpiraEn(), refreshToken.getUsuario().getUsername());
    }
}
