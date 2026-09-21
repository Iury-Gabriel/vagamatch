package dev.iurygabriel.vagamatch.shared.domain;

/**
 * Exceção de regra de negócio, lançada pelo domínio.
 * Mantém o domínio livre de dependências de framework.
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
