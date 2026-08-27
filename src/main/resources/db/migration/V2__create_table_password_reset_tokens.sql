CREATE TABLE password_reset_tokens (
                                       id BIGSERIAL PRIMARY KEY,
                                       token VARCHAR(100) NOT NULL UNIQUE,
                                       user_id BIGINT NOT NULL,
                                       expiration_date TIMESTAMP WITH TIME ZONE NOT NULL,
                                       CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);