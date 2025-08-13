package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        DecodedJWT jwt = JWT.decode(token);
        assertFalse(validator.validate(jwt)); // 4 não é primo
    }

    @Test
    public void testNullSeed() {
        // Simula um token com seed nulo (ajustado manualmente para teste)
        String invalidToken = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", null);
        DecodedJWT jwt = JWT.decode(invalidToken);
        assertFalse(validator.validate(jwt)); // Deve falhar por seed nulo
    }

    @Test
    public void testNonNumericSeed() {
        // Gera um token com um seed não numérico (ex.: "abc")
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "abc");
        DecodedJWT jwt = JWT.decode(token);
        assertFalse(validator.validate(jwt)); // "abc" não é número
    }
}