--------------------
-- Users
--------------------

CREATE SEQUENCE user_seq;

CREATE TABLE users (
  id BIGINT PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  password VARCHAR(255)
);

--------------------
-- User Tables
--------------------

CREATE SEQUENCE tables_seq;

CREATE TABLE user_tables (
    id BIGINT PRIMARY KEY,
    table_name VARCHAR(255) NOT NULL,
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

--------------------
-- User Tables
--------------------

CREATE TYPE data_type AS ENUM ('NUMBER', 'BOOL', 'FLOAT', 'STRING', 'DATE');

--------------------
-- Table Fields
--------------------

CREATE SEQUENCE fields_seq;

CREATE TABLE table_fields (
    id BIGINT PRIMARY KEY,
    field_name VARCHAR(255) NOT NULL,
    data_type data_type NOT NULL,
    position INTEGER NOT NULL,
    table_id BIGINT,
    FOREIGN KEY (table_id) REFERENCES user_tables(id) ON DELETE CASCADE
);