package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;
import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtValidatorTest {

    @BeforeEach
    public void setUp() {
        // Nada a inicializar, mas pode ser usado para mock ou setup futuro
    }

    @Test
    public void testValidToken() {
        // Gera um token válido com 3 claims (Name, Role, Seed)
        String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
        assertTrue(JwtValidator.isValid(token));
        // Verifica manualmente o decode para confirmar
        DecodedJWT jwt = JWT.decode(token);
        assertEquals(3, jwt.getClaims().size()); // Deve ter exatamente 3 claims
    }

    @Test
    public void testInvalidTokenWrongNumberOfClaims() {
        // Gera um token com menos claims (ex.: sem Seed)
        String invalidToken = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", null); // Pode falhar
        assertFalse(JwtValidator.isValid(invalidToken)); // Deve falhar por número errado de claims
    }

    @Test
    public void testMalformedToken() {
        // Testa um token malformado
        String malformedToken = "invalid.token.format";
        assertFalse(JwtValidator.isValid(malformedToken)); // Deve falhar na decodificação
    }
}
