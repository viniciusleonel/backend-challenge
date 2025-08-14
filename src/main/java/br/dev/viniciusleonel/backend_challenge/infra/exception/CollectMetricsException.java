package br.dev.viniciusleonel.backend_challenge.infra.exception;

public class CollectMetricsException extends RuntimeException {
    public CollectMetricsException(String message) {
        super(message);
    }
}
