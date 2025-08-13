package br.dev.viniciusleonel.backend_challenge.infra.tracing;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class TraceContextTest {

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void shouldStartTraceWithUniqueIds() {
        // When
        TraceContext.startTrace();
        
        // Then
        String traceId = TraceContext.getCurrentTraceId();
        String spanId = TraceContext.getCurrentSpanId();
        String operationName = TraceContext.getCurrentOperationName();
        
        assertNotNull(traceId);
        assertNotNull(spanId);
        assertEquals("root", operationName);
        assertEquals(16, traceId.length());
        assertEquals(8, spanId.length());
    }

    @Test
    void shouldStartSpanWithParentRelationship() {
        // Given
        TraceContext.startTrace();
        String parentSpanId = TraceContext.getCurrentSpanId();
        
        // When
        TraceContext.startSpan("testOperation");
        
        // Then
        String currentSpanId = TraceContext.getCurrentSpanId();
        String operationName = TraceContext.getCurrentOperationName();
        
        assertNotEquals(parentSpanId, currentSpanId);
        assertEquals("testOperation", operationName);
    }

    @Test
    void shouldEndSpanAndReturnToParent() {
        // Given
        TraceContext.startTrace();
        String rootSpanId = TraceContext.getCurrentSpanId();
        TraceContext.startSpan("childOperation");
        String childSpanId = TraceContext.getCurrentSpanId();
        
        // When
        TraceContext.endSpan();
        
        // Then
        String currentSpanId = TraceContext.getCurrentSpanId();
        assertEquals(rootSpanId, currentSpanId);
        assertNotEquals(childSpanId, currentSpanId);
    }

    @Test
    void shouldEndTraceAndClearContext() {
        // Given
        TraceContext.startTrace();
        assertNotNull(TraceContext.getCurrentTraceId());
        
        // When
        TraceContext.endTrace();
        
        // Then
        assertNull(TraceContext.getCurrentTraceId());
        assertNull(TraceContext.getCurrentSpanId());
        assertNull(TraceContext.getCurrentOperationName());
    }

    @Test
    void shouldGenerateUniqueTraceIds() {
        // When
        TraceContext.startTrace();
        String traceId1 = TraceContext.getCurrentTraceId();
        
        TraceContext.endTrace();
        TraceContext.startTrace();
        String traceId2 = TraceContext.getCurrentTraceId();
        
        // Then
        assertNotEquals(traceId1, traceId2);
    }
} 