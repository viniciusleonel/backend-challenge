package br.dev.viniciusleonel.backend_challenge.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;
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
        log.info("Endpoint /api/validate chamado");
        if (!JwtValidator.isValid(token)) {
            log.error("JWT invalido");
            return ResponseEntity.badRequest().body(false);
        }

        log.info("JWT valido");
        return ResponseEntity.ok(true);
    }
}
