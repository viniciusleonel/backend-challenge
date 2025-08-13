package br.dev.viniciusleonel.backend_challenge.utils;

public class GenerateJwtToken {
    public static void main(String[] args) {
        String name = "Toninho Araujo";
        String role = "Admin";
        String seed = "";

        String token = JwtGenerator.generateJwtToken(name, role, seed);
        System.out.println("JWT Gerado:");
        System.out.println(token);
    }
}
