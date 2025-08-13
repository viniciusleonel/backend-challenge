package br.dev.viniciusleonel.backend_challenge.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.viniciusleonel.backend_challenge.infra.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.tracing.TraceContext;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private static final Logger log = LoggerFactory.getLogger(MonitoringController.class);
    private final MetricsCollector metricsCollector;

    public MonitoringController(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
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
            log.error("Erro ao coletar metricas: {}", e.getMessage(), e);
            
            // Retorna metricas basicas em caso de erro
            Map<String, Object> errorMetrics = new HashMap<>();
            errorMetrics.put("error", "Erro ao coletar metricas: " + e.getMessage());
            errorMetrics.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(errorMetrics);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        try {
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
                Map<String, Object> perf = (Map<String, Object>) performanceMetrics;
                health.put("avgResponseTime", perf.get("avgResponseTime"));
            }
            
            log.info("Health check executado com sucesso");
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("Erro no health check: {}", e.getMessage(), e);
            
            Map<String, Object> errorHealth = new HashMap<>();
            errorHealth.put("status", "DOWN");
            errorHealth.put("error", e.getMessage());
            errorHealth.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(errorHealth);
        }
    }

    @GetMapping("/tracing/current")
    public ResponseEntity<Map<String, Object>> getCurrentTrace() {
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
            log.error("Erro ao coletar informacoes de tracing: {}", e.getMessage(), e);
            
            Map<String, Object> errorTrace = new HashMap<>();
            errorTrace.put("error", "Erro ao coletar tracing: " + e.getMessage());
            errorTrace.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(errorTrace);
        }
    }

    @GetMapping("/tracing/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpointTraces() {
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
            log.error("Erro ao coletar traces por endpoint: {}", e.getMessage(), e);
            
            Map<String, Object> errorTraces = new HashMap<>();
            errorTraces.put("error", "Erro ao coletar traces: " + e.getMessage());
            errorTraces.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(errorTraces);
        }
    }

    @PostMapping("/metrics/reset")
    public ResponseEntity<Map<String, Object>> resetMetrics() {
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
            log.error("Erro ao resetar metricas: {}", e.getMessage(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erro ao resetar metricas: " + e.getMessage());
            errorResponse.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}