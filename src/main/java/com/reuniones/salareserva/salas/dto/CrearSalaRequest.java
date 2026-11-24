package com.reuniones.salareserva.salas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/** Datos para crear una sala. Solo {@code nombre} es obligatorio. */
public record CrearSalaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 150, message = "El nombre no puede superar 150 caracteres")
        String nombre,

        @Size(max = 200, message = "La ubicación no puede superar 200 caracteres")
        String ubicacion,

        @Positive(message = "La capacidad debe ser mayor que cero")
        Integer capacidad
) {
}
