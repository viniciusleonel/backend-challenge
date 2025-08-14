package br.dev.viniciusleonel.backend_challenge.infra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Backend-Challenge")
                        .version("1.0")
                        .description("Este é um projeto Spring Boot que implementa um sistema de validação de JWT (JSON Web Tokens) com validações específicas para claims personalizados. A função principal da API é validar tokens JWT e retornar um boolean indicando se são válidos ou não.")
                        .contact(new Contact()
                                    .name("Vinicius Leonel")
                                    .email("viniciuslps.cms@gmail.com")
                                    .url("https://www.linkedin.com/in/viniciuslps/")
                            )
                                    .license(new License()
                                            .name("GitHub")
                                            .url("https://github.com/viniciusleonel/backend-challenge")));
    }
}

