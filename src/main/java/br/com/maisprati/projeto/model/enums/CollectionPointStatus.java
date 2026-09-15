package br.com.maisprati.projeto.model.enums;

public enum CollectionPointStatus {
    ACTIVE("Ponto de coleta ativo"),
    PENDING("Ponto de coleta pendente de ativação"),
    SUSPENDED("Ponto de coleta suspenso");

    private final String description;

    CollectionPointStatus(String description) {
        this.description = description;
    }
}
