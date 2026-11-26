package com.reuniones.salareserva.auth.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración del JWT (prefijo {@code app.jwt} en application.yml).
 *
 * @param secret       secreto HMAC-SHA256 (mínimo 32 bytes)
 * @param expirationMs validez del token en milisegundos
 * @param refreshExpirationMs validez de expiracion del refreshToken en milisegundo
 */
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(String secret, long expirationMs, long refreshExpirationMs) {
}
