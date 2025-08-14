package br.dev.viniciusleonel.backend_challenge.validators;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;

import static org.junit.jupiter.api.Assertions.*;

public class JwtValidatorTest {

	private JwtValidator jwtValidator;

	@BeforeEach
	public void setUp() {
		// Inicializa o JwtValidator antes de cada teste
		jwtValidator = new JwtValidator();
	}

	@Test
	public void testValidToken() {
		// Testa um token valido, espera que o validador aceite o token
		String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
		assertTrue(jwtValidator.isValid(token));

		// Verifica se o token possui exatamente 3 claims
		DecodedJWT jwt = JWT.decode(token);
		assertEquals(3, jwt.getClaims().size());
	}

    @Test
    public void testInvalidTokenWrongNumberOfClaims() {
        // Gera um token sem Seed
        String invalidToken = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", null);

        // Espera que o validator lance InvalidClaimException
        assertThrows(InvalidClaimException.class, () -> jwtValidator.isValid(invalidToken));
    }

    @Test
    public void testMalformedToken() {
        // Token malformado
        String malformedToken = "invalid.token.format";

        // Espera que o validator lance JWTDecodeException
        assertThrows(com.auth0.jwt.exceptions.JWTDecodeException.class, () -> jwtValidator.isValid(malformedToken));
    }

	@Test
	public void testTokenWithEmptyClaims() {
		// Testa um token com claims vazias, espera que o validador rejeite
		String tokenWithEmptyClaims = JwtGenerator.generateJwtToken("", "", "");
        assertThrows(InvalidClaimException.class, () -> jwtValidator.isValid(tokenWithEmptyClaims));
	}

	@Test
	public void testNullToken() {
		// Testa um token nulo, espera que o validador rejeite
        assertThrows(com.auth0.jwt.exceptions.JWTDecodeException.class, () -> jwtValidator.isValid(null));
	}

	@Test
	public void testEmptyToken() {
		// Testa um token vazio, espera que o validador rejeite
        assertThrows(com.auth0.jwt.exceptions.JWTDecodeException.class, () -> jwtValidator.isValid(""));
	}
}