package br.dev.viniciusleonel.backend_challenge.infra.observability.tracing;

import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceContext;
import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceSpan;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import java.util.Map;

class TraceSpanTest {

    @BeforeEach
    void setUp() {
        MDC.clear();
        TraceContext.startTrace();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldCreateSpanWithOperationName() {
        // When
        TraceSpan span = new TraceSpan("testOperation");
        
        // Then
        assertEquals("testOperation", TraceContext.getCurrentOperationName());
        span.close();
    }

    @Test
    void shouldAddTagsCorrectly() {
        // Given
        TraceSpan span = new TraceSpan("testOperation");
        
        // When
        span.addTag("key1", "value1");
        span.addTag("key2", "value2");
        
        // Then
        Map<String, String> tags = span.getTags();
        assertEquals("value1", tags.get("key1"));
        assertEquals("value2", tags.get("key2"));
        
        span.close();
    }

    @Test
    void shouldAddMetricsCorrectly() {
        // Given
        TraceSpan span = new TraceSpan("testOperation");
        
        // When
        span.addMetric("duration", 100L);
        span.addMetric("count", 5);
        
        // Then
        Map<String, Object> metrics = span.getMetrics();
        assertEquals(100L, metrics.get("duration"));
        assertEquals(5, metrics.get("count"));
        
        span.close();
    }

    @Test
    void shouldAddBusinessContext() {
        // Given
        TraceSpan span = new TraceSpan("testOperation");
        
        // When
        span.addBusinessContext("userId", "12345");
        span.addBusinessContext("operation", "validation");
        
        // Then
        Map<String, String> tags = span.getTags();
        assertEquals("12345", tags.get("business.userId"));
        assertEquals("validation", tags.get("business.operation"));
        
        span.close();
    }

    @Test
    void shouldAddErrorContext() {
        // Given
        TraceSpan span = new TraceSpan("testOperation");
        
        // When
        span.addError("Validation failed");
        
        // Then
        Map<String, String> tags = span.getTags();
        assertEquals("Validation failed", tags.get("error"));
        
        span.close();
    }

    @Test
    void shouldCloseSpanAndEndTrace() {
        // Given
        TraceSpan span = new TraceSpan("testOperation");
        String spanId = TraceContext.getCurrentSpanId();
        
        // When
        span.close();
        
        // Then
        assertNotEquals(spanId, TraceContext.getCurrentSpanId());
    }
} 