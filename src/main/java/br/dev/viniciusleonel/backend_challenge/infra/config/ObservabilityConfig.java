package br.dev.viniciusleonel.backend_challenge.infra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;

@Configuration
@EnableScheduling
public class ObservabilityConfig {

    private final MetricsCollector metricsCollector;

    public ObservabilityConfig(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    // Limpa métricas antigas a cada hora
    @Scheduled(fixedRate = 3600000) // 1 hora
    public void cleanupOldMetrics() {
        // Implementação para limpeza de métricas antigas
    }

    // Log de métricas a cada 5 minutos
    @Scheduled(fixedRate = 300000) // 5 minutos
    public void logMetrics() {
        // Log das métricas atuais para monitoramento
    }
}