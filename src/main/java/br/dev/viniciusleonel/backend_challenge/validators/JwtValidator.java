package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.controller.ApiController;
import br.dev.viniciusleonel.backend_challenge.utils.JwtDecoder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class JwtValidator {

    private static final Logger log = LoggerFactory.getLogger(JwtValidator.class);

    // Busca a lista de validadores, mantendo o princípio 'Open Closed'
    private static final List<ClaimValidator> validators = JwtValidationConfig.getValidators();

    public static boolean isValid(String token) {

            log.info("Iniciando validacao do JWT");

            DecodedJWT jwt = JwtDecoder.decode(token);

            log.debug("Verificando total de claims");
            // Valida o total de claims presentes no JWT
            if (jwt.getClaims().size() != validators.size()) {
                log.error("Total de claims invalido: {}", jwt.getClaims().size());
                return false;
            }

            log.info("Total de claims valido: {}", jwt.getClaims().size());
            log.info("Chamando validadores de claims");
            // Chama o metodo 'validate' de cada validador da lista
            for (ClaimValidator validator : validators) {
                validator.validate(jwt);
            }

            log.info("JWT passou nas validacoes");
            return true;
    }
}
