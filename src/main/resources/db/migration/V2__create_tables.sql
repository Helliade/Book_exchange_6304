--DROP TABLE username;

CREATE TABLE Username (
    id BIGINT PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);
