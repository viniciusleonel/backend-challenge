package br.dev.viniciusleonel.backend_challenge.controller;

import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validateJwt(@RequestParam String token) {
        if (JwtValidator.isValid(token)) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.badRequest().body(false);
    }
}
