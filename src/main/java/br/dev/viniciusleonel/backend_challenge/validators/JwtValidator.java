package br.dev.viniciusleonel.backend_challenge.validators;

import java.util.List;

import com.auth0.jwt.exceptions.JWTDecodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.auth0.jwt.interfaces.DecodedJWT;

import br.dev.viniciusleonel.backend_challenge.utils.JwtDecoder;

@Component
public class JwtValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtValidator.class);
    private final List<ClaimValidator> validators;

    public JwtValidator() {
        this.validators = JwtValidationConfig.getValidators();
    }

    public boolean isValid(String token) {
        log.info("Iniciando validacao do JWT");

        if (token == null || token.trim().isEmpty()) {
            throw new JWTDecodeException("Token nulo ou vazio");
        }

        DecodedJWT jwt = JwtDecoder.decode(token);
        return validateAllClaims(jwt) && hasValidClaimsCount(jwt);
    }

    private boolean hasValidClaimsCount(DecodedJWT jwt) {
        int claimsCount = jwt.getClaims().size();
        int expectedCount = validators.size();
        
        if (claimsCount != expectedCount) {
            log.error("Total de claims invalido: esperado {}, encontrado {}", expectedCount, claimsCount);
            return false;
        }
        
        log.info("Total de claims valido: {}", claimsCount);
        return true;
    }

    private boolean validateAllClaims(DecodedJWT jwt) {
        log.info("Chamando validadores de claims");
        
        for (ClaimValidator validator : validators) {
            validator.validate(jwt);
        }

        log.info("JWT passou nas validacoes");
        return true;
    }
}