package br.com.maisprati.projeto.model.enums;

public enum Perfil {
    ADMIN("Administrador do sistema"),
    GERENTE_PONTO_COLETA("Gerente do ponto de coleta"),
    OPERADOR_PONTO_COLETA("Operador do ponto de coleta"),
    USUARIO_DESCARTE("Usuário que descarta as roupas"),
    GERENTE_RECICLADORA("Gerente da empresa/cooperativa recicladora"),
    OPERADOR_RECICLADORA("Operador da empresa/cooperativa recicladora");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }
}
