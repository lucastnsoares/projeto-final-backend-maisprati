package br.com.maisprati.projeto.model.entity;


import java.util.List;
import java.util.Set;

public class PontoColeta {
    private Usuario gerente;
    private Set<Usuario> operadores;
    private Endereco endereco;

    public PontoColeta(Usuario gerente, Set<Usuario> operadores, Endereco endereco) {
        this.gerente = gerente;
        this.operadores = operadores;
        this.endereco = endereco;
    }

    public void addOperador(Usuario operador) {
        if (operador == null) {
            throw new IllegalArgumentException("O operador a ser cadastrado deve ser fornecido");
        }
        if (!operadores.add(operador)) {}
            throw new IllegalArgumentException("O operador já está cadastrado neste ponto de coleta");
    }

}
