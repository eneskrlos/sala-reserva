package com.reuniones.salareserva.reservas;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Pruebas unitarias de la lógica de {@link ReservaService} (con Mockito). */
@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private SalaRepository salaRepository;

    @InjectMocks
    private ReservaService reservaService;

    private final OffsetDateTime inicio = OffsetDateTime.of(2026, 12, 1, 10, 0, 0, 0, ZoneOffset.UTC);
    private final OffsetDateTime fin = OffsetDateTime.of(2026, 12, 1, 11, 0, 0, 0, ZoneOffset.UTC);

    private Sala salaExistente() {
        return Sala.builder().id(1L).nombre("Sala A").activa(true).build();
    }

    private CrearReservaRequest request(OffsetDateTime ini, OffsetDateTime f) {
        return new CrearReservaRequest(1L, "Ana", ini, f);
    }

    @Test
    @DisplayName("Crea la reserva cuando no hay solape")
    void crearOk() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaExistente()));
        when(reservaRepository.existeSolape(1L, inicio, fin)).thenReturn(false);
        when(reservaRepository.saveAndFlush(any(Reserva.class)))
                .thenAnswer(inv -> {
                    Reserva r = inv.getArgument(0);
                    r.setId(99L);
                    return r;
                });

        ReservaResponse res = reservaService.crear(request(inicio, fin));

        assertThat(res.id()).isEqualTo(99L);
        assertThat(res.estado()).isEqualTo("ACTIVA");
        assertThat(res.salaId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Lanza 409 (SolapeReservaException) cuando la sala está ocupada")
    void crearSolape() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaExistente()));
        when(reservaRepository.existeSolape(1L, inicio, fin)).thenReturn(true);

        assertThatThrownBy(() -> reservaService.crear(request(inicio, fin)))
                .isInstanceOf(SolapeReservaException.class);

        verify(reservaRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Lanza 400 (ReglaNegocioException) cuando fin no es posterior al inicio")
    void crearFinAntesDeInicio() {
        when(salaRepository.findById(1L)).thenReturn(Optional.of(salaExistente()));

        assertThatThrownBy(() -> reservaService.crear(request(fin, inicio)))
                .isInstanceOf(ReglaNegocioException.class);

        verify(reservaRepository, never()).existeSolape(anyLong(), any(), any());
    }

    @Test
    @DisplayName("Lanza 404 (RecursoNoEncontradoException) cuando la sala no existe")
    void crearSalaInexistente() {
        when(salaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.crear(request(inicio, fin)))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Cancelar cambia el estado a CANCELADA (borrado lógico)")
    void cancelar() {
        Reserva reserva = Reserva.builder()
                .id(5L).sala(salaExistente()).responsable("Ana")
                .inicio(inicio).fin(fin).estado(EstadoReserva.ACTIVA).build();
        when(reservaRepository.findById(5L)).thenReturn(Optional.of(reserva));

        reservaService.cancelar(5L);

        assertThat(reserva.getEstado()).isEqualTo(EstadoReserva.CANCELADA);
        verify(reservaRepository).save(reserva);
    }

    @Test
    @DisplayName("Cancelar una reserva inexistente lanza 404")
    void cancelarInexistente() {
        when(reservaRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reservaService.cancelar(123L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
