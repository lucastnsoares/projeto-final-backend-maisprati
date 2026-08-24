package br.com.maisprati.projeto.dto.request;

import br.com.maisprati.projeto.model.enums.UF;

public record EnderecoRequestDTO(
        String cep,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        UF uf,
        String pais,
        String descricao
) {}
