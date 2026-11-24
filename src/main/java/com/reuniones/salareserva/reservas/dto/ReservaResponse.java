package com.reuniones.salareserva.reservas.dto;

import com.reuniones.salareserva.reservas.domain.Reserva;
import java.time.OffsetDateTime;

/** Representación pública de una reserva. */
public record ReservaResponse(
        Long id,
        Long salaId,
        String salaNombre,
        String responsable,
        OffsetDateTime inicio,
        OffsetDateTime fin,
        String estado) {

    public static ReservaResponse from(Reserva reserva) {
        return new ReservaResponse(
                reserva.getId(),
                reserva.getSala().getId(),
                reserva.getSala().getNombre(),
                reserva.getResponsable(),
                reserva.getInicio(),
                reserva.getFin(),
                reserva.getEstado().name());
    }
}
