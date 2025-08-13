package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.handler.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;

public class RoleValidator implements ClaimValidator{
    private static final List<String> VALID_ROLES = List.of("Admin", "Member", "External");

    // Valida se o JWT contém a claim 'Role' e se é válido conforme a lista de 'VALID_ROLES'
    @Override
    public boolean validate(DecodedJWT jwt) {
        String role = jwt.getClaim("Role").asString();

        if (role == null || !VALID_ROLES.contains(role)) {
            throw new InvalidClaimException("Role inválido");
        }

        return true;

    }
}
