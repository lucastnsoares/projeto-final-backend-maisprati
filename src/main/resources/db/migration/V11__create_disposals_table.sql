CREATE TABLE disposals (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    collection_point_id BIGINT NOT NULL,
    approximate_weight_in_kg NUMERIC(10, 2),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    confirmed_by_id BIGINT,
    confirmed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_disposal_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_disposal_collection_point FOREIGN KEY (collection_point_id) REFERENCES collection_points(id),
    CONSTRAINT fk_disposal_confirmed_by FOREIGN KEY (confirmed_by_id) REFERENCES users(id)
);

-- Tabela associativa para permitir vários tipos de tecido num mesmo descarte
CREATE TABLE disposal_cloth_types (
    disposal_id BIGINT NOT NULL,
    cloth_type_id BIGINT NOT NULL,
    PRIMARY KEY (disposal_id, cloth_type_id),
    CONSTRAINT fk_dct_disposal FOREIGN KEY (disposal_id) REFERENCES disposals(id),
    CONSTRAINT fk_dct_cloth_type FOREIGN KEY (cloth_type_id) REFERENCES cloth_types(id)
);