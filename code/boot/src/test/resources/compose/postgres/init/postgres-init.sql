-- CREATE SCHEMA
CREATE SCHEMA IF NOT EXISTS flowrapp_management;

-- SEQUENCES

CREATE SEQUENCE flowrapp_management.users_id_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 2147483647
    START 1
    CACHE 1
    NO CYCLE;

-- TABLES

CREATE TABLE if not exists flowrapp_management.mockusers
(
    id   serial NOT NULL,
    dni  varchar(255),
    name varchar(255)
);


CREATE TABLE if not exists flowrapp_management.users
(
    id            integer GENERATED ALWAYS AS IDENTITY,
    name          varchar(255) NOT NULL,
    mail          varchar(320) NOT NULL,
    phone         varchar(15),
    password_hash text         NOT NULL,
    enabled       boolean      NOT NULL DEFAULT true,
    created_at    timestamptz  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id)
);

CREATE TABLE if not exists flowrapp_management.business
(
    id         integer GENERATED ALWAYS AS IDENTITY,
    name       varchar(255) NOT NULL,
    owner_id   integer      NOT NULL,
    longitude  double precision,
    latitude   double precision,
    area       double precision,
    created_at timestamptz  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id),
    FOREIGN KEY (owner_id) REFERENCES flowrapp_management.users
);

CREATE TABLE if not exists flowrapp_management.users_roles
(
    user_id     integer     NOT NULL,
    business_id integer     NOT NULL,
    role        varchar(50) NOT NULL,
    invited_by  integer,
    joined_at   timestamptz NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, business_id),
    FOREIGN KEY (user_id) REFERENCES flowrapp_management.users (id),
    FOREIGN KEY (business_id) REFERENCES flowrapp_management.business (id),
    FOREIGN KEY (invited_by) REFERENCES flowrapp_management.users (id)
);

CREATE TABLE if not exists flowrapp_management.invitations
(
    id          integer GENERATED ALWAYS AS IDENTITY,
    invited     integer     NOT NULL,
    invited_by  integer     NOT NULL,
    business_id integer     NOT NULL,
    token       uuid        NOT NULL DEFAULT gen_random_uuid(),
    role        varchar(50) NOT NULL,
    created_at  timestamptz NOT NULL DEFAULT NOW(),
    expires_at  timestamptz NOT NULL DEFAULT NOW() + INTERVAL '7 days',
    status      varchar(20) NOT NULL DEFAULT 'PENDING',
    PRIMARY KEY (id),
    FOREIGN KEY (invited) REFERENCES flowrapp_management.users (id),
    FOREIGN KEY (business_id) REFERENCES flowrapp_management.business (id),
    FOREIGN KEY (invited_by) REFERENCES flowrapp_management.users (id)
);

CREATE TABLE if not exists flowrapp_management.worklogs
(
    id          integer GENERATED ALWAYS AS IDENTITY,
    user_id     integer     NOT NULL,
    business_id integer     NOT NULL,
    clocked_in  timestamptz NOT NULL,
    clocked_out timestamptz,
    created_at  timestamptz NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES flowrapp_management.users (id),
    FOREIGN KEY (business_id) REFERENCES flowrapp_management.business (id)
);

CREATE TABLE if not exists flowrapp_management.reports
(
    id          integer GENERATED ALWAYS AS IDENTITY,
    user_id     integer          NOT NULL,
    business_id integer          NOT NULL,
    clock_day   DATE             NOT NULL,
    hours       double precision NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES flowrapp_management.users (id),
    FOREIGN KEY (business_id) REFERENCES flowrapp_management.business (id)
);

CREATE TABLE if not exists flowrapp_management.push_tokens
(
    id         integer GENERATED ALWAYS AS IDENTITY,
    user_id    integer      NOT NULL,
    token      uuid         NOT NULL,
    device_id  varchar(255) NOT NULL,
    platform   varchar(50)  NOT NULL,
    created_at timestamptz  NOT NULL DEFAULT NOW(),
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES flowrapp_management.users (id)
);

-- INDEXES

CREATE INDEX IF NOT EXISTS idx_mockusers_dni_name ON flowrapp_management.mockusers (dni, name);

CREATE INDEX IF NOT EXISTS idx_users_name ON flowrapp_management.users (name);
CREATE INDEX IF NOT EXISTS idx_users_mail ON flowrapp_management.users (mail);

CREATE INDEX IF NOT EXISTS idx_business_owner_id ON flowrapp_management.business (owner_id);

CREATE INDEX IF NOT EXISTS idx_users_roles_user_id ON flowrapp_management.users_roles (user_id);
CREATE INDEX IF NOT EXISTS idx_users_roles_business_id ON flowrapp_management.users_roles (business_id);

-- INSERTS

INSERT INTO flowrapp_management.mockusers (dni, name)
VALUES ('21242', 'divios');

INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('admin', 'admin@admin.com', '123456789', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq'); -- Password: adminadmin
INSERT INTO flowrapp_management.users (name, mail, phone, password_hash)
VALUES ('test', 'test@test.com', '123456789', '$2a$10$8w.xERKkZZhKuCMU6K/0x.OmaEYBVqPBfGRHKHfyIEXK4P8kU43fq'); -- Password: 1234

INSERT INTO flowrapp_management.business (name, owner_id, longitude, latitude, area, created_at)
VALUES ('Test Business', 1, 0.0, 0.0, 100.0, NOW());

INSERT INTO flowrapp_management.users_roles (user_id, business_id, role, invited_by, joined_at)
VALUES (1, 1, 'OWNER', 1, NOW());
INSERT INTO flowrapp_management.users_roles (user_id, business_id, role, invited_by, joined_at)
VALUES (2, 1, 'EMPLOYEE', 1, NOW());

INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, created_at, expires_at, status)
VALUES (1, 1, 1, gen_random_uuid(), 'OWNER', NOW(), NOW() + INTERVAL '7 days', 'ACCEPTED');
INSERT INTO flowrapp_management.invitations (invited, invited_by, business_id, token, role, created_at, expires_at, status)
VALUES (1, 2, 1, gen_random_uuid(), 'EMPLOYEE', NOW(), NOW() + INTERVAL '7 days', 'ACCEPTED');

INSERT INTO flowrapp_management.worklogs (user_id, business_id, clocked_in, clocked_out, created_at)
VALUES (2, 1, NOW() - INTERVAL '1 hour', NOW(), NOW());

INSERT INTO flowrapp_management.reports (user_id, business_id, clock_day, hours)
VALUES (2, 1, CURRENT_DATE, 1.0);