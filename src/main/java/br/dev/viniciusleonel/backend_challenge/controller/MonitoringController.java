package br.dev.viniciusleonel.backend_challenge.controller;

import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MonitorHealth;
import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    private final MetricsCollector metricsCollector;
    private static final Logger log = LoggerFactory.getLogger(MonitoringController.class);

    public MonitoringController(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @GetMapping("/metrics")
    public ResponseEntity<Map<String, Object>> getMetrics() {
        log.info("Endpoint chamado: /monitoring/metrics");
        return TraceMetrics.collectMetrics(metricsCollector);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        log.info("Endpoint chamado: /monitoring/health");
        return MonitorHealth.healthCheck(metricsCollector);
    }

    @GetMapping("/tracing/current")
    public ResponseEntity<Map<String, Object>> getCurrentTrace() {
        log.info("Endpoint chamado: /monitoring/tracing/current");
        return TraceMetrics.collectCurrentTrace();
    }

    @GetMapping("/tracing/endpoints")
    public ResponseEntity<Map<String, Object>> getEndpointTraces() {
        log.info("Endpoint chamado: /monitoring/tracing/endpoints");
        return TraceMetrics.collectEndpointTraces();
    }

    @PostMapping("/metrics/reset")
    public ResponseEntity<Map<String, Object>> resetMetrics() {
        log.info("Endpoint chamado: /monitoring/metrics/reset");
        return TraceMetrics.resetMetrics(metricsCollector);
    }
}