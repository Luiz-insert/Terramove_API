package br.com.terramove.api_esg.model;

import br.com.terramove.api_esg.model.enums.StatusAuditoria;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "auditorias_compliance")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaCompliance {

    @Id
    private String id;

    @NotBlank(message = "Título é obrigatório")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    private String descricao;

    @NotNull
    private StatusAuditoria status;

    private String regiaoId; // Referência para regioes_monitoradas

    private LocalDateTime dataAuditoria;

    @NotBlank(message = "Responsável é obrigatório")
    private String responsavel;

    private String normaReferencia;

    private String resultado;

    private String observacoes;

    private LocalDateTime dataProximaAuditoria;

    private LocalDateTime dataCadastro = LocalDateTime.now();

    private LocalDateTime dataAtualizacao;

    // NoSQL: Lista de evidências (links, nomes de arquivos, ou metadados de fotos)
    private List<String> evidencias;

    // Flexibilidade: Checklist dinâmico de conformidade
    private List<ChecklistItem> checklist;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChecklistItem {
        private String item;
        private boolean conforme;
        private String nota;
    }
}
