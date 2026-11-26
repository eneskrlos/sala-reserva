package com.reuniones.salareserva.auth;

import com.reuniones.salareserva.auth.dto.AuthResponse;
import com.reuniones.salareserva.auth.dto.LoginRequest;
import com.reuniones.salareserva.auth.dto.RefreshRequest;
import com.reuniones.salareserva.auth.dto.RegisterRequest;
import com.reuniones.salareserva.auth.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoints públicos de registro y autenticación. */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro y login para obtener el token JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registra un nuevo usuario (rol USER)")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.registrar(request));
    }

    @PostMapping("/login")
    @Operation(summary = "Autentica y devuelve un token JWT")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Devuelve un nuevo access token a partir de un refresh token")
    public ResponseEntity<AuthResponse> refrescar(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refrescar(request));
    }

}
