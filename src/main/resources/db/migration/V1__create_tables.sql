DROP TABLE username;

CREATE TABLE Username (
    id BIGINT PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE Booking (
    id BIGINT PRIMARY KEY,
    type VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL
);

CREATE TABLE Book (
    id BIGINT PRIMARY KEY,
    year_of_publ SMALLINT NOT NULL,
    publ_house VARCHAR(255) NOT NULL,
    lang VARCHAR(255) NOT NULL,
    condit VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL
);

CREATE TABLE Сreation (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    writer VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL
    year_of_publ SMALLINT NOT NULL
);