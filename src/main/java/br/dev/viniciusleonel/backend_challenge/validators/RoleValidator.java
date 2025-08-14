package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleValidator implements ClaimValidator{

    private static final Logger log = LoggerFactory.getLogger(RoleValidator.class);

    // Valida se o JWT contém a claim 'Role' e se é válido conforme a lista de 'VALID_ROLES'
    @Override
    public boolean validate(DecodedJWT jwt) {
        log.info("Iniciando validacao da claim Role");
        String role = jwt.getClaim("Role").asString();

        if (role == null || role.isEmpty()) {
            log.error("Role nulo ou vazio: {}", role);
            throw new InvalidClaimException("Role nulo ou vazio");
        }

        if (!JwtValidationConfig.VALID_ROLES.contains(role)) {
            log.error("'Role' invalido: {}", role);
            throw new InvalidClaimException("Role invalido");
        }

        log.debug("Role valida: {}", role);
        return true;

    }
}
