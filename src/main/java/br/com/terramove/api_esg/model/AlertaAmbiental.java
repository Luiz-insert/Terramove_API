package br.com.terramove.api_esg.model;

import br.com.terramove.api_esg.model.enums.NivelRisco;
import br.com.terramove.api_esg.model.enums.StatusAlerta;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "alertas_ambientais")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertaAmbiental {

    @Id
    private String id;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    private String descricao;

    @NotNull
    private NivelRisco nivelRisco;

    @NotNull
    private StatusAlerta status;

    private String eventoSismicoId; // Referência para a coleção eventos_sismicos

    private String areaAfetada;
    
    private String medidasRecomendadas;

    private LocalDateTime dataEmissao = LocalDateTime.now();
    
    private LocalDateTime dataResolucao;

    // NoSQL: Flexibilidade para diferentes tipos de alertas (enchente, sismo, incêndio)
    private Map<String, Object> propriedadesEspecificas;
}
