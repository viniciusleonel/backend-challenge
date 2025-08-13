package br.dev.viniciusleonel.backend_challenge.infra.interceptor;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Adiciona contexto único para esta requisição
        String requestId = UUID.randomUUID().toString();
        MDC.put("requestId", requestId);
        MDC.put("endpoint", request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // Sempre limpa o contexto ao final da requisição
        MDC.clear();
    }
}