package br.dev.viniciusleonel.backend_challenge.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    
    @GetMapping
    public String HelloWorld () {
        return "Hello World";
    }
}
