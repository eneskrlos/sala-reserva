package com.reuniones.salareserva.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Datos para registrar un nuevo usuario. */
public record RegisterRequest(

        @NotBlank(message = "El username es obligatorio")
        @Size(min = 3, max = 100, message = "El username debe tener entre 3 y 100 caracteres")
        String username,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 72, message = "La contraseña debe tener entre 6 y 72 caracteres")
        String password
) {
}
