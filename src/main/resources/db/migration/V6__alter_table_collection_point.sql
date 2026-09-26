ALTER TABLE collection_points
    ADD status VARCHAR(100);

ALTER TABLE collection_points
    DROP COLUMN is_active;

ALTER TABLE collection_points
    DROP COLUMN is_pending;