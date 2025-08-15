package br.dev.viniciusleonel.backend_challenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.dev.viniciusleonel.backend_challenge.infra.observability.monitoring.MetricsCollector;
import br.dev.viniciusleonel.backend_challenge.infra.observability.tracing.TraceSpan;
import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "JWT Validation", description = "Endpoints para validação de tokens JWT")
public class ApiController {

    private final MetricsCollector metricsCollector;
    private final JwtValidator jwtValidator;

    public ApiController(MetricsCollector metricsCollector, JwtValidator jwtValidator) {
        this.metricsCollector = metricsCollector;
        this.jwtValidator = jwtValidator;
    }

    @GetMapping("/validate")
    // Documentação Swagger
    @Operation(
        summary = "Validar JWT",
        description = "Valida um token JWT e retorna se é válido ou não"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Token válido",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Boolean.class),
                examples = @ExampleObject(value = "true", summary = "Token válido")
            )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "Token inválido",
                content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class),
                    examples = @ExampleObject(value = "false", summary = "Token válido")
                )
        ),
        @ApiResponse(
                responseCode = "422",
                description = "Claim inválida",
                    content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Boolean.class),
                    examples = @ExampleObject(value = "false", summary = "Token válido")
            )
        )
    })
    public ResponseEntity<Boolean> validateJwt(
        // Documentação Swagger
        @Parameter(
            description = "Token JWT a ser validado", 
            required = true,
            examples = {
                @ExampleObject(
                    name = "Token Válido",
                    summary = "Exemplo de token válido",
                    value = "eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg"
                ),
                @ExampleObject(
                    name = "Token Inválido",
                    summary = "Exemplo de token inválido",
                    value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.token.here"
                ),
                @ExampleObject(
                    name = "Token com Claim Inválido",
                    summary = "Exemplo de token com claim inválido",
                    value = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJyb2xlIjoiaW52YWxpZCIsInNlZWQiOiIxMjM0NTY3ODkwIn0.invalid_signature"
                )
            }
        )
        @RequestParam String token
    ) {
        try (TraceSpan span = new TraceSpan("validateJwt")) {
            span.addTag("tokenLength", String.valueOf(token.length()));
            span.addBusinessContext("operation", "jwt_validation");
            
            boolean isValid = jwtValidator.isValid(token);
            metricsCollector.recordJwtValidation(isValid);
            
            return isValid ? ResponseEntity.ok(true) : ResponseEntity.badRequest().body(false);
        }
    }
}