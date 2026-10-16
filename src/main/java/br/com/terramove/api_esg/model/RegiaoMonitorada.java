package br.com.terramove.api_esg.model;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Document(collection = "regioes_monitoradas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegiaoMonitorada {

    @Id
    private String id;

    @NotBlank(message = "Nome da região é obrigatório")
    private String nome;

    @NotBlank(message = "Estado/Província é obrigatório")
    private String estado;

    private Double areaKm2;

    // GeoJSON ou coordenadas simples para NoSQL
    private List<Double> coordenadasCentro; 

    private String nivelPreservacao;

    // Flexibilidade NoSQL: Diferentes biomas podem ter diferentes indicadores (ex: umidade para floresta, salinidade para costa)
    private Map<String, Object> indicadoresAmbientais;
}
