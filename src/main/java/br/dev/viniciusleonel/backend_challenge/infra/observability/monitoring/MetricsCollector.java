package br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring;

import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Component;

@Component
public class MetricsCollector {
    
    // Contadores de requisições
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong successfulRequests = new AtomicLong(0);
    private final AtomicLong failedRequests = new AtomicLong(0);
    
    // Métricas de performance
    private final ConcurrentHashMap<String, AtomicLong> endpointRequests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, AtomicLong> endpointErrors = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<Long>> responseTimes = new ConcurrentHashMap<>();
    
    // Métricas de JWT
    private final AtomicLong totalJwtValidations = new AtomicLong(0);
    private final AtomicLong validJwts = new AtomicLong(0);
    private final AtomicLong invalidJwts = new AtomicLong(0);
    
    // Métricas de claims
    private final ConcurrentHashMap<String, AtomicLong> claimValidationErrors = new ConcurrentHashMap<>();
    
    private final ConcurrentHashMap<String, Object> aggregatedMetrics = new ConcurrentHashMap<>();

    public void recordRequest(String endpoint, String method) {
        if (endpoint != null && method != null) {
            totalRequests.incrementAndGet();
            String key = endpoint + ":" + method;
            endpointRequests.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
        }
    }
    
    public void recordSuccessfulRequest(String endpoint, String method, Long responseTime) {
        totalRequests.incrementAndGet();
        successfulRequests.incrementAndGet();
        
        String key = endpoint + ":" + method;
        endpointRequests.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
        
        if (responseTime != null) {
            responseTimes.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).offer(responseTime);
            
            // Mantém apenas os últimos 1000 tempos de resposta para cada endpoint
            ConcurrentLinkedQueue<Long> times = responseTimes.get(key);
            if (times != null && times.size() > 1000) {
                times.poll();
            }
        }
        
        updatePerformanceMetrics();
    }
    
    public void recordFailedRequest(String endpoint, String method, Long responseTime, String errorType) {
        totalRequests.incrementAndGet();
        failedRequests.incrementAndGet();
        
        String key = endpoint + ":" + method;
        endpointRequests.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
        endpointErrors.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
        
        if (responseTime != null) {
            responseTimes.computeIfAbsent(key, k -> new ConcurrentLinkedQueue<>()).offer(responseTime);
            
            // Mantém apenas os últimos 1000 tempos de resposta para cada endpoint
            ConcurrentLinkedQueue<Long> times = responseTimes.get(key);
            if (times != null && times.size() > 1000) {
                times.poll();
            }
        }
        
        updatePerformanceMetrics();
    }
    
    public void recordJwtValidation(boolean isValid) {
        totalJwtValidations.incrementAndGet();
        if (isValid) {
            validJwts.incrementAndGet();
        } else {
            invalidJwts.incrementAndGet();
        }
    }
    
    public void recordClaimValidationError(String claimType, String error) {
        if (claimType != null && error != null) {
            String key = claimType + ":" + error;
            claimValidationErrors.computeIfAbsent(key, k -> new AtomicLong(0)).incrementAndGet();
        }
    }
    
    private void updatePerformanceMetrics() {
        long total = totalRequests.get();
        long successful = successfulRequests.get();
        
        if (total > 0) {
            double successRate = (double) successful / total * 100.0;
            aggregatedMetrics.put("successRate", successRate);
        }
    }
    
    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new ConcurrentHashMap<>();
        
        try {
            // Métricas gerais
            metrics.put("totalRequests", totalRequests.get());
            metrics.put("successfulRequests", successfulRequests.get());
            metrics.put("failedRequests", failedRequests.get());
            
            // Calcular taxa de sucesso em tempo real
            long total = totalRequests.get();
            long successful = successfulRequests.get();
            double successRate = total > 0 ? (double) successful / total * 100.0 : 0.0;
            metrics.put("successRate", successRate);
            
            // Métricas de JWT
            metrics.put("totalJwtValidations", totalJwtValidations.get());
            metrics.put("validJwts", validJwts.get());
            metrics.put("invalidJwts", invalidJwts.get());
            
            // Calcular taxa de validação JWT em tempo real
            long totalJwt = totalJwtValidations.get();
            long validJwt = validJwts.get();
            double jwtValidationRate = totalJwt > 0 ? (double) validJwt / totalJwt * 100.0 : 0.0;
            metrics.put("jwtValidationRate", jwtValidationRate);
            
            // Métricas por endpoint
            metrics.put("endpointMetrics", getEndpointMetrics());
            
            // Métricas de claims
            metrics.put("claimValidationErrors", getClaimValidationErrors());
            
            // Métricas de performance
            metrics.put("performanceMetrics", getPerformanceMetrics());
            
            metrics.put("timestamp", Instant.now());
            
        } catch (Exception e) {
            // Em caso de erro, retorna metricas basicas
            metrics.put("error", "Erro ao coletar metricas: " + e.getMessage());
            metrics.put("timestamp", Instant.now());
        }
        
        return metrics;
    }
    
    private double calculateSuccessRate() {
        long total = totalRequests.get();
        return total > 0 ? (double) successfulRequests.get() / total * 100 : 0.0;
    }
    
    private double calculateJwtValidationRate() {
        long total = totalJwtValidations.get();
        return total > 0 ? (double) validJwts.get() / total * 100 : 0.0;
    }
    
    private Map<String, Object> getEndpointMetrics() {
        Map<String, Object> endpointMetrics = new ConcurrentHashMap<>();
        
        try {
            endpointRequests.forEach((endpoint, count) -> {
                Map<String, Object> endpointData = new ConcurrentHashMap<>();
                endpointData.put("requests", count.get());
                
                AtomicLong errorCount = endpointErrors.get(endpoint);
                endpointData.put("errors", errorCount != null ? errorCount.get() : 0);
                
                ConcurrentLinkedQueue<Long> times = responseTimes.get(endpoint);
                if (times != null && !times.isEmpty()) {
                    endpointData.put("avgResponseTime", calculateAverageResponseTime(times));
                    endpointData.put("minResponseTime", times.stream().mapToLong(Long::longValue).min().orElse(0));
                    endpointData.put("maxResponseTime", times.stream().mapToLong(Long::longValue).max().orElse(0));
                }
                
                endpointMetrics.put(endpoint, endpointData);
            });
        } catch (Exception e) {
            endpointMetrics.put("error", "Erro ao coletar metricas de endpoint: " + e.getMessage());
        }
        
        return endpointMetrics;
    }
    
    private Map<String, Object> getClaimValidationErrors() {
        Map<String, Object> claimErrors = new ConcurrentHashMap<>();
        
        try {
            claimValidationErrors.forEach((claimError, count) -> {
                claimErrors.put(claimError, count.get());
            });
        } catch (Exception e) {
            claimErrors.put("error", "Erro ao coletar erros de claims: " + e.getMessage());
        }
        
        return claimErrors;
    }
    
    private Map<String, Object> getPerformanceMetrics() {
        Map<String, Object> performance = new ConcurrentHashMap<>();
        
        try {
            // Calcula métricas de performance agregadas
            long totalResponseTime = 0;
            long totalResponses = 0;
            long minResponseTime = Long.MAX_VALUE;
            long maxResponseTime = 0;
            
            for (ConcurrentLinkedQueue<Long> times : responseTimes.values()) {
                if (times != null) {
                    for (Long time : times) {
                        if (time != null) {
                            totalResponseTime += time;
                            totalResponses++;
                            minResponseTime = Math.min(minResponseTime, time);
                            maxResponseTime = Math.max(maxResponseTime, time);
                        }
                    }
                }
            }
            
            performance.put("totalResponses", totalResponses);
            performance.put("avgResponseTime", totalResponses > 0 ? (double) totalResponseTime / totalResponses : 0.0);
            performance.put("minResponseTime", minResponseTime == Long.MAX_VALUE ? 0 : minResponseTime);
            performance.put("maxResponseTime", maxResponseTime);
            
        } catch (Exception e) {
            performance.put("error", "Erro ao calcular metricas de performance: " + e.getMessage());
        }
        
        return performance;
    }
    
    private double calculateAverageResponseTime(ConcurrentLinkedQueue<Long> times) {
        try {
            return times.stream()
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    public void resetMetrics() {
        try {
            totalRequests.set(0);
            successfulRequests.set(0);
            failedRequests.set(0);
            totalJwtValidations.set(0);
            validJwts.set(0);
            invalidJwts.set(0);
            
            endpointRequests.clear();
            endpointErrors.clear();
            responseTimes.clear();
            claimValidationErrors.clear();
        } catch (Exception e) {
            // Log do erro mas nao falha a operacao
            System.err.println("Erro ao resetar metricas: " + e.getMessage());
        }
    }
}
