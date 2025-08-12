package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class NameValidatorTest {

    @Test
    public void testValidName() {
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
        DecodedJWT jwt = JWT.decode(token);
        NameValidator validator = new NameValidator();
        assertTrue(validator.validate(jwt));
    }

    @Test
    public void testNameWithNumbers() {
        String token = JwtGenerator.generateJwtToken("Toninho123 Araujo", "Admin", "7841");
        DecodedJWT jwt = JWT.decode(token);
        NameValidator validator = new NameValidator();
        assertFalse(validator.validate(jwt));
    }

    @Test
    public void testLongName() {
        StringBuilder longName = new StringBuilder();
        for (int i = 0; i < 257; i++) longName.append("a");
        String token = JwtGenerator.generateJwtToken(longName.toString(), "Admin", "7841");
        DecodedJWT jwt = JWT.decode(token);
        NameValidator validator = new NameValidator();
        assertFalse(validator.validate(jwt));
    }
}