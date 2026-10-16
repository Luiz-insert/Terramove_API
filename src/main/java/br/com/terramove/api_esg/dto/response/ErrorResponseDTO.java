package br.com.terramove.api_esg.dto.response;

import java.time.LocalDateTime;

/**
 * DTO de resposta padronizado para erros da API.
 * Utilizado pelo GlobalExceptionHandler.
 */
public record ErrorResponseDTO(
        int status,
        String error,
        String message,
        String path,
        LocalDateTime timestamp
) {
}
