package br.dev.viniciusleonel.backend_challenge.validators;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;

public class NameValidatorTest {

	private final NameValidator validator = new NameValidator();

	@Test
	public void testValidName() {
		// Testa um nome valido, espera que o validador aceite o nome "Toninho Araujo"
		String token = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
		DecodedJWT jwt = JWT.decode(token);
		assertTrue(validator.validate(jwt));
	}

	@Test
	public void testNameWithNumbers() {
		// Testa um nome com numeros, espera que o validador lance InvalidClaimException
		String token = JwtGenerator.generateJwtToken("Toninho123 Araujo", "Admin", "7841");
		DecodedJWT jwt = JWT.decode(token);
		assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
	}

	@Test
	public void testLongName() {
		// Testa um nome muito longo (257 caracteres), espera que o validador lance InvalidClaimException
		String longName = "a".repeat(257);
		String token = JwtGenerator.generateJwtToken(longName, "Admin", "7841");
		DecodedJWT jwt = JWT.decode(token);
		assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
	}

	@Test
	public void testEmptyName() {
		// Testa um nome vazio, espera que o validador lance InvalidClaimException
		String token = JwtGenerator.generateJwtToken("", "Admin", "7841");
		DecodedJWT jwt = JWT.decode(token);
		assertThrows(InvalidClaimException.class, () -> validator.validate(jwt));
	}
}