package com.reuniones.salareserva.auth.dto;

import com.reuniones.salareserva.auth.domain.RolUsuario;
import com.reuniones.salareserva.auth.domain.Usuario;

/** Representación pública de un usuario (sin exponer la contraseña). */
public record UsuarioResponse(Long id, String username, RolUsuario rol) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(usuario.getId(), usuario.getUsername(), usuario.getRol());
    }
}
