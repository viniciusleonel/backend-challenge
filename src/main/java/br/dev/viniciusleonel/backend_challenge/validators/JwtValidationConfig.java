package br.dev.viniciusleonel.backend_challenge.validators;

import java.util.List;

public final class JwtValidationConfig {

    // Roles válidas centralizadas
    public static final List<String> VALID_ROLES = List.of("Admin", "Member", "External");

    private JwtValidationConfig() {
        // Construtor privado para evitar instanciação
    }

    // Lista de validadores
    public static List<ClaimValidator> getValidators() {
        return List.of(
                new NameValidator(),
                new RoleValidator(), // RoleValidator irá usar JwtValidationConfig.VALID_ROLES
                new SeedValidator()
                // Pode adicionar novos validadores aqui
        );
    }
}
