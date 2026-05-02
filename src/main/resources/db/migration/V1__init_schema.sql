CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    department VARCHAR(255),
    role VARCHAR(50) NOT NULL,
    total_permission_days INTEGER NOT NULL
);

CREATE TABLE permission (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    permission_type VARCHAR(50),
    permission_status VARCHAR(50),
    day_count INTEGER NOT NULL,
    create_date DATE,
    CONSTRAINT fk_permission_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);
