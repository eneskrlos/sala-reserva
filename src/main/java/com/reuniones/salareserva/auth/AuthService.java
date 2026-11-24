package com.reuniones.salareserva.auth;

import com.reuniones.salareserva.auth.domain.Usuario;
import com.reuniones.salareserva.auth.domain.UsuarioRepository;
import com.reuniones.salareserva.auth.dto.AuthResponse;
import com.reuniones.salareserva.auth.dto.LoginRequest;
import com.reuniones.salareserva.auth.dto.RegisterRequest;
import com.reuniones.salareserva.auth.dto.UsuarioResponse;
import com.reuniones.salareserva.auth.jwt.JwtService;
import com.reuniones.salareserva.shared.exception.UsuarioYaExisteException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Lógica de registro y autenticación de usuarios. */
@Service
public class AuthService {

    private static final String ROL_POR_DEFECTO = "USER";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /** Registra un nuevo usuario con rol USER. Lanza 409 si el username ya existe. */
    @Transactional
    public UsuarioResponse registrar(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.username())) {
            throw new UsuarioYaExisteException(request.username());
        }
        Usuario usuario = Usuario.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .rol(ROL_POR_DEFECTO)
                .build();
        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }

    /** Valida las credenciales y devuelve un JWT. Lanza 401 si son inválidas. */
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password()));
        String token = jwtService.generarToken(request.username());
        return AuthResponse.bearer(token, jwtService.getExpirationMs());
    }
}
