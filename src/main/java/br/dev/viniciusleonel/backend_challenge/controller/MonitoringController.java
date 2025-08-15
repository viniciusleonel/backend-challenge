package br.dev.viniciusleonel.backend_challenge.controller;

import java.util.Map;

import br.dev.viniciusleonel.backend_challenge.utils.ApiResponseExamples;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MonitorHealth;
import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceMetrics;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/monitoring")
@Tag(name = "Monitoring", description = "Endpoints para monitoramento e observabilidade")
public class MonitoringController {

    private final MetricsCollector metricsCollector;
    private static final Logger log = LoggerFactory.getLogger(MonitoringController.class);

    public MonitoringController(MetricsCollector metricsCollector) {
        this.metricsCollector = metricsCollector;
    }

    @GetMapping("/metrics")
    @Operation(
        summary = "Obter métricas",
        description = "Retorna as métricas coletadas pelo sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Métricas retornadas com sucesso",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = ApiResponseExamples.METRICS_EXAMPLE,
                summary = "Exemplo de métricas"
            )
        )
    )
    public ResponseEntity<Map<String, Object>> getMetrics() {
        log.info("Endpoint chamado: /monitoring/metrics");
        return TraceMetrics.collectMetrics(metricsCollector);
    }

    @GetMapping("/health")
    @Operation(
        summary = "Verificar saúde",
        description = "Verifica a saúde do sistema"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Sistema saudável",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = ApiResponseExamples.HEALTH_EXAMPLE,
                summary = "Exemplo de saúde do sistema"
            )
        )
    )
    public ResponseEntity<Map<String, Object>> health() {
        log.info("Endpoint chamado: /monitoring/health");
        return MonitorHealth.healthCheck(metricsCollector);
    }

    @GetMapping("/tracing/current")
    @Operation(
        summary = "Rastreamento atual",
        description = "Retorna informações sobre o rastreamento atual"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Informações de rastreamento retornadas",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = ApiResponseExamples.CURRENT_TRACE_EXAMPLE,
                summary = "Exemplo de rastreamento atual"
            )
        )
    )
    public ResponseEntity<Map<String, Object>> getCurrentTrace() {
        log.info("Endpoint chamado: /monitoring/tracing/current");
        return TraceMetrics.collectCurrentTrace();
    }

    @GetMapping("/tracing/endpoints")
    @Operation(
        summary = "Rastreamento de endpoints",
        description = "Retorna informações sobre o rastreamento dos endpoints"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Informações de rastreamento dos endpoints retornadas",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = ApiResponseExamples.ENDPOINTS_TRACE_EXAMPLE,
                summary = "Exemplo de rastreamento de endpoints"
            )
        )
    )
    public ResponseEntity<Map<String, Object>> getEndpointTraces() {
        log.info("Endpoint chamado: /monitoring/tracing/endpoints");
        return TraceMetrics.collectEndpointTraces();
    }

    @PostMapping("/metrics/reset")
    @Operation(
        summary = "Resetar métricas",
        description = "Reseta todas as métricas coletadas"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Métricas resetadas com sucesso",
        content = @Content(
            mediaType = "application/json",
            examples = @ExampleObject(
                value = ApiResponseExamples.RESET_METRICS_EXAMPLE,
                summary = "Exemplo de reset de métricas"
            )
        )
    )
    public ResponseEntity<Map<String, Object>> resetMetrics() {
        log.info("Endpoint chamado: /monitoring/metrics/reset");
        return TraceMetrics.resetMetrics(metricsCollector);
    }
}