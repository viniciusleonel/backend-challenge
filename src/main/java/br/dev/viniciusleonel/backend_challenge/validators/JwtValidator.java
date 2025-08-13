package br.dev.viniciusleonel.backend_challenge.validators;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.List;

public class JwtValidator {

    // Busca a lista de validadores, mantendo o princípio 'Open Closed'
    private static final List<ClaimValidator> validators = JwtValidationConfig.getValidators();

    public static boolean isValid(String token) {
            DecodedJWT jwt = JWT.decode(token);

            // Valida o total de claims presentes no JWT
            if (jwt.getClaims().size() != validators.size()) {
                return false;
            }

            // Chama o metodo 'validate' de cada validador da lista
            for (ClaimValidator validator : validators) {
                validator.validate(jwt);
            }
            return true;
    }
}
