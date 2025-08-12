package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.utils.NumberUtils;
import com.auth0.jwt.interfaces.DecodedJWT;

public class SeedValidator implements ClaimValidator{

    // Valida se o JWT contém a claim 'Seed', se é um número inteiro e se é primo
    @Override
    public boolean validate(DecodedJWT jwt) {
        String seedStr = jwt.getClaim("Seed").asString();
        if (seedStr == null) return false;

        int seed;
        try {
            seed = Integer.parseInt(seedStr);
        } catch (NumberFormatException e) {
            return false;
        }
        return NumberUtils.isPrime(seed);
    }
}
