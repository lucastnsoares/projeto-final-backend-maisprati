package br.com.maisprati.projeto.model.entity;

public abstract class Empresa {
    private String razaoSocial;
    private String nomeFantasia;
    private Endereco enderecoMatriz;

    public Empresa(String razaoSocial, String nomeFantasia) {
        this.razaoSocial = razaoSocial;
        this.nomeFantasia = nomeFantasia;
    }
}
