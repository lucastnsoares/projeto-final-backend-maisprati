package br.com.maisprati.projeto.service;

import br.com.maisprati.projeto.dto.request.AlteracaoDeDadosRequestDTO;
import br.com.maisprati.projeto.dto.request.AlterarSenhaRequestDTO;
import br.com.maisprati.projeto.dto.response.DadosUsuarioLogadoResponseDTO;
import br.com.maisprati.projeto.model.entity.Endereco;
import br.com.maisprati.projeto.model.entity.Usuario;
import br.com.maisprati.projeto.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder encoder;

    public void alterarSenha(Usuario usuarioLogado, AlterarSenhaRequestDTO dto){
        if(!encoder.matches(dto.senhaAtual(), usuarioLogado.getSenha())){
            throw new IllegalArgumentException("A senha atual informada está incorreta");
        }
        usuarioLogado.setSenha(encoder.encode(dto.novaSenha()));
        usuarioRepository.save(usuarioLogado);
    }

    public void alterarDados(Usuario usuarioLogado, AlteracaoDeDadosRequestDTO dto) {
        if(dto.telefone() != null && !dto.telefone().isBlank()){
            usuarioLogado.setTelefone(dto.telefone());
        }

        if (dto.endereco() != null) {

            // Busca o endereço principal atual do usuário. Se ele não tiver nenhum, cria um novo.
            Endereco enderecoPrincipal = usuarioLogado.getEnderecos().stream()
                    .filter(Endereco::getPrincipal)
                    .findFirst()
                    .orElse(new Endereco());

            // Aplica os dados do DTO para a entidade Endereco
            if (dto.endereco().cep() != null) enderecoPrincipal.setCep(dto.endereco().cep());
            if (dto.endereco().logradouro() != null) enderecoPrincipal.setLogradouro(dto.endereco().logradouro());
            if (dto.endereco().numero() != null) enderecoPrincipal.setNumero(dto.endereco().numero());
            if (dto.endereco().bairro() != null) enderecoPrincipal.setBairro(dto.endereco().bairro());
            if (dto.endereco().cidade() != null) enderecoPrincipal.setCidade(dto.endereco().cidade());
            if (dto.endereco().uf() != null) enderecoPrincipal.setUf(dto.endereco().uf());
            if (dto.endereco().descricao() != null) enderecoPrincipal.setDescricao(dto.endereco().descricao());

            // Verifica se é endereço recém criado
            if (enderecoPrincipal.getId() == null) {
                enderecoPrincipal.setPrincipal(true);
                enderecoPrincipal.setUsuario(usuarioLogado);
                usuarioLogado.getEnderecos().add(enderecoPrincipal);
            }
        }
        usuarioRepository.save(usuarioLogado);
    }

    public DadosUsuarioLogadoResponseDTO dados(Usuario usuarioLogado) {
        Usuario usuario = usuarioRepository.findByEmailComEnderecos(usuarioLogado.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado no banco de dados."));
        return new DadosUsuarioLogadoResponseDTO(usuario);
    }
}
