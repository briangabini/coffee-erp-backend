-- New Tables
CREATE TABLE authority
(
    id         UUID PRIMARY KEY NOT NULL,
    permission VARCHAR(255)
);

CREATE TABLE role
(
    id   UUID PRIMARY KEY NOT NULL,
    name VARCHAR(255)
);

CREATE TABLE users
(
    id                      UUID PRIMARY KEY    NOT NULL,
    username                VARCHAR(255)        NOT NULL,
    password                VARCHAR(255)        NOT NULL,
    account_non_expired     BOOLEAN DEFAULT TRUE,
    account_non_locked      BOOLEAN DEFAULT TRUE,
    credentials_non_expired BOOLEAN DEFAULT TRUE,
    enabled                 BOOLEAN DEFAULT TRUE
);

-- Associations
CREATE TABLE role_authority
(
    authority_id UUID NOT NULL,
    role_id      UUID NOT NULL,
    PRIMARY KEY (authority_id, role_id),
    CONSTRAINT fk_role_auth_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_role_auth_auth FOREIGN KEY (authority_id) REFERENCES authority (id)
);

CREATE TABLE user_role
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    PRIMARY KEY (role_id, user_id),
    CONSTRAINT fk_user_role_role FOREIGN KEY (role_id) REFERENCES role (id),
    CONSTRAINT fk_user_role_user FOREIGN KEY (user_id) REFERENCES users (id)
);
