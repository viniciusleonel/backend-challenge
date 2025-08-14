package br.dev.viniciusleonel.backend_challenge.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;

public class SeedValidatorTest {

    private final SeedValidator validator = new SeedValidator();

    @Test
    public void testValidPrimeSeed() {
        // Testa um seed primo valido (7841), espera que o validador aceite o seed
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
        DecodedJWT jwt = JWT.decode(token);
        assertTrue(validator.validate(jwt));
    }

    @Test
    public void testNonPrimeSeed() {
        // Testa um seed nao primo (4), espera que o validador lance InvalidClaimException
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "4");
        DecodedJWT jwt = JWT.decode(token);
        assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
    }

    @Test
    public void testNullSeed() {
        // Testa um token sem o claim Seed, espera que o validador lance InvalidClaimException
        String token = JWT.create()
                .withClaim("Name", "Toninho Araujo")
                .withClaim("Role", "Admin")
                .sign(Algorithm.HMAC256("your-secret-key")); // sem Seed
        DecodedJWT jwt = JWT.decode(token);
        assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
    }

    @Test
    public void testNonNumericSeed() {
        // Testa um seed nao numerico ("abc"), espera que o validador lance InvalidClaimException
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "abc");
        DecodedJWT jwt = JWT.decode(token);
        assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
    }
}