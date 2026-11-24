package com.reuniones.salareserva.auth.dto;

import jakarta.validation.constraints.NotBlank;

/** Credenciales para autenticarse y obtener un JWT. */
public record LoginRequest(

        @NotBlank(message = "El username es obligatorio")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
