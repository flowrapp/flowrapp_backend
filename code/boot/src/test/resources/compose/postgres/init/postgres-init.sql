
CREATE SEQUENCE users_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;

CREATE TABLE if not exists users
(
    id   serial NOT NULL,
    dni  varchar(255),
    name varchar(255)
);

CREATE INDEX IF NOT EXISTS idx_users_dni_name ON users (dni, name);

INSERT INTO users (dni, name)
VALUES ('21242', 'divios');