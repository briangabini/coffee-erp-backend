CREATE TABLE coffee_beans (
    id uuid NOT null,
    version integer,
    created_date timestamp(6),
    last_modified_date timestamp(6),
    name varchar(255) NOT NULL,
    origin varchar(255) NOT NULL,
    roast_level varchar(255) NOT NULL,
    price_per_kg numeric(38,2) NOT NULL CHECK (price_per_kg >= 0),
    PRIMARY KEY (id)
);

CREATE TABLE suppliers (
    version integer,
    created_date timestamp(6),
    last_modified_date timestamp(6),
    id uuid NOT NULL,
    contact_email varchar(255) NOT NULL,
    name varchar(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE coffee_bean_supplier (
    coffee_beans_id uuid not null,
    suppliers_id uuid not null,
    PRIMARY KEY (coffee_beans_id, suppliers_id)
);

CREATE TABLE inventory_stocks (
    expiry_date date,
    quantity_grams integer NOT NULL CHECK (quantity_grams >= 0),
    version integer,
    created_date timestamp(6),
    last_modified_date timestamp(6),
    bean_id uuid NOT NULL,
    id uuid NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS coffee_bean_supplier
    ADD CONSTRAINT FK6p48ttw34wx04512slf3pvae9
    FOREIGN KEY (suppliers_id)
    REFERENCES suppliers

ALTER TABLE IF EXISTS coffee_bean_supplier
    ADD CONSTRAINT FKr3v13kpltigwe56ur1ssqstk8
    FOREIGN KEY (coffee_beans_id)
    REFERENCES coffee_beans

ALTER TABLE IF EXISTS inventory_stocks
    ADD CONSTRAINT FKaeiwpy336gcpbru0tjnju937j
    FOREIGN KEY (bean_id)
    REFERENCES coffee_beans

