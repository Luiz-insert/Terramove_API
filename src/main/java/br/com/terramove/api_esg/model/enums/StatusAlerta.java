package br.com.terramove.api_esg.model.enums;

/**
 * Status do ciclo de vida de um Alerta Ambiental.
 */
public enum StatusAlerta {

    ATIVO("Ativo"),
    EM_MONITORAMENTO("Em Monitoramento"),
    RESOLVIDO("Resolvido"),
    CANCELADO("Cancelado");

    private final String label;

    StatusAlerta(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
