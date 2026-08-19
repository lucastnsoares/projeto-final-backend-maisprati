package br.com.maisprati.projeto.model.entity;

import br.com.maisprati.projeto.model.enums.Perfil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public class Usuario {
    private Long id;
    private Set<Perfil> perfil;
    private String nome;
    private String documento;
    private String email;
    private String senha;
    private String telefone;
    private List<Endereco> enderecos;
    private Boolean ativo;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Usuario usuario)) {
            return false;
        }

        return id != null && id.equals(usuario.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
