ALTER TABLE posts
ADD COLUMN deletion_justification VARCHAR(255);

ALTER TABLE posts
ADD COLUMN deleted_by_user_id BIGINT;

ALTER TABLE posts ADD CONSTRAINT fk_posts_deleted_by
FOREIGN KEY (deleted_by_id) REFERENCES users(id)
ON DELETE SET NULL;