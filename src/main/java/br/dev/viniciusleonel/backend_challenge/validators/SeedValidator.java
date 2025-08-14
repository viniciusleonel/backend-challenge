package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.NumberUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SeedValidator implements ClaimValidator{

    private static final Logger log = LoggerFactory.getLogger(SeedValidator.class);

    // Valida se o JWT contém a claim 'Seed', se é um número inteiro e se é primo
    @Override
    public boolean validate(DecodedJWT jwt) {
        log.info("Iniciando validacao da claim Seed");
        String seedStr = jwt.getClaim("Seed").asString();
        if (seedStr == null || seedStr.isEmpty()) {
            log.error("Seed nulo ou vazio: {}", seedStr);
            throw new InvalidClaimException("Seed nulo ou vazio");
        }

        int seed;
        try {
            seed = Integer.parseInt(seedStr);
        } catch (NumberFormatException e) {
            log.error("Seed invalido: {}", seedStr);
            throw new InvalidClaimException("Seed inválido");
        }

        if (!NumberUtils.isPrime(seed)) {
            log.error("Seed nao e primo: {}", seedStr);
            throw new InvalidClaimException("Seed nao é primo");
        }

        log.debug("Seed valida: {}", seed);
        return true;
    }
}
