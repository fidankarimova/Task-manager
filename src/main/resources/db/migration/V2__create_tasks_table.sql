CREATE TABLE tasks (
                       id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       description VARCHAR(1000),
                       status VARCHAR(255) CHECK (status IN ('TODO', 'IN_PROGRESS', 'DONE')),
                       created_at TIMESTAMP,
                       user_id BIGINT NOT NULL,
                       CONSTRAINT fk_tasks_user FOREIGN KEY (user_id) REFERENCES users(id)
);