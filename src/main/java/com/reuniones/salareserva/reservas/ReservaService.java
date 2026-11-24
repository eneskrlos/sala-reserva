package com.reuniones.salareserva.reservas;

import com.reuniones.salareserva.reservas.domain.EstadoReserva;
import com.reuniones.salareserva.reservas.domain.Reserva;
import com.reuniones.salareserva.reservas.domain.ReservaRepository;
import com.reuniones.salareserva.reservas.dto.CrearReservaRequest;
import com.reuniones.salareserva.reservas.dto.ReservaResponse;
import com.reuniones.salareserva.salas.domain.Sala;
import com.reuniones.salareserva.salas.domain.SalaRepository;
import com.reuniones.salareserva.shared.exception.RecursoNoEncontradoException;
import com.reuniones.salareserva.shared.exception.ReglaNegocioException;
import com.reuniones.salareserva.shared.exception.SolapeReservaException;
import java.time.LocalDate;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lógica de negocio de reservas. */
@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final SalaRepository salaRepository;

    public ReservaService(ReservaRepository reservaRepository, SalaRepository salaRepository) {
        this.reservaRepository = reservaRepository;
        this.salaRepository = salaRepository;
    }

    /**
     * Crea una reserva ACTIVA. Valida que la sala exista (404), que el fin sea posterior al
     * inicio (400) y que no haya solape con otra reserva activa (409). La exclusion constraint
     * de la base de datos actúa como red de seguridad ante concurrencia.
     */
    @Transactional
    public ReservaResponse crear(CrearReservaRequest request) {
        Sala sala = salaRepository.findById(request.salaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Sala", request.salaId()));

        if (!request.fin().isAfter(request.inicio())) {
            throw new ReglaNegocioException("El fin debe ser posterior al inicio");
        }

        if (reservaRepository.existeSolape(sala.getId(), request.inicio(), request.fin())) {
            throw new SolapeReservaException();
        }

        Reserva reserva = Reserva.builder()
                .sala(sala)
                .responsable(request.responsable())
                .inicio(request.inicio())
                .fin(request.fin())
                .estado(EstadoReserva.ACTIVA)
                .build();

        try {
            return ReservaResponse.from(reservaRepository.saveAndFlush(reserva));
        } catch (DataIntegrityViolationException ex) {
            // Red de seguridad: la exclusion constraint detectó un solape por concurrencia.
            throw new SolapeReservaException();
        }
    }

    /** Lista reservas con filtros opcionales por sala y por fecha, paginadas. */
    @Transactional(readOnly = true)
    public Page<ReservaResponse> listar(Long salaId, LocalDate fecha, Pageable pageable) {
        Specification<Reserva> spec = Specification
                .where(ReservaSpecifications.deSala(salaId))
                .and(ReservaSpecifications.enFecha(fecha));
        return reservaRepository.findAll(spec, pageable).map(ReservaResponse::from);
    }

    /** Cancela una reserva (borrado lógico). Idempotente; 404 si no existe. */
    @Transactional
    public void cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Reserva", id));
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }
}
