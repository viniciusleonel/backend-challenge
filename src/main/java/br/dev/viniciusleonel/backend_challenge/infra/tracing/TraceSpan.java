package br.dev.viniciusleonel.backend_challenge.infra.tracing;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class TraceSpan implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(TraceSpan.class);
    
    private final String operationName;
    private final Instant startTime;
    private final String spanId;
    private final String parentSpanId;
    private final Map<String, String> tags;
    private final Map<String, Object> metrics;
    
    public TraceSpan(String operationName) {
        this.operationName = operationName;
        this.startTime = Instant.now();
        this.spanId = MDC.get(TraceContext.SPAN_ID);
        this.parentSpanId = MDC.get(TraceContext.PARENT_SPAN_ID);
        this.tags = new HashMap<>();
        this.metrics = new HashMap<>();
        
        TraceContext.startSpan(operationName);
        
        log.debug("Span iniciado: {} [spanId: {}, parentSpanId: {}]", 
                 operationName, spanId, parentSpanId);
    }
    
    @Override
    public void close() {
        Instant endTime = Instant.now();
        long durationMs = endTime.toEpochMilli() - startTime.toEpochMilli();
        
        // Adiciona métricas de performance
        addMetric("duration", durationMs);
        addMetric("startTime", startTime.toEpochMilli());
        addMetric("endTime", endTime.toEpochMilli());
        
        TraceContext.endSpan();
        
        // Log de finalizacao com metricas
        log.debug("Span finalizado: {} [duracao: {}ms, spanId: {}, tags: {}, metrics: {}]", 
                 operationName, durationMs, spanId, tags, metrics);
    }
    
    public void addTag(String key, String value) {
        if (key != null && value != null) {
            tags.put(key, value);
            MDC.put("tag." + key, value);
        }
    }
    
    public void addMetric(String key, Object value) {
        if (key != null && value != null) {
            metrics.put(key, value);
            MDC.put("metric." + key, value.toString());
        }
    }
    
    public void addError(String error) {
        if (error != null) {
            addTag("error", error);
            MDC.put("error", error);
        }
    }
    
    public void addBusinessContext(String key, String value) {
        if (key != null && value != null) {
            addTag("business." + key, value);
        }
    }
    
    public void addSecurityContext(String key, String value) {
        if (key != null && value != null) {
            addTag("security." + key, value);
        }
    }
    
    public Map<String, String> getTags() {
        return new HashMap<>(tags);
    }
    
    public Map<String, Object> getMetrics() {
        return new HashMap<>(metrics);
    }
}