-- Adiciona a flag indicando se há uma edição pendente
ALTER TABLE collection_points ADD COLUMN has_pending_update BOOLEAN DEFAULT FALSE;

-- Cria a tabela de auditoria e snapshot
CREATE TABLE collection_point_pending_updates (
    id BIGSERIAL PRIMARY KEY,
    collection_point_id BIGINT NOT NULL UNIQUE, -- Apenas 1 edição pendente por ponto por vez
    update_payload_json TEXT NOT NULL,
    requested_by_user_id BIGINT NOT NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_pending_update_collection_point FOREIGN KEY (collection_point_id) REFERENCES collection_points(id),
    CONSTRAINT fk_pending_update_user FOREIGN KEY (requested_by_user_id) REFERENCES users(id)
);