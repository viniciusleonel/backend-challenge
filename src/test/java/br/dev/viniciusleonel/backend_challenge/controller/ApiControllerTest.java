package br.dev.viniciusleonel.backend_challenge.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testValidateValidToken() throws Exception {
        // Valida um token JWT válido: todos os claims estão corretos (Name sem números, Role válido, Seed primo)
        String validToken = JwtGenerator.generateJwtToken("Toninho Araujo", "Admin", "7841");
        mockMvc.perform(get("/api/validate")
                        .param("token", validToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    public void testValidateInvalidToken() throws Exception {
        // Valida um token JWT inválido: claim Name contém números, o que é proibido
        String invalidToken = JwtGenerator.generateJwtToken("Toninho123 Araujo", "Admin", "7841");
        mockMvc.perform(get("/api/validate")
                        .param("token", invalidToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    public void testValidateMalformedToken() throws Exception {
        // Valida um token JWT malformado: formato inválido, não é um JWT
        String malformedToken = "invalid.token.format";
        mockMvc.perform(get("/api/validate")
                        .param("token", malformedToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    public void testValidateMissingToken() throws Exception {
        // Valida requisição sem o parâmetro 'token': espera-se erro 400 (Bad Request)
        mockMvc.perform(get("/api/validate"))
                .andExpect(status().isBadRequest()); // Ou status adequado, dependendo da configuração
    }
}
