package br.dev.viniciusleonel.backend_challenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceSpan;
import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final MetricsCollector metricsCollector;
    private final JwtValidator jwtValidator;

    public ApiController(MetricsCollector metricsCollector, JwtValidator jwtValidator) {
        this.metricsCollector = metricsCollector;
        this.jwtValidator = jwtValidator;
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateJwt(@RequestParam String token) {
        try (TraceSpan span = new TraceSpan("validateJwt")) {
            span.addTag("tokenLength", String.valueOf(token.length()));
            span.addBusinessContext("operation", "jwt_validation");
            
            boolean isValid = jwtValidator.isValid(token);
            
            // Registra metricas
            metricsCollector.recordJwtValidation(isValid);
            
            return isValid ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
        }
    }
}