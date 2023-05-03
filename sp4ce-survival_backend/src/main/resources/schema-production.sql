-- TABLES --
CREATE TABLE IF NOT EXISTS users(
    id uuid NOT NULL DEFAULT GEN_RANDOM_UUID() UNIQUE,
    username VARCHAR(25) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(75) NOT NULL UNIQUE,
    role VARCHAR(15) NOT NULL,
    created_at DATE NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS scores(
    id uuid NOT NULL DEFAULT GEN_RANDOM_UUID() UNIQUE,
    user_id uuid NOT NULL,
    score_number BIGINT NOT NULL,
    date_obtained DATE NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);