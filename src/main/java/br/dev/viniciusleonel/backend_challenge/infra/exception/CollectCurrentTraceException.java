package br.dev.viniciusleonel.backend_challenge.infra.exception;

public class CollectCurrentTraceException extends RuntimeException {
    public CollectCurrentTraceException(String message) {
        super(message);
    }
}