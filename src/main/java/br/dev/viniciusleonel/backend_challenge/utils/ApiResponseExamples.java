package br.dev.viniciusleonel.backend_challenge.utils;

public final class ApiResponseExamples {

    public static final String METRICS_EXAMPLE = """
            {
                "endpointMetrics": {
                    "/api/validate:GET": {
                        "minResponseTime": 2,
                        "maxResponseTime": 2,
                        "avgResponseTime": 2.0,
                        "requests": 2,
                        "errors": 0
                    }
                },
                "successRate": 50.0,
                "totalJwtValidations": 1,
                "validJwts": 1,
                "claimValidationErrors": {},
                "performanceMetrics": {
                    "totalResponses": 3,
                    "minResponseTime": 2,
                    "maxResponseTime": 2,
                    "avgResponseTime": 2.0
                },
                "invalidJwts": 0,
                "currentSpanId": "0c93a92c",
                "jwtValidationRate": 100.0,
                "totalRequests": 6,
                "successfulRequests": 3,
                "currentOperation": "root",
                "currentTraceId": "c83c43a942da48ec",
                "failedRequests": 0,
                "timestamp": "2025-08-14T22:37:47.732610800Z"
            }
        """;

    public static final String HEALTH_EXAMPLE = """
        {
          "traceId": "36e791a1e0674893",
          "spanId": "760302b3",
          "successRate": 47.05882352941176,
          "avgResponseTime": 42.125,
          "totalRequests": 17,
          "status": "UP",
          "timestamp": 1755210470060
        }
        """;

    public static final String CURRENT_TRACE_EXAMPLE = """
        {
          "traceId": "3f2ee910bd574e9a",
          "spanId": "628d9a08",
          "endpoint": "/monitoring/tracing/current",
          "requestId": "128f4e1b-6cb0-4451-8daa-cbfd0b9099c7",
          "operationName": "root",
          "timestamp": 1755211178652
        }
        """;

    public static final String RESET_METRICS_EXAMPLE = """
        {
          "traceId": "065e9b67546846e7",
          "spanId": "379448bf",
          "endpoint": "/monitoring/metrics/reset",
          "requestId": "8ecae05e-2509-43b1-bdf9-7bfb73ad405a",
          "message": "Metricas resetadas com sucesso",
          "timestamp": 1755211200114
        }
        """;

    public static final String ENDPOINTS_TRACE_EXAMPLE = """
        {
          "traceId": "efe1016c129b45f6",
          "endpoints": {
            "/api/validate": {
              "avgDuration": 45.2,
              "lastTrace": 1755211231643,
              "errorRate": 2.1,
              "totalTraces": 150
            }
          },
          "timestamp": 1755211231643
        }
        """;

    private ApiResponseExamples() {} // Construtor privado para classe utilitária
}