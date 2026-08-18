package br.com.maisprati.projeto.model.enums;

public enum UF {
    AC("Acre", "Norte"),
    AL("Alagoas", "Nordeste"),
    AP("Amapá", "Norte"),
    AM("Amazonas", "Norte"),
    BA("Bahia", "Nordeste"),
    CE("Ceará", "Nordeste"),
    DF("Distrito Federal", "Centro-Oeste"),
    ES("Espírito Santo", "Sudeste"),
    GO("Goiás", "Centro-Oeste"),
    MA("Maranhão", "Nordeste"),
    MT("Mato Grosso", "Centro-Oeste"),
    MS("Mato Grosso do Sul", "Centro-Oeste"),
    MG("Minas Gerais", "Sudeste"),
    PA("Pará", "Norte"),
    PB("Paraíba", "Nordeste"),
    PR("Paraná", "Sul"),
    PE("Pernambuco", "Nordeste"),
    PI("Piauí", "Nordeste"),
    RJ("Rio de Janeiro", "Sudeste"),
    RN("Rio Grande do Norte", "Nordeste"),
    RS("Rio Grande do Sul", "Sul"),
    RO("Rondônia", "Norte"),
    RR("Roraima", "Norte"),
    SC("Santa Catarina", "Sul"),
    SP("São Paulo", "Sudeste"),
    SE("Sergipe", "Nordeste"),
    TO("Tocantins", "Norte");

    private final String nome;
    private final String regiao;

    UF(String nome, String regiao) {
        this.nome = nome;
        this.regiao = regiao;
    }

    public String getNome() {
        return nome;
    }

    public String getRegiao() {
        return regiao;
    }
}