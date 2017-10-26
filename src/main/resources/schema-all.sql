DROP TABLE gotcharacters IF EXISTS;

CREATE TABLE gotcharacters  (
    got_id BIGINT IDENTITY NOT NULL PRIMARY KEY,
    first_name VARCHAR(20),
    last_name VARCHAR(20)
);