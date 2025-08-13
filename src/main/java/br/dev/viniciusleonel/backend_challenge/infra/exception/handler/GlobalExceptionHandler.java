package br.dev.viniciusleonel.backend_challenge.infra.exception.handler;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidClaimException.class)
    public ResponseEntity<Boolean> handleInvalidName(InvalidClaimException ex) {
        return ResponseEntity.unprocessableEntity().body(false);
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<Boolean> handleInvalidName(JWTDecodeException ex) {
        return ResponseEntity.badRequest().body(false);
    }
}

