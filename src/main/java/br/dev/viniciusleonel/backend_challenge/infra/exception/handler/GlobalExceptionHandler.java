package br.dev.viniciusleonel.backend_challenge.infra.exception.handler;

import br.dev.viniciusleonel.backend_challenge.utils.JwtDecoder;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidClaimException.class)
    public ResponseEntity<Boolean> handleInvalidName(InvalidClaimException ex) {
        log.error("Claim inválida detectada: {}", ex.getMessage(), ex);
        return ResponseEntity.unprocessableEntity().body(false);
    }

    @ExceptionHandler(JWTDecodeException.class)
    public ResponseEntity<Boolean> handleInvalidName(JWTDecodeException ex) {
        log.error("Token inválido detectado: {}", ex.getMessage(), ex);
        return ResponseEntity.badRequest().body(false);
    }
}

