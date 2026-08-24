package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.Endereco;
import br.com.maisprati.projeto.model.enums.UF;

public record EnderecoResponseDTO(String cep,
                                  String logradouro,
                                  String numero,
                                  String complemento,
                                  String bairro,
                                  String cidade,
                                  UF uf,
                                  String pais,
                                  String descricao) {
    public EnderecoResponseDTO (Endereco endereco) {
        this(
                endereco.getCep(),
                endereco.getLogradouro(),
                endereco.getNumero(),
                endereco.getComplemento(),
                endereco.getBairro(),
                endereco.getCidade(),
                endereco.getUf(),
                endereco.getPais(),
                endereco.getDescricao()
        );
    }
}
