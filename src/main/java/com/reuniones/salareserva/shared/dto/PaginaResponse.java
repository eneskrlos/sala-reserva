package com.reuniones.salareserva.shared.dto;

import java.util.List;
import org.springframework.data.domain.Page;

/**
 * Envoltorio de respuesta paginada con un contrato estable (evita exponer la
 * serialización interna de {@link Page}).
 */
public record PaginaResponse<T>(
        List<T> contenido,
        int pagina,
        int tamano,
        long totalElementos,
        int totalPaginas,
        boolean ultima) {

    public static <T> PaginaResponse<T> from(Page<T> page) {
        return new PaginaResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast());
    }
}
