package com.reuniones.salareserva.shared.exception;

/** Se lanza cuando una reserva se solapa con otra ACTIVA en la misma sala. Resulta en HTTP 409. */
public class SolapeReservaException extends RuntimeException {

    public SolapeReservaException() {
        super("La sala ya está reservada en ese horario");
    }
}
