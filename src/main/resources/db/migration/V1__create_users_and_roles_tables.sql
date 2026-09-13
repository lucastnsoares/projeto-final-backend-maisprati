-- Tabela de Usuários
CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(120) NOT NULL,
                       document VARCHAR(20) NOT NULL UNIQUE,
                       email VARCHAR(120) NOT NULL UNIQUE,
                       password_hash VARCHAR(64) NOT NULL,
                       phone VARCHAR(20),
                       active BOOLEAN NOT NULL DEFAULT TRUE,
                       avatar_url VARCHAR(255),
                       created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP WITH TIME ZONE
);

-- Tabela auxiliar de Roles do Usuário
CREATE TABLE users_roles (
                             user_id BIGINT NOT NULL,
                             role VARCHAR(60) NOT NULL,
                             PRIMARY KEY (user_id, role),
                             CONSTRAINT fk_users_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);