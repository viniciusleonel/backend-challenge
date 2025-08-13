package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.handler.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SeedValidatorTest {

    private SeedValidator validator;

    @BeforeEach
    public void setUp() {
        validator = new SeedValidator();
    }

    @Test
    public void testValidPrimeSeed() {
        // Gera um token com um seed primo (ex.: 7841)
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
        DecodedJWT jwt = JWT.decode(token);
        assertTrue(validator.validate(jwt)); // 7841 é primo
    }

    @Test
    public void testNonPrimeSeed() {
        // Gera um token com um seed não primo (ex.: 4)
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "4");
        assertThrows(InvalidClaimException.class, () -> JwtValidator.isValid(token));
    }

    @Test
    public void testNullSeed() {
        // Simula um token com seed nulo (ajustado manualmente para teste)
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", null);
        assertThrows(InvalidClaimException.class, () -> JwtValidator.isValid(token));
    }

    @Test
    public void testNonNumericSeed() {
        // Gera um token com um seed não numérico (ex.: "abc")
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "abc");
        assertThrows(InvalidClaimException.class, () -> JwtValidator.isValid(token));
    }
}