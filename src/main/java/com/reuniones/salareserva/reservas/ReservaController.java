package com.reuniones.salareserva.reservas;

import com.reuniones.salareserva.reservas.dto.CrearReservaRequest;
import com.reuniones.salareserva.reservas.dto.ReservaResponse;
import com.reuniones.salareserva.shared.dto.PaginaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de reservas: crear y cancelar (JWT) y listar con filtros (público). */
@RestController
@RequestMapping("/api/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas de salas")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @PostMapping
    @Operation(summary = "Crea una reserva (requiere JWT). 409 si la sala está ocupada")
    public ResponseEntity<ReservaResponse> crear(@Valid @RequestBody CrearReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Lista reservas (público) con filtros por sala y fecha, paginado")
    public ResponseEntity<PaginaResponse<ReservaResponse>> listar(
            @RequestParam(required = false) Long salaId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @ParameterObject @PageableDefault(size = 20, sort = "inicio", direction = Sort.Direction.ASC)
            Pageable pageable) {
        return ResponseEntity.ok(
                PaginaResponse.from(reservaService.listar(salaId, fecha, pageable)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancela una reserva - borrado lógico (requiere JWT)")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
