package com.reuniones.salareserva.salas.dto;

import com.reuniones.salareserva.salas.domain.Sala;

/** Representación pública de una sala. */
public record SalaResponse(Long id, String nombre, String ubicacion,
                           Integer capacidad, boolean activa) {

    public static SalaResponse from(Sala sala) {
        return new SalaResponse(
                sala.getId(),
                sala.getNombre(),
                sala.getUbicacion(),
                sala.getCapacidad(),
                Boolean.TRUE.equals(sala.getActiva()));
    }
}
