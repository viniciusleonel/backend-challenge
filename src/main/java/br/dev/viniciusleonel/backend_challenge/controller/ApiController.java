package br.dev.viniciusleonel.backend_challenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import br.dev.viniciusleonel.backend_challenge.infra.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.tracing.TraceSpan;
import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);
    private final MetricsCollector metricsCollector;

    public ApiController(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateJwt(@RequestParam String token) {
        try (TraceSpan span = new TraceSpan("validateJwt")) {
            span.addTag("tokenLength", String.valueOf(token.length()));
            span.addBusinessContext("operation", "jwt_validation");
            
            log.info("Endpoint /api/validate chamado");
            
            if (!JwtValidator.isValid(token)) {
                span.addError("JWT invalido");
                log.error("JWT invalido");
                
                // Registra metricas
                metricsCollector.recordJwtValidation(false);
                
                return ResponseEntity.badRequest().body(false);
            }

            log.info("JWT valido");
            
            // Registra metricas
            metricsCollector.recordJwtValidation(true);
            
            return ResponseEntity.ok(true);
        }
    }
}