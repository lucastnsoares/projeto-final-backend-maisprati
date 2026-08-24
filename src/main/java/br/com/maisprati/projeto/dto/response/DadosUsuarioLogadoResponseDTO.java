package br.com.maisprati.projeto.dto.response;

import br.com.maisprati.projeto.model.entity.Usuario;

import java.util.List;

public record DadosUsuarioLogadoResponseDTO (
        String nome,
        String documento,
        String email,
        String telefone,
        List<EnderecoResponseDTO> enderecos) {

    public DadosUsuarioLogadoResponseDTO(Usuario usuario){
        this(
                usuario.getNome(),
                usuario.getDocumento(),
                usuario.getEmail(),
                usuario.getTelefone(),
                usuario.getEnderecos().stream()
                        .map(EnderecoResponseDTO::new)
                        .toList()
        );
    }
}

