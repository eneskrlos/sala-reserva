package com.reuniones.salareserva.shared.exception;

/** Error de regla de negocio en los datos (p. ej. fin anterior al inicio). Resulta en HTTP 400. */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
