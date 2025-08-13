package br.dev.viniciusleonel.backend_challenge.validators;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.interfaces.DecodedJWT;

import br.dev.viniciusleonel.backend_challenge.infra.tracing.TraceSpan;
import br.dev.viniciusleonel.backend_challenge.utils.JwtDecoder;

public class JwtValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtValidator.class);
    private static final List<ClaimValidator> validators = JwtValidationConfig.getValidators();

    public static boolean isValid(String token) {
        try (TraceSpan span = new TraceSpan("JwtValidation")) {
            span.addTag("totalValidators", String.valueOf(validators.size()));
            span.addBusinessContext("operation", "jwt_validation");
            
            log.info("Iniciando validacao do JWT");

            DecodedJWT jwt;
            try (TraceSpan decodeSpan = new TraceSpan("JwtDecode")) {
                jwt = JwtDecoder.decode(token);
                decodeSpan.addTag("claimsCount", String.valueOf(jwt.getClaims().size()));
            }

            log.debug("Verificando total de claims");
            if (jwt.getClaims().size() != validators.size()) {
                span.addError("Total de claims invalido");
                log.error("Total de claims invalido: {}", jwt.getClaims().size());
                return false;
            }

            log.info("Total de claims valido: {}", jwt.getClaims().size());
            log.info("Chamando validadores de claims");
            
            for (ClaimValidator validator : validators) {
                try (TraceSpan validatorSpan = new TraceSpan("Validator_" + validator.getClass().getSimpleName())) {
                    validator.validate(jwt);
                    validatorSpan.addTag("validator", validator.getClass().getSimpleName());
                }
            }

            log.info("JWT passou nas validacoes");
            return true;
        }
    }
}