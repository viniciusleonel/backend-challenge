package br.dev.viniciusleonel.backend_challenge.infra.observability.tracing;

import br.dev.viniciusleonel.backend_challenge.infra.exception.CollectCurrentTraceException;
import br.dev.viniciusleonel.backend_challenge.infra.exception.CollectEndpointTraceException;
import br.dev.viniciusleonel.backend_challenge.infra.exception.CollectMetricsException;
import br.dev.viniciusleonel.backend_challenge.infra.exception.ResetMetricsException;
import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class TraceMetrics {

    private static final Logger log = LoggerFactory.getLogger(TraceMetrics.class);

    public static ResponseEntity<Map<String, Object>> resetMetrics (MetricsCollector metricsCollector){
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Metricas resetadas com sucesso");
            response.put("timestamp", System.currentTimeMillis());

            // Adiciona contexto de tracing de forma segura
            String currentTraceId = TraceContext.getCurrentTraceId();
            if (currentTraceId != null) {
                response.put("traceId", currentTraceId);
            }

            // Adiciona outras informacoes de contexto se disponiveis
            String currentSpanId = TraceContext.getCurrentSpanId();
            if (currentSpanId != null) {
                response.put("spanId", currentSpanId);
            }

            String requestId = MDC.get("requestId");
            if (requestId != null) {
                response.put("requestId", requestId);
            }

            String endpoint = MDC.get("endpoint");
            if (endpoint != null) {
                response.put("endpoint", endpoint);
            }

            metricsCollector.resetMetrics();
            log.info("Metricas resetadas com sucesso");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Erro ao resetar metricas, lançando excecao");
            throw new ResetMetricsException("Erro ao resetar metricas");
        }
    }

    public static ResponseEntity<Map<String, Object>> collectMetrics(MetricsCollector metricsCollector) {
        try {
            // Verifica se o TraceContext esta disponivel antes de usar
            String currentTraceId = TraceContext.getCurrentTraceId();
            String currentSpanId = TraceContext.getCurrentSpanId();
            String currentOperation = TraceContext.getCurrentOperationName();

            Map<String, Object> metrics = metricsCollector.getMetrics();

            // Adiciona contexto de tracing de forma segura
            if (currentTraceId != null) {
                metrics.put("currentTraceId", currentTraceId);
            }
            if (currentSpanId != null) {
                metrics.put("currentSpanId", currentSpanId);
            }
            if (currentOperation != null) {
                metrics.put("currentOperation", currentOperation);
            }

            log.info("Metricas coletadas com sucesso");
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            throw new CollectMetricsException("Erro ao coletar metricas");
        }
    }

    public static ResponseEntity<Map<String, Object>> collectEndpointTraces() {
        try {
            Map<String, Object> traces = new HashMap<>();

            // Simula informacoes de traces por endpoint
            Map<String, Object> endpointTraces = new HashMap<>();
            endpointTraces.put("/api/validate", Map.of(
                    "totalTraces", 150,
                    "avgDuration", 45.2,
                    "errorRate", 2.1,
                    "lastTrace", System.currentTimeMillis()
            ));

            traces.put("endpoints", endpointTraces);

            // Adiciona contexto de tracing de forma segura
            String currentTraceId = TraceContext.getCurrentTraceId();
            if (currentTraceId != null) {
                traces.put("traceId", currentTraceId);
            }

            traces.put("timestamp", System.currentTimeMillis());

            log.info("Informacoes de traces por endpoint coletadas");
            return ResponseEntity.ok(traces);
        } catch (Exception e) {
            throw new CollectEndpointTraceException("Erro ao coletar endpoint traces");
        }
    }

    public static ResponseEntity<Map<String, Object>> collectCurrentTrace() {
        try {
            Map<String, Object> trace = new HashMap<>();

            // Adiciona informacoes de tracing de forma segura
            String traceId = TraceContext.getCurrentTraceId();
            if (traceId != null) {
                trace.put("traceId", traceId);
            }

            String spanId = TraceContext.getCurrentSpanId();
            if (spanId != null) {
                trace.put("spanId", spanId);
            }

            String operationName = TraceContext.getCurrentOperationName();
            if (operationName != null) {
                trace.put("operationName", operationName);
            }

            // Adiciona informacoes do MDC
            String requestId = MDC.get("requestId");
            if (requestId != null) {
                trace.put("requestId", requestId);
            }

            String endpoint = MDC.get("endpoint");
            if (endpoint != null) {
                trace.put("endpoint", endpoint);
            }

            trace.put("timestamp", System.currentTimeMillis());

            log.info("Informacoes de tracing coletadas");
            return ResponseEntity.ok(trace);
        } catch (Exception e) {
            throw new CollectCurrentTraceException("Erro ao coletar informacoes de tracing");
        }
    }
}
