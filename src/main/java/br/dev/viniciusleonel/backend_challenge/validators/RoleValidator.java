package br.dev.viniciusleonel.backend_challenge.validators;

import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.List;

public class RoleValidator implements ClaimValidator{
    private static final List<String> VALID_ROLES = List.of("Admin", "Member", "External");

    // Valida se o JWT contém a claim 'Role' e se é válido conforme a lista de 'VALID_ROLES'
    @Override
    public boolean validate(DecodedJWT jwt) {
        String role = jwt.getClaim("Role").asString();
        return role != null && VALID_ROLES.contains(role);
    }
}
