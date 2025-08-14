package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NameValidator implements ClaimValidator{

    private static final Logger log = LoggerFactory.getLogger(NameValidator.class);

    // Valida se o JWT contém a claim 'Name', se não tem números e se tem menos que 256 caracteres
    @Override
    public boolean validate(DecodedJWT jwt) {
        log.info("Iniciando validacao da claim Name");
        String name = jwt.getClaim("Name").asString();

        if (name == null || name.isEmpty()) {
            log.error("Nome nulo ou vazio: {}", name);
            throw new InvalidClaimException("Nome nulo ou vazio");
        }

        if (name.length() >= 256) {
            log.error("Nome excedeu tamanho maximo de 256 caracteres: {} caracteres", name.length());
            throw new InvalidClaimException("Nome excedeu tamanho maximo de 256 caracteres");
        }

        if ( name.matches(".*\\d.*")) {
            log.error("Nome contem numeros: {} ", name);
            throw new InvalidClaimException("Nome contem numeros");
        }

        log.debug("Nome valido: {}", name);
        return true;
    }
}
