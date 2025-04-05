CREATE TABLE Username (
    id BIGINT PRIMARY KEY,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

INSERT INTO Username (id, login, password) VALUES
(2, 'Daria', '87654321');

CREATE TABLE Booking (
    id BIGINT PRIMARY KEY,
    type VARCHAR(50) NOT NULL CHECK (type IN ('GIVE', 'TAKE')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('CREATED', 'IN_TRANSIT', 'DELIVERY_READY', 'COMPLETED')),
    user_id BIGINT NOT NULL REFERENCES Username(id)
);

CREATE TABLE Booking_Book (
  Booking_id BIGINT REFERENCES Booking(id),
  Book_id BIGINT REFERENCES Book(id),
  PRIMARY KEY (Booking_id, Book_id)
);

CREATE TABLE Book (
    id BIGINT PRIMARY KEY,
    year_of_publ SMALLINT NOT NULL,
    publ_house VARCHAR(255) NOT NULL,
    lang VARCHAR(255) NOT NULL,
    condit VARCHAR(50) NOT NULL CHECK (condit IN ('PERFECT', 'HAS_FLAWS', 'DAMAGED')),
    status VARCHAR(50) NOT NULL CHECK (status IN ('FREE', 'BOOKED', 'OUT'))
);

CREATE TABLE Book_Creation (
  Book_id BIGINT REFERENCES Book(id),
  Creation_id BIGINT REFERENCES Creation(id),
  PRIMARY KEY (Book_id, Creation_id)
);      -- Надежная связь между студентом и курсом

CREATE TABLE Creation (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    writer VARCHAR(255) NOT NULL,
    genre VARCHAR(255) NOT NULL,
    year_of_publ SMALLINT NOT NULL
);

-- Надо поправить некоторые поля на "выбор варианта"