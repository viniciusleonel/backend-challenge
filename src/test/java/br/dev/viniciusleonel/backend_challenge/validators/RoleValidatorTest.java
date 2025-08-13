package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class RoleValidatorTest {

    @Test
    public void testValidRole() {
        // Gera um token com um 'Role' válido
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
        DecodedJWT jwt = JWT.decode(token);
        RoleValidator validator = new RoleValidator();
        assertTrue(validator.validate(jwt)); // Role "Admin" é válido
    }

    @Test
    public void testInvalidRole() {
        // Gera um token com um role inválido
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Guest", "7841");
        DecodedJWT jwt = JWT.decode(token);
        RoleValidator validator = new RoleValidator();
        assertFalse(validator.validate(jwt)); // Role "Guest" não está em VALID_ROLES
    }

    @Test
    public void testNullRole() {
        // Gera um token com role nulo (simulado ajustando o token não é ideal,
        // mas para teste, usamos um valor inválido)
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", null, "7841"); // Pode lançar exceção
        DecodedJWT jwt = JWT.decode(token);
        RoleValidator validator = new RoleValidator();
        assertFalse(validator.validate(jwt)); // Deve falhar por role nulo
    }
}