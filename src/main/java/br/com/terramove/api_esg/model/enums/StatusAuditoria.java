package br.com.terramove.api_esg.model.enums;

/**
 * Status do ciclo de vida de uma Auditoria de Compliance.
 */
public enum StatusAuditoria {

    PENDENTE("Pendente"),
    EM_ANDAMENTO("Em Andamento"),
    CONCLUIDA("Concluída"),
    NAO_CONFORME("Não Conforme"),
    CANCELADA("Cancelada");

    private final String label;

    StatusAuditoria(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
