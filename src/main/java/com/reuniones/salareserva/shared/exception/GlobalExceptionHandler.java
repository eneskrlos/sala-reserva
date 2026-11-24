package com.reuniones.salareserva.shared.exception;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Manejo centralizado de errores. Devuelve respuestas consistentes con
 * {@link ProblemDetail} (RFC-7807). Se amplía en tareas posteriores (404 de recursos,
 * 409 de solape de reservas, etc.).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 400 — Errores de validación de Bean Validation sobre el cuerpo de la petición. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Datos inválidos");
        pd.setDetail("Uno o más campos no superaron la validación.");
        Map<String, String> errores = new LinkedHashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errores.putIfAbsent(fe.getField(), fe.getDefaultMessage());
        }
        pd.setProperty("errores", errores);
        return pd;
    }

    /** 400 — Regla de negocio incumplida (p. ej. fin anterior al inicio). */
    @ExceptionHandler(ReglaNegocioException.class)
    public ProblemDetail handleReglaNegocio(ReglaNegocioException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Solicitud inválida");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    /** 404 — Recurso no encontrado. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail handleNoEncontrado(RecursoNoEncontradoException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("No encontrado");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    /** 409 — Recursos en conflicto: duplicados o solape de reservas. */
    @ExceptionHandler({UsuarioYaExisteException.class, SalaYaExisteException.class,
            SolapeReservaException.class})
    public ProblemDetail handleConflicto(RuntimeException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflicto");
        pd.setDetail(ex.getMessage());
        return pd;
    }

    /** 401 — Credenciales inválidas en el login. */
    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ProblemDetail handleAuth(AuthenticationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
        pd.setTitle("No autenticado");
        pd.setDetail("Credenciales inválidas.");
        return pd;
    }
}
