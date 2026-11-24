package com.reuniones.salareserva.shared.exception;

/** Se lanza cuando no se encuentra un recurso por su identificador. Resulta en HTTP 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Object id) {
        super(recurso + " no encontrada/o con id " + id);
    }
}
