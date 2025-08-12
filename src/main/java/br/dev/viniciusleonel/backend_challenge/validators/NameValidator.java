package br.dev.viniciusleonel.backend_challenge.validators;

import com.auth0.jwt.interfaces.DecodedJWT;

public class NameValidator implements ClaimValidator{

    // Valida se o JWT contém a claim 'Name', se não tem números e se tem menos que 256 caracteres
    @Override
    public boolean validate(DecodedJWT jwt) {
        String name = jwt.getClaim("Name").asString();
        return name != null && name.length() <= 256 && !name.matches(".*\\d.*");
    }
}
