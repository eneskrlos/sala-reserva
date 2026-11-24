package com.reuniones.salareserva.shared.exception;

/** Se lanza al intentar registrar un username que ya existe. Resulta en HTTP 409. */
public class UsuarioYaExisteException extends RuntimeException {

    public UsuarioYaExisteException(String username) {
        super("Ya existe un usuario con username '" + username + "'");
    }
}
