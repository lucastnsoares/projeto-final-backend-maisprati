package br.com.maisprati.projeto.model.entity;

import java.util.ArrayList;
import java.util.List;

public class EmpresaRecicladora extends Empresa {

    private List<PontoColeta> pontosColeta;

    public EmpresaRecicladora(String razaoSocial, String nomeFantasia) {
        super(razaoSocial, nomeFantasia);
        this.pontosColeta = new ArrayList<>();
    }
}
