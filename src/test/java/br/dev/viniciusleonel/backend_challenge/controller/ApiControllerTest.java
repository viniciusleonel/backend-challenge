package br.dev.viniciusleonel.backend_challenge.controller;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.WebApplicationContext;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.dev.viniciusleonel.backend_challenge.infra.exception.InvalidClaimException;
import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;
import static br.dev.viniciusleonel.backend_challenge.validators.JwtValidationConfig.getValidators;
import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerTest {

    private JwtValidator jwtValidator;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // Inicializa o MockMvc antes de cada teste para garantir que o contexto da aplicação esteja pronto
    @BeforeEach
    public void setUp() {
        jwtValidator = new JwtValidator();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @ParameterizedTest
    @CsvSource({
            "Toninho, Admin, 7841",
            "Maximiliano Domingos da Silva e Albuquerque Neto, Member, 97",
            "Márcio Sébastião Gonçalves Júnior, External, 13"
    })
    public void testValidTokenWithValidClaims(String name, String role, String seed) throws Exception {
        // Testa um token valido, espera que o validador aceite o token
        String token = JwtGenerator.generateJwtToken(name, role, seed);
        assertTrue(jwtValidator.isValid(token));
        mockMvc.perform(get("/api/validate")
                        .param("token", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        // Verifica se o token possui exatamente 3 claims
        DecodedJWT jwt = JWT.decode(token);
        assertEquals(getValidators().size(), jwt.getClaims().size());
    }

    private static Stream<Arguments> provideInvalidClaims() {
        String longName = "A".repeat(257); // Nome com 257 caracteres

        return Stream.of(
                Arguments.of("Toninho123 Araujo", "Admin", "7841"),    // nome com números
                Arguments.of("Toninho Araujo", "Guest", "7841"),       // role inválido
                Arguments.of("Toninho Araujo", "Admin", "4"),          // seed não primo
                Arguments.of("123456", "Admin", "7841"),               // nome só números
                Arguments.of("Toninho Araujo", "InvalidRole", "7841"), // role inexistente
                Arguments.of("Toninho Araujo", "Admin", "abc"),        // seed não numérico
                Arguments.of(" ", " ", " "),                           // claims vazias
                Arguments.of(longName, "Admin", "7841")                // nome muito grande
        );
    }

    // Testa a validação de um token com claims inválidas, espera status 400 e resposta false
    @ParameterizedTest
    @MethodSource("provideInvalidClaims")
    public void testInvalidClaims(String name, String role, String seed) throws Exception {
        String invalidToken = JwtGenerator.generateJwtToken(name, role, seed);
        mockMvc.perform(get("/api/validate")
                        .param("token", invalidToken))
                .andExpect(status().isUnprocessableEntity()) // 422
                .andExpect(jsonPath("$").value(false));
        InvalidClaimException exception = assertThrows(InvalidClaimException.class,
                () -> jwtValidator.isValid(invalidToken));
        assertNotNull(exception);
    }

    // Testa a validação de tokens inválidos, espera status 400 e resposta false
    @ParameterizedTest
    @ValueSource(strings = {
            "invalid.token.format",
            "not.a.jwt",
            "eyJhbGciNzg0zI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoirseVMSIsIoiQWUiOiJUb25pbmhvIEFyYXVqbyJ9.sVANvrseVSEND48seVS",
            "",
            "null"
    })
	public void testInvalidTokens(String malformedToken) throws Exception {
		mockMvc.perform(get("/api/validate")
						.param("token", malformedToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").value(false));
        JWTDecodeException exception = assertThrows(JWTDecodeException.class,
                () -> jwtValidator.isValid(malformedToken));
        assertNotNull(exception);
	}

    // Testa a validação sem enviar o parâmetro token, espera status 400 e resposta false
    @Test
    public void testMissingServletRequestParameterToken() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/validate"))
                .andExpect(status().isBadRequest())
                .andReturn();

        // Verifica se a exceção resolvida é MissingServletRequestParameterException
        Exception resolvedException = result.getResolvedException();
        assertNotNull(resolvedException);
        assertInstanceOf(MissingServletRequestParameterException.class, resolvedException);
    }
}
