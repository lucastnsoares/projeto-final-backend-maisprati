package br.com.maisprati.projeto.model.entity;

import br.com.maisprati.projeto.model.enums.UF;
import java.math.BigDecimal;

public class Endereco {
    private Long id;
    private Boolean principal;
    private String descricao;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private UF uf;
    private String pais;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
