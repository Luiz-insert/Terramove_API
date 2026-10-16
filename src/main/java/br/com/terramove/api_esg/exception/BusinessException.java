package br.com.terramove.api_esg.exception;

/**
 * Exceção lançada para violações de regras de negócio.
 * Mapeada para HTTP 422 Unprocessable Entity pelo GlobalExceptionHandler.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
