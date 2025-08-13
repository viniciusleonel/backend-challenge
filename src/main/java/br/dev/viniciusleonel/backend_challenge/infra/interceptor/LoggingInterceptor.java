package br.dev.viniciusleonel.backend_challenge.infra.interceptor;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import br.dev.viniciusleonel.backend_challenge.infra.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.tracing.TraceContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(LoggingInterceptor.class);
    private final MetricsCollector metricsCollector;

    public LoggingInterceptor(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        
        // Adiciona contexto unico para esta requisicao
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("endpoint", request.getRequestURI());
        MDC.put("method", request.getMethod());
        MDC.put("startTime", String.valueOf(startTime));
        
        // Inicia o tracing - IMPORTANTE: sempre inicializa
        TraceContext.startTrace();
        
        // Log para debug
        log.debug("Tracing iniciado para: {} {} [requestId: {}, traceId: {}]", 
                 request.getMethod(), request.getRequestURI(), 
                 requestId, TraceContext.getCurrentTraceId());
        
        // Registra metricas
        metricsCollector.recordRequest(request.getRequestURI(), request.getMethod());
        
        // Armazena startTime para uso posterior
        request.setAttribute("startTime", startTime);
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long responseTime = endTime - startTime;
        
        // Adiciona informacoes de resposta
        MDC.put("statusCode", String.valueOf(response.getStatus()));
        MDC.put("responseTime", String.valueOf(responseTime));
        MDC.put("duration", String.valueOf(responseTime));
        
        // Log para debug
        log.debug("Finalizando tracing para: {} {} [traceId: {}, duracao: {}ms]", 
                 request.getMethod(), request.getRequestURI(), 
                 TraceContext.getCurrentTraceId(), responseTime);
        
        // Registra metricas de performance
        if (response.getStatus() >= 200 && response.getStatus() < 400) {
            metricsCollector.recordSuccessfulRequest(request.getRequestURI(), request.getMethod(), responseTime);
        } else {
            String errorType = ex != null ? ex.getClass().getSimpleName() : "HTTP_" + response.getStatus();
            metricsCollector.recordFailedRequest(request.getRequestURI(), request.getMethod(), responseTime, errorType);
        }
        
        // Finaliza o tracing
        TraceContext.endTrace();
        
        // Sempre limpa o contexto ao final da requisicao
        MDC.clear();
    }
}