package br.com.maisprati.projeto.model.enums;

public enum Perfil {
    PERFIL_ADMIN("Administrador do sistema"),
    PERFIL_GERENTE_PONTO_COLETA("Gerente do ponto de coleta"),
    PERFIL_OPERADOR_PONTO_COLETA("Operador do ponto de coleta"),
    PERFIL_USUARIO_DESCARTE("Usuário que descarta as roupas"),
    PERFIL_GERENTE_RECICLADORA("Gerente da empresa/cooperativa recicladora"),
    PERFIL_OPERADOR_RECICLADORA("Operador da empresa/cooperativa recicladora");

    private final String descricao;

    Perfil(String descricao) {
        this.descricao = descricao;
    }
}
