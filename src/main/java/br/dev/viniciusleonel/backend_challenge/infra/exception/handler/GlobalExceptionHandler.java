package br.dev.viniciusleonel.backend_challenge.infra.exception.handler;

import br.dev.viniciusleonel.backend_challenge.infra.exception.*;
import com.auth0.jwt.exceptions.JWTDecodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(ResetMetricsException.class)
    public ResponseEntity<Map<String, Object>> handleResetMetricsError(ResetMetricsException ex) {
        log.error("Erro ao resetar metricas: {}", ex.getMessage(), ex);
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Erro ao resetar metricas: " + ex.getMessage());
        errorResponse.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.internalServerError().body(errorResponse);
    }

    @ExceptionHandler(HealthCheckException.class)
    public ResponseEntity<Map<String, Object>> handleHealthCheckError(HealthCheckException ex) {
        log.error("Erro no health check: {}", ex.getMessage(), ex);
        Map<String, Object> errorHealth = new HashMap<>();
        errorHealth.put("status", "DOWN");
        errorHealth.put("error", ex.getMessage());
        errorHealth.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.internalServerError().body(errorHealth);
    }

    @ExceptionHandler(CollectMetricsException.class)
    public ResponseEntity<Map<String, Object>> handleCollectMetricsError(CollectMetricsException ex) {
        log.error("Erro ao coletar metricas: {}", ex.getMessage(), ex);
        Map<String, Object> errorMetrics = new HashMap<>();
        errorMetrics.put("error", "Erro ao coletar metricas: " + ex.getMessage());
        errorMetrics.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.internalServerError().body(errorMetrics);
    }

    @ExceptionHandler(CollectEndpointTraceException.class)
    public ResponseEntity<Map<String, Object>> handleCollectEndpointTraceError(CollectEndpointTraceException ex) {
        log.error("Erro ao coletar traces por endpoint: {}", ex.getMessage(), ex);
        Map<String, Object> errorTraces = new HashMap<>();
        errorTraces.put("error", "Erro ao coletar traces: " + ex.getMessage());
        errorTraces.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.internalServerError().body(errorTraces);
    }

    @ExceptionHandler(CollectCurrentTraceException.class)
    public ResponseEntity<Map<String, Object>> handleCollectCurrentTraceError(CollectCurrentTraceException ex) {
        log.error("Erro ao coletar informacoes de tracing: {}", ex.getMessage(), ex);
        Map<String, Object> errorTrace = new HashMap<>();
        errorTrace.put("error", "Erro ao coletar tracing: " + ex.getMessage());
        errorTrace.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.internalServerError().body(errorTrace);
    }
}

