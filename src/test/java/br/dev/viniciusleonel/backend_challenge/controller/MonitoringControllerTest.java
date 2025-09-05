package br.dev.viniciusleonel.backend_challenge.controller;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;

import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceContext;

import java.util.Map;

class MonitoringControllerTest {

    private MonitoringController controller;
    private MetricsCollector metricsCollector;

    @BeforeEach
    void setUp() {
        metricsCollector = new MetricsCollector();
        controller = new MonitoringController(metricsCollector);
        TraceContext.startTrace();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldReturnMetricsWithTraceContext() {
        ResponseEntity<Map<String, Object>> response = controller.getMetrics();
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.get("currentTraceId"));
        assertNotNull(body.get("currentSpanId"));
    }

    @Test
    void shouldReturnHealthWithTraceContext() {
        ResponseEntity<Map<String, Object>> response = controller.health();
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("UP", body.get("status"));
        assertNotNull(body.get("traceId"));
    }

    @Test
    void shouldReturnCurrentTrace() {
        ResponseEntity<Map<String, Object>> response = controller.getCurrentTrace();
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.get("traceId"));
        assertNotNull(body.get("spanId"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void shouldResetMetrics() {
        metricsCollector.recordRequest("/api/validate", "GET");
        
        ResponseEntity<Map<String, Object>> response = controller.resetMetrics();
        
        assertTrue(response.getStatusCode().is2xxSuccessful());
        Map<String, Object> body = response.getBody();
        assertEquals("Metricas resetadas com sucesso", body.get("message"));
        assertNotNull(body.get("traceId"));
    }
} 