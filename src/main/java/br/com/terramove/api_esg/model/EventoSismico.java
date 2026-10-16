package br.com.terramove.api_esg.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "eventos_sismicos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoSismico {

    @Id
    private String id;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotNull(message = "Magnitude é obrigatória")
    @DecimalMin("0.0")
    @DecimalMax("10.0")
    private Double magnitude;

    @NotBlank(message = "Localização é obrigatória")
    private String localizacao;

    @NotNull(message = "Data de ocorrência é obrigatória")
    private LocalDateTime dataOcorrencia;

    private String descricao;

    private LocalDateTime dataCadastro = LocalDateTime.now();

    // Modelo flexível: metadados adicionais que podem variar por evento
    private Map<String, Object> metadados;
}
