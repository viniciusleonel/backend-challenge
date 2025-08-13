package br.dev.viniciusleonel.backend_challenge.infra.tracing;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TraceContext {
    private static final Logger log = LoggerFactory.getLogger(TraceContext.class);
    
    public static final String TRACE_ID = "traceId";
    public static final String SPAN_ID = "spanId";
    public static final String PARENT_SPAN_ID = "parentSpanId";
    public static final String OPERATION_NAME = "operationName";
    
    public static void startTrace() {
        String traceId = generateTraceId();
        String spanId = generateSpanId();
        
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);
        MDC.put(PARENT_SPAN_ID, "");
        MDC.put(OPERATION_NAME, "root");
        
        log.debug("Trace iniciado: traceId={}, spanId={}", traceId, spanId);
    }
    
    public static void startSpan(String operationName) {
        String parentSpanId = MDC.get(SPAN_ID);
        String spanId = generateSpanId();
        
        MDC.put(PARENT_SPAN_ID, parentSpanId);
        MDC.put(SPAN_ID, spanId);
        MDC.put(OPERATION_NAME, operationName);
        
        log.debug("Span iniciado: operationName={}, spanId={}, parentSpanId={}", 
                 operationName, spanId, parentSpanId);
    }
    
    public static void endSpan() {
        String parentSpanId = MDC.get(PARENT_SPAN_ID);
        if (parentSpanId != null && !parentSpanId.isEmpty()) {
            MDC.put(SPAN_ID, parentSpanId);
            MDC.put(PARENT_SPAN_ID, "");
            MDC.put(OPERATION_NAME, "parent");
            
            log.debug("Span finalizado, voltando para parent: spanId={}", parentSpanId);
        }
    }
    
    public static void endTrace() {
        String traceId = MDC.get(TRACE_ID);
        log.debug("Trace finalizado: traceId={}", traceId);
        
        MDC.remove(TRACE_ID);
        MDC.remove(SPAN_ID);
        MDC.remove(PARENT_SPAN_ID);
        MDC.remove(OPERATION_NAME);
    }
    
    private static String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
    
    private static String generateSpanId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }
    
    public static String getCurrentTraceId() {
        String traceId = MDC.get(TRACE_ID);
        log.debug("getCurrentTraceId() retornou: {}", traceId);
        return traceId;
    }
    
    public static String getCurrentSpanId() {
        String spanId = MDC.get(SPAN_ID);
        log.debug("getCurrentSpanId() retornou: {}", spanId);
        return spanId;
    }
    
    public static String getCurrentOperationName() {
        String operationName = MDC.get(OPERATION_NAME);
        log.debug("getCurrentOperationName() retornou: {}", operationName);
        return operationName;
    }
}