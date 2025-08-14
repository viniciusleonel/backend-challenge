package br.dev.viniciusleonel.backend_challenge.infra.exception;

public class HealthCheckException extends RuntimeException {
    public HealthCheckException(String message) {
        super(message);
    }
}
