package br.dev.viniciusleonel.backend_challenge.controller;

import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    private static final Logger log = LoggerFactory.getLogger(ApiController.class);

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateJwt(@RequestParam String token) {
        // No início de cada requisição
        log.info("Endpoint /api/validate chamado");

        long startTime = System.currentTimeMillis();
        if (!JwtValidator.isValid(token)) {
            log.error("JWT invalido");
            return ResponseEntity.badRequest().body(false);
        }

        log.info("Validacao JWT concluida em {}ms", System.currentTimeMillis() - startTime);
        return ResponseEntity.ok(true);
    }
}
