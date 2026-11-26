package com.reuniones.salareserva.auth;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.reuniones.salareserva.auth.domain.RefreshToken;
import com.reuniones.salareserva.auth.domain.RefreshTokenRepository;
import com.reuniones.salareserva.auth.domain.Usuario;
import com.reuniones.salareserva.auth.dto.RefreshTokenResponse;
import com.reuniones.salareserva.auth.jwt.JwtService;
import com.reuniones.salareserva.shared.exception.ReglaNegocioException;

@Service 
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    // private final long refreshExpirationMs; Date expiraRefrshToken = new Date(ahora.getTime() + refreshExpirationMs);
    
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
        JwtService jwtService
     ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Transactional 
    public RefreshTokenResponse crear(Usuario usuario) {
        
       
        OffsetDateTime expiraEnDate = OffsetDateTime.now().plus(jwtService.getRefreshExpirationMs(), ChronoUnit.MILLIS);
        RefreshToken refreshToken = RefreshToken.builder()
            .token(UUID.randomUUID().toString())
            .usuario(usuario)
            .expiraEn(expiraEnDate)
            .revocado(false)
            .build();

        return RefreshTokenResponse.from(refreshTokenRepository.save(refreshToken));    
    }

    @Transactional(readOnly = true)
    public RefreshTokenResponse validar (String token) {

        if(token == null || token.trim().isEmpty()){
            throw new ReglaNegocioException("El token no puede ser vacio");
        }

        RefreshToken tokenObtenido = refreshTokenRepository.findByToken(token)
                        .orElseThrow(() -> new ReglaNegocioException("Refresh token inválido"));
        OffsetDateTime ahora = OffsetDateTime.now();
        
        if(tokenObtenido.isRevocado() || tokenObtenido.getExpiraEn().isBefore(ahora)){
            throw new ReglaNegocioException("El token expiro o ya fue revocado.");
        }

        return RefreshTokenResponse.from(tokenObtenido);

    }

    @Transactional
    public void revocar(String token){
        
        if(token == null || token.trim().isEmpty()){
            throw new ReglaNegocioException("El token no puede ser vacio");
        }

        RefreshToken tokenObtenido = refreshTokenRepository.findByToken(token)
                        .orElseThrow(() -> new ReglaNegocioException("Refresh token inválido"));
        OffsetDateTime ahora = OffsetDateTime.now();
        
        if(tokenObtenido.isRevocado() || tokenObtenido.getExpiraEn().isBefore(ahora)){
            throw new ReglaNegocioException("El token expiro o fue revocado");
        }

        tokenObtenido.setRevocado(true);

        refreshTokenRepository.save(tokenObtenido);
    }
    
}
