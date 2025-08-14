package br.dev.viniciusleonel.backend_challenge.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;

public class RoleValidatorTest {

	private final RoleValidator validator = new RoleValidator();

	@Test
	public void testValidRole() {
		// Testa um role valido ("Admin"), espera que o validador aceite o role
		String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
		DecodedJWT jwt = JWT.decode(token);
		assertTrue(validator.validate(jwt));
	}

	@Test
	public void testInvalidRole() {
		// Testa um role invalido ("Guest"), espera que o validador lance InvalidClaimException
		String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Guest", "7841");
		DecodedJWT jwt = JWT.decode(token);
		assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
	}

	@Test
	public void testNullRole() {
		// Testa um role nulo, espera que o validador lance InvalidClaimException
		String token = JwtGenerator.generateJwtToken("Toninho Araujo", null, "7841");
		DecodedJWT jwt = JWT.decode(token);
		assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
	}
}