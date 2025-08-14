package br.dev.viniciusleonel.backend_challenge.validators;

import com.auth0.jwt.interfaces.DecodedJWT;

public interface ClaimValidator {
    boolean validate(DecodedJWT jwt);
}
