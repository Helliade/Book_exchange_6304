
CREATE TABLE Booking (
    id BIGINT PRIMARY KEY,
    type VARCHAR(50) NOT NULL CHECK (type IN ('GIVE', 'TAKE')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('CREATED', 'IN_TRANSIT', 'DELIVERY_READY', 'COMPLETED')),
    user_id BIGINT NOT NULL
);

CREATE TABLE Book (
    id BIGINT PRIMARY KEY,
    year_of_publ SMALLINT NOT NULL,
    publ_house VARCHAR(255) NOT NULL,
    lang VARCHAR(255) NOT NULL,
    condit VARCHAR(50) NOT NULL CHECK (condition IN ('PERFECT', 'HAS_FLAWS', 'DAMAGED')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('FREE', 'BOOKED', 'OUT'))
);

CREATE TABLE Сreation (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    writer VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL
    year_of_publ SMALLINT NOT NULL
);

-- Надо поправить некоторые поля на "выбор варианта"