package br.dev.viniciusleonel.backend_challenge.validators;

import java.util.List;

public class ClaimValidatorsList {

    private ClaimValidatorsList() {
        // Construtor privado para evitar instanciação
    }

    public static List<ClaimValidator> getValidators() {
        return List.of(
                new NameValidator(),
                new RoleValidator(),
                new SeedValidator()
                // Pode adicionar mais validadores aqui no futuro
        );
    }

}
