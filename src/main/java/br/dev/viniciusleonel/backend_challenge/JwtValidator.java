package br.dev.viniciusleonel.backend_challenge;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.exceptions.JWTDecodeException;

import java.util.Set;

public class JwtValidator {

    private static final Set<String> VALID_ROLES = Set.of("Admin", "Member", "External");

    public static boolean isValid(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);

            // Valida se tem apenas 3 claims
            if (jwt.getClaims().size() != 3) {
                return false;
            }

            // Verifica se tem os claims necessárias
            if (jwt.getClaim("Name").isMissing() || jwt.getClaim("Role").isMissing() || jwt.getClaim("Seed").isMissing()) {
                return false;
            }

            // Valida claim Name - não pode ter números e tamanho máximo 256
            String name = jwt.getClaim("Name").asString();
            if (name == null || name.length() > 256 || name.matches(".*\\d.*")) {
                return false;
            }

            // Valida claim Role - deve ser um dos valores válidos
            String role = jwt.getClaim("Role").asString();
            if (!VALID_ROLES.contains(role)) {
                return false;
            }

            // Valida claim Seed - deve ser número primo
            String seedStr = jwt.getClaim("Seed").asString();
            if (seedStr == null) return false;

            int seed;
            try {
                seed = Integer.parseInt(seedStr);
            } catch (NumberFormatException e) {
                return false;
            }

            if (!isPrime(seed)) return false;

            return true;

        } catch (JWTDecodeException e) {
            return false;
        }
    }

    private static boolean isPrime(int number) {
        if (number <= 1) return false;
        if (number == 2) return true;
        if (number % 2 == 0) return false;
        for (int i = 3; i <= Math.sqrt(number); i += 2) {
            if (number % i == 0) return false;
        }
        return true;
    }
}
