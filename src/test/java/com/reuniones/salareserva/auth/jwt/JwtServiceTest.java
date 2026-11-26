package com.reuniones.salareserva.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

/** Pruebas unitarias del servicio de JWT (sin contexto de Spring). */
class JwtServiceTest {

    private JwtService jwtService;

    private UserDetails usuario(String username) {
        return User.withUsername(username)
                .password("irrelevante")
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .build();
    }

    @BeforeEach
    void setUp() {
        JwtProperties props = new JwtProperties(
                "secreto-de-pruebas-suficientemente-largo-para-hmac-256-bits", 900000L, 604800000);
        jwtService = new JwtService(props);
    }

    @Test
    @DisplayName("Genera un token y extrae el username (subject)")
    void generaYExtrae() {
        String token = jwtService.generarToken("admin");

        assertThat(token).isNotBlank();
        assertThat(jwtService.extraerUsername(token)).isEqualTo("admin");
    }

    @Test
    @DisplayName("El token es válido para el mismo usuario")
    void tokenValido() {
        String token = jwtService.generarToken("admin");

        assertThat(jwtService.esValido(token, usuario("admin"))).isTrue();
    }

    @Test
    @DisplayName("El token NO es válido para otro usuario")
    void tokenOtroUsuario() {
        String token = jwtService.generarToken("admin");

        assertThat(jwtService.esValido(token, usuario("carlos"))).isFalse();
    }

    @Test
    @DisplayName("Un token manipulado no se considera válido")
    void tokenInvalido() {
        assertThat(jwtService.esValido("token.falso.xyz", usuario("admin"))).isFalse();
    }
}
