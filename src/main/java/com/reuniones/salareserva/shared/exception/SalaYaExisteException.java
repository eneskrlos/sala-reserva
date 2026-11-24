package com.reuniones.salareserva.shared.exception;

/** Se lanza al intentar crear una sala con un nombre que ya existe. Resulta en HTTP 409. */
public class SalaYaExisteException extends RuntimeException {

    public SalaYaExisteException(String nombre) {
        super("Ya existe una sala con nombre '" + nombre + "'");
    }
}
