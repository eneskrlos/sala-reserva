package com.reuniones.salareserva.salas.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/** Acceso a datos de {@link Sala}. */
public interface SalaRepository extends JpaRepository<Sala, Long> {

    boolean existsByNombreIgnoreCase(String nombre);

    List<Sala> findAllByOrderByNombreAsc();
}
