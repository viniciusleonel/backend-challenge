package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.handler.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;

public class RoleValidator implements ClaimValidator{

    // Valida se o JWT contém a claim 'Role' e se é válido conforme a lista de 'VALID_ROLES'
    @Override
    public boolean validate(DecodedJWT jwt) {
        String role = jwt.getClaim("Role").asString();

        if (role == null || !JwtValidationConfig.VALID_ROLES.contains(role)) {
            throw new InvalidClaimException("Role inválido");
        }

        return true;

    }
}
