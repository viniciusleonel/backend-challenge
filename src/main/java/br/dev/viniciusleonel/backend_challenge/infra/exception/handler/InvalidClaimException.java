package br.dev.viniciusleonel.backend_challenge.infra.exception.handler;

public class InvalidClaimException extends RuntimeException {
    public InvalidClaimException(String message) {
        super(message);
    }
}
