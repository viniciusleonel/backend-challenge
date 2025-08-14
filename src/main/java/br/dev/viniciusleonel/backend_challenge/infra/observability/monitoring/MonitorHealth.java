package br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring;

import br.dev.viniciusleonel.backend_challenge.infra.exception.HealthCheckException;
import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class MonitorHealth {

    private static final Logger log = LoggerFactory.getLogger(MonitorHealth.class);

    public static ResponseEntity<Map<String, Object>> healthCheck(MetricsCollector metricsCollector) {
        try {
            log.info("Executando Health check");

            Map<String, Object> health = new HashMap<>();

            health.put("status", "UP");
            health.put("timestamp", System.currentTimeMillis());

            // Adiciona contexto de tracing de forma segura
            String currentTraceId = TraceContext.getCurrentTraceId();
            if (currentTraceId != null) {
                health.put("traceId", currentTraceId);
            }

            String currentSpanId = TraceContext.getCurrentSpanId();
            if (currentSpanId != null) {
                health.put("spanId", currentSpanId);
            }

            // Adiciona metricas basicas de saude
            Map<String, Object> metrics = metricsCollector.getMetrics();
            health.put("totalRequests", metrics.get("totalRequests"));
            health.put("successRate", metrics.get("successRate"));

            Object performanceMetrics = metrics.get("performanceMetrics");
            if (performanceMetrics instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> perf = (Map<String, Object>) performanceMetrics;
                health.put("avgResponseTime", perf.get("avgResponseTime"));
            }

            log.info("Health check executado com sucesso");
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.info("Erro ao executar Health check");
            throw new HealthCheckException("Erro no health check");
        }
    }
}
