package br.com.terramove.api_esg.model.enums;

/**
 * Classifica o nível de risco de um evento sísmico ou alerta ambiental
 * com base na magnitude Richter.
 */
public enum NivelRisco {

    BAIXO("Baixo", "Magnitude < 3.0 — sem risco significativo"),
    MODERADO("Moderado", "Magnitude 3.0–4.9 — pode causar danos menores"),
    ALTO("Alto", "Magnitude 5.0–6.9 — risco de danos estruturais"),
    CRITICO("Crítico", "Magnitude >= 7.0 — potencial catastrófico");

    private final String label;
    private final String descricao;

    NivelRisco(String label, String descricao) {
        this.label = label;
        this.descricao = descricao;
    }

    public String getLabel() {
        return label;
    }

    public String getDescricao() {
        return descricao;
    }
}
