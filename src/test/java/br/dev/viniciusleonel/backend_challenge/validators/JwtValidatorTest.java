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
        // Gera um token sem Seed
        String invalidToken = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", null);

        // Espera que o validator lance InvalidClaimException
        assertThrows(InvalidClaimException.class, () -> JwtValidator.isValid(invalidToken));
    }

    @Test
    public void testMalformedToken() {
        // Token malformado
        String malformedToken = "invalid.token.format";

        // Espera que o validator lance JWTDecodeException
        assertThrows(com.auth0.jwt.exceptions.JWTDecodeException.class, () -> JwtValidator.isValid(malformedToken));
    }
}
