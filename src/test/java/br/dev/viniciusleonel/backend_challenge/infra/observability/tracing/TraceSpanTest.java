package br.dev.viniciusleonel.backend_challenge.infra.observability.tracing;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

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
        try (TraceSpan span = new TraceSpan("testOperation")) {
            assertEquals("testOperation", TraceContext.getCurrentOperationName());
        } // span.close() é chamado automaticamente
    }

    @Test
    void shouldAddTagsCorrectly() {
        try (TraceSpan span = new TraceSpan("testOperation")) {
            span.addTag("key1", "value1");
            span.addTag("key2", "value2");

            Map<String, String> tags = span.getTags();
            assertEquals("value1", tags.get("key1"));
            assertEquals("value2", tags.get("key2"));
        } // span.close() é chamado automaticamente
    }

    @Test
    void shouldAddMetricsCorrectly() {
        try (TraceSpan span = new TraceSpan("testOperation")) {
            span.addMetric("duration", 100L);
            span.addMetric("count", 5);

            Map<String, Object> metrics = span.getMetrics();
            assertEquals(100L, metrics.get("duration"));
            assertEquals(5, metrics.get("count"));
        }
    }

    @Test
    void shouldAddBusinessContext() {
        try (TraceSpan span = new TraceSpan("testOperation")) {
            span.addBusinessContext("userId", "12345");
            span.addBusinessContext("operation", "validation");

            Map<String, String> tags = span.getTags();
            assertEquals("12345", tags.get("business.userId"));
            assertEquals("validation", tags.get("business.operation"));
        }
    }

    @Test
    void shouldAddErrorContext() {
        try (TraceSpan span = new TraceSpan("testOperation")) {
            span.addError("Validation failed");

            Map<String, String> tags = span.getTags();
            assertEquals("Validation failed", tags.get("error"));
        }
    }

    @Test
    void shouldCloseSpanAndEndTrace() {
        try (TraceSpan span = new TraceSpan("testOperation")) {
            String spanId = TraceContext.getCurrentSpanId();

            span.close(); // Obrigado a fechar para o teste

            assertNotEquals(spanId, TraceContext.getCurrentSpanId());
        }

    }
} 