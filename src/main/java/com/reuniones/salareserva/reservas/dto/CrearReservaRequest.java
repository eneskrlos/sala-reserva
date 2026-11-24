package com.reuniones.salareserva.reservas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

/** Datos para crear una reserva. Las fechas van en formato ISO-8601 con offset. */
public record CrearReservaRequest(

        @NotNull(message = "El salaId es obligatorio")
        Long salaId,

        @NotBlank(message = "El responsable es obligatorio")
        @Size(max = 150, message = "El responsable no puede superar 150 caracteres")
        String responsable,

        @NotNull(message = "El inicio es obligatorio")
        OffsetDateTime inicio,

        @NotNull(message = "El fin es obligatorio")
        OffsetDateTime fin
) {
}
