package br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring;

import static org.junit.jupiter.api.Assertions.assertEquals;

import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class MetricsCollectorTest {

    private MetricsCollector metricsCollector;

    @BeforeEach
    void setUp() {
        metricsCollector = new MetricsCollector();
    }

    @Test
    void shouldRecordRequestCorrectly() {
        // When
        metricsCollector.recordRequest("/api/validate", "GET");
        
        // Then
        Map<String, Object> metrics = metricsCollector.getMetrics();
        assertEquals(1L, metrics.get("totalRequests"));
    }

    @Test
    void shouldRecordSuccessfulRequest() {
        // When
        metricsCollector.recordSuccessfulRequest("/api/validate", "GET", 100L);
        
        // Then
        Map<String, Object> metrics = metricsCollector.getMetrics();
        assertEquals(1L, metrics.get("successfulRequests"));
        assertEquals(100.0, metrics.get("successRate"));
    }

    @Test
    void shouldRecordFailedRequest() {
        // When
        metricsCollector.recordFailedRequest("/api/validate", "GET", 150L, "ValidationError");
        
        // Then
        Map<String, Object> metrics = metricsCollector.getMetrics();
        assertEquals(1L, metrics.get("failedRequests"));
        assertEquals(0.0, metrics.get("successRate"));
    }

    @Test
    void shouldRecordJwtValidation() {
        // When
        metricsCollector.recordJwtValidation(true);
        metricsCollector.recordJwtValidation(false);
        
        // Then
        Map<String, Object> metrics = metricsCollector.getMetrics();
        assertEquals(2L, metrics.get("totalJwtValidations"));
        assertEquals(1L, metrics.get("validJwts"));
        assertEquals(1L, metrics.get("invalidJwts"));
        assertEquals(50.0, metrics.get("jwtValidationRate"));
    }

    @Test
    void shouldRecordClaimValidationError() {
        // When
        metricsCollector.recordClaimValidationError("Name", "contains_numbers");
        metricsCollector.recordClaimValidationError("Role", "invalid_role");
        
        // Then
        Map<String, Object> metrics = metricsCollector.getMetrics();
        Map<String, Object> claimErrors = (Map<String, Object>) metrics.get("claimValidationErrors");
        
        assertEquals(1L, claimErrors.get("Name:contains_numbers"));
        assertEquals(1L, claimErrors.get("Role:invalid_role"));
    }

    @Test
    void shouldCalculatePerformanceMetrics() {
        // Given
        metricsCollector.recordSuccessfulRequest("/api/validate", "GET", 50L);
        metricsCollector.recordSuccessfulRequest("/api/validate", "GET", 100L);
        metricsCollector.recordSuccessfulRequest("/api/validate", "GET", 150L);
        
        // When
        Map<String, Object> metrics = metricsCollector.getMetrics();
        Map<String, Object> performance = (Map<String, Object>) metrics.get("performanceMetrics");
        
        // Then
        assertEquals(3L, performance.get("totalResponses"));
        assertEquals(100.0, performance.get("avgResponseTime"));
        assertEquals(50L, performance.get("minResponseTime"));
        assertEquals(150L, performance.get("maxResponseTime"));
    }

    @Test
    void shouldResetMetrics() {
        // Given
        metricsCollector.recordRequest("/api/validate", "GET");
        metricsCollector.recordJwtValidation(true);
        
        // When
        metricsCollector.resetMetrics();
        
        // Then
        Map<String, Object> metrics = metricsCollector.getMetrics();
        assertEquals(0L, metrics.get("totalRequests"));
        assertEquals(0L, metrics.get("totalJwtValidations"));
    }
} 