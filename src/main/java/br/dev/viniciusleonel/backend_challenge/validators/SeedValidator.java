package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.handler.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.NumberUtils;
import com.auth0.jwt.interfaces.DecodedJWT;

public class SeedValidator implements ClaimValidator{

    // Valida se o JWT contém a claim 'Seed', se é um número inteiro e se é primo
    @Override
    public boolean validate(DecodedJWT jwt) {
        String seedStr = jwt.getClaim("Seed").asString();
        if (seedStr == null) {
            throw new InvalidClaimException("Seed inválido");
        }

        int seed;
        try {
            seed = Integer.parseInt(seedStr);
        } catch (NumberFormatException e) {
            throw new InvalidClaimException("Seed inválido");
        }

        if (!NumberUtils.isPrime(seed)) {
            throw new InvalidClaimException("Seed inválido");
        }
        return true;
    }
}
