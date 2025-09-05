package br.dev.viniciusleonel.backend_challenge.infra.observability.tracing;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

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
        TraceContext.startTrace();

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
        TraceContext.startTrace();
        String parentSpanId = TraceContext.getCurrentSpanId();

        TraceContext.startSpan("testOperation");

        String currentSpanId = TraceContext.getCurrentSpanId();
        String operationName = TraceContext.getCurrentOperationName();

        assertNotEquals(parentSpanId, currentSpanId);
        assertEquals("testOperation", operationName);
    }

    @Test
    void shouldEndSpanAndReturnToParent() {
        TraceContext.startTrace();
        String rootSpanId = TraceContext.getCurrentSpanId();
        TraceContext.startSpan("childOperation");
        String childSpanId = TraceContext.getCurrentSpanId();

        TraceContext.endSpan();

        String currentSpanId = TraceContext.getCurrentSpanId();
        assertEquals(rootSpanId, currentSpanId);
        assertNotEquals(childSpanId, currentSpanId);
    }

    @Test
    void shouldEndTraceAndClearContext() {
        TraceContext.startTrace();
        assertNotNull(TraceContext.getCurrentTraceId());

        TraceContext.endTrace();

        assertNull(TraceContext.getCurrentTraceId());
        assertNull(TraceContext.getCurrentSpanId());
        assertNull(TraceContext.getCurrentOperationName());
    }

    @Test
    void shouldGenerateUniqueTraceIds() {
        TraceContext.startTrace();
        String traceId1 = TraceContext.getCurrentTraceId();

        TraceContext.endTrace();
        TraceContext.startTrace();
        String traceId2 = TraceContext.getCurrentTraceId();

        assertNotEquals(traceId1, traceId2);
    }
} 