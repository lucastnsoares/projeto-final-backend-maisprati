package br.com.maisprati.projeto.model.enums;

public enum Role {
    ROLE_ADMIN("Administrador do sistema"),
    ROLE_GERENTE_PONTO_COLETA("Gerente do ponto de coleta"),
    ROLE_OPERADOR_PONTO_COLETA("Operador do ponto de coleta"),
    ROLE_USUARIO_DESCARTE("Usuário que descarta as roupas"),
    ROLE_GERENTE_RECICLADORA("Gerente da empresa/cooperativa recicladora"),
    ROLE_OPERADOR_RECICLADORA("Operador da empresa/cooperativa recicladora");

    private final String descricao;

    Role(String descricao) {
        this.descricao = descricao;
    }
}
