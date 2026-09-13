package br.com.maisprati.projeto.model.enums;

public enum Role {
    ADMIN("Administrador do sistema"),
    PONTO_COLETA_GERENTE("Gerente do ponto de coleta"),
    PONTO_COLETA_OPERADOR("Operador do ponto de coleta"),
    DOADOR("Usuário que descarta os tecidos no Ponto de Coleta");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
