package br.dev.viniciusleonel.backend_challenge.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import br.dev.viniciusleonel.backend_challenge.utils.JwtGenerator;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    // Inicializa o MockMvc antes de cada teste para garantir que o contexto da aplicacao esta pronto
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    // Testa a validacao de um token invalido (nome com numeros), espera status 400 e resposta false
    @Test
	public void testValidateInvalidToken() throws Exception {
		// Token com claim inválido (Name com números) deve retornar 422
		String invalidToken = JwtGenerator.generateJwtToken("Toninho123 Araujo", "Admin", "7841");
		mockMvc.perform(post("/api/validate")
						.param("token", invalidToken))
				.andExpect(status().isUnprocessableEntity()) // 422
				.andExpect(jsonPath("$").value(false));
	}

    // Testa a validacao de um token malformado, espera status 400 e resposta false
	@Test
	public void testValidateMalformedToken() throws Exception {
		String malformedToken = "invalid.token.format";
		mockMvc.perform(post("/api/validate")
						.param("token", malformedToken))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$").value(false));
	}

    // Testa a validacao sem enviar o parametro token, espera status 400
	@Test
	public void testValidateMissingToken() throws Exception {
		mockMvc.perform(post("/api/validate"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testValidateInvalidRole() throws Exception {
		// Token com role inválido deve retornar 422
		String invalidToken = JwtGenerator.generateJwtToken("Toninho Araujo", "Guest", "7841");
		mockMvc.perform(post("/api/validate")
						.param("token", invalidToken))
				.andExpect(status().isUnprocessableEntity()) // 422
				.andExpect(jsonPath("$").value(false));
	}

	@Test
	public void testValidateInvalidSeed() throws Exception {
		// Token com seed não primo deve retornar 422
		String invalidToken = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "4");
		mockMvc.perform(post("/api/validate")
						.param("token", invalidToken))
				.andExpect(status().isUnprocessableEntity()) // 422
				.andExpect(jsonPath("$").value(false));
	}
}
