-- Criação da tabela de Usuários
CREATE TABLE usuario (
                         id BIGSERIAL PRIMARY KEY,
                         nome VARCHAR(120) NOT NULL,
                         documento VARCHAR(20) NOT NULL UNIQUE,
                         email VARCHAR(120) NOT NULL UNIQUE,
                         senha VARCHAR(64) NOT NULL,
                         telefone VARCHAR(20),
                         ativo BOOLEAN NOT NULL DEFAULT TRUE,
                         criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         atualizado_em TIMESTAMP WITH TIME ZONE
);

-- Criação da tabela de Endereços
CREATE TABLE endereco (
                          id BIGSERIAL PRIMARY KEY,
                          usuario_id BIGINT NOT NULL,
                          principal BOOLEAN DEFAULT FALSE,
                          descricao VARCHAR(30),
                          cep VARCHAR(10),
                          logradouro VARCHAR(150),
                          numero VARCHAR(20),
                          complemento VARCHAR(50),
                          bairro VARCHAR(60),
                          cidade VARCHAR(60),
                          uf VARCHAR(2),
                          pais VARCHAR(50),
                          latitude DECIMAL(10, 8),
                          longitude DECIMAL(11, 8),
                          criado_em TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          atualizado_em TIMESTAMP WITH TIME ZONE,
                          CONSTRAINT fk_endereco_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- Criação da tabela auxiliar para a coleção de Perfis (Set<Perfil>) do Usuário
CREATE TABLE usuario_perfil (
                                usuario_id BIGINT NOT NULL,
                                perfil VARCHAR(60) NOT NULL,
                                PRIMARY KEY (usuario_id, perfil),
                                CONSTRAINT fk_usuario_perfil FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);