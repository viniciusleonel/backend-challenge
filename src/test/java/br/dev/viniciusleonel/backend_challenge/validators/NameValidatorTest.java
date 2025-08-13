package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.handler.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class NameValidatorTest {


    @Test
    public void testValidName() {
        // Gera um token com um 'Name' válido
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
        DecodedJWT jwt = JWT.decode(token);
        NameValidator validator = new NameValidator();
        assertTrue(validator.validate(jwt));
    }

    @Test
    public void testNameWithNumbers() {
        // Gera um token com um 'Name' inválido (Contém números)
        String token = JwtGenerator.generateJwtToken("Toninho123 Araujo", "Admin", "7841");
        assertThrows(InvalidClaimException.class, () -> JwtValidator.isValid(token));
    }

    @Test
    public void testLongName() {
        // Gera um token com um 'Name' inválido (Contém mais de 256 caracteres)
        StringBuilder longName = new StringBuilder();
        longName.append("a".repeat(257));
        String token = JwtGenerator.generateJwtToken(longName.toString(), "Admin", "7841");
        assertThrows(InvalidClaimException.class, () -> JwtValidator.isValid(token));
    }
}