package com.reuniones.salareserva.reservas.domain;

import java.time.OffsetDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** Acceso a datos de {@link Reserva}. Soporta filtros dinámicos vía Specifications. */
public interface ReservaRepository
        extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {

    /**
     * Indica si existe alguna reserva ACTIVA de la sala que se solape con el intervalo dado.
     * Solape: {@code inicio < finExistente AND fin > inicioExistente}.
     */
    @Query("""
            SELECT COUNT(r) > 0 FROM Reserva r
            WHERE r.sala.id = :salaId
              AND r.estado = com.reuniones.salareserva.reservas.domain.EstadoReserva.ACTIVA
              AND r.inicio < :fin
              AND r.fin > :inicio
            """)
    boolean existeSolape(@Param("salaId") Long salaId,
                         @Param("inicio") OffsetDateTime inicio,
                         @Param("fin") OffsetDateTime fin);
}
