package com.reuniones.salareserva.salas;

import com.reuniones.salareserva.salas.dto.CrearSalaRequest;
import com.reuniones.salareserva.salas.dto.SalaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints de salas: crear (JWT) y listar (público). */
@RestController
@RequestMapping("/api/salas")
@Tag(name = "Salas", description = "Gestión de salas de reuniones")
public class SalaController {

    private final SalaService salaService;

    public SalaController(SalaService salaService) {
        this.salaService = salaService;
    }

    @PostMapping
    @Operation(summary = "Crea una sala (requiere JWT)")
    public ResponseEntity<SalaResponse> crear(@Valid @RequestBody CrearSalaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(salaService.crear(request));
    }

    @GetMapping
    @Operation(summary = "Lista todas las salas (público)")
    public ResponseEntity<List<SalaResponse>> listar() {
        return ResponseEntity.ok(salaService.listar());
    }
}
