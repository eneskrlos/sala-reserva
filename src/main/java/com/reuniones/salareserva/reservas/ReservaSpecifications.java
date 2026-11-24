package com.reuniones.salareserva.reservas;

import com.reuniones.salareserva.reservas.domain.Reserva;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specifications para filtrar reservas de forma dinámica y componible.
 * Cada método devuelve {@code null} cuando el parámetro es nulo, de modo que puede
 * encadenarse con {@code .and(...)} sin afectar la consulta.
 */
public final class ReservaSpecifications {

    private ReservaSpecifications() {
    }

    /** Filtra por sala. */
    public static Specification<Reserva> deSala(Long salaId) {
        if (salaId == null) {
            return null;
        }
        return (root, query, cb) -> cb.equal(root.get("sala").get("id"), salaId);
    }

    /**
     * Filtra las reservas que intersectan el día indicado (interpretado en UTC):
     * {@code inicio < finDelDia AND fin > inicioDelDia}.
     */
    public static Specification<Reserva> enFecha(LocalDate fecha) {
        if (fecha == null) {
            return null;
        }
        OffsetDateTime inicioDia = fecha.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime finDia = inicioDia.plusDays(1);
        return (root, query, cb) -> cb.and(
                cb.lessThan(root.get("inicio"), finDia),
                cb.greaterThan(root.get("fin"), inicioDia));
    }
}
