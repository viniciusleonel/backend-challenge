package br.dev.viniciusleonel.backend_challenge.infra.exception;

public class InvalidClaimException extends RuntimeException {
    public InvalidClaimException(String message) {
        super(message);
    }
}
