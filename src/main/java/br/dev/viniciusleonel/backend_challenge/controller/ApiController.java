package br.dev.viniciusleonel.backend_challenge.controller;

import br.dev.viniciusleonel.backend_challenge.validators.JwtValidator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ApiController {

    @GetMapping("/validate")
    public boolean validateJwt(@RequestParam String token) {
        try {
            return JwtValidator.isValid(token);
        } catch (Exception e) {
            return false;
        }
    }
}
