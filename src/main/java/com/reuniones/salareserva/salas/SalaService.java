package com.reuniones.salareserva.salas;

import com.reuniones.salareserva.salas.domain.Sala;
import com.reuniones.salareserva.salas.domain.SalaRepository;
import com.reuniones.salareserva.salas.dto.CrearSalaRequest;
import com.reuniones.salareserva.salas.dto.SalaResponse;
import com.reuniones.salareserva.shared.exception.SalaYaExisteException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lógica de negocio de salas. */
@Service
public class SalaService {

    private final SalaRepository salaRepository;

    public SalaService(SalaRepository salaRepository) {
        this.salaRepository = salaRepository;
    }

    /** Crea una sala activa. Lanza 409 si ya existe otra con el mismo nombre. */
    @Transactional
    public SalaResponse crear(CrearSalaRequest request) {
        if (salaRepository.existsByNombreIgnoreCase(request.nombre())) {
            throw new SalaYaExisteException(request.nombre());
        }
        Sala sala = Sala.builder()
                .nombre(request.nombre())
                .ubicacion(request.ubicacion())
                .capacidad(request.capacidad())
                .activa(true)
                .build();
        return SalaResponse.from(salaRepository.save(sala));
    }

    /** Lista todas las salas ordenadas por nombre. */
    @Transactional(readOnly = true)
    public List<SalaResponse> listar() {
        return salaRepository.findAllByOrderByNombreAsc()
                .stream()
                .map(SalaResponse::from)
                .toList();
    }
}
