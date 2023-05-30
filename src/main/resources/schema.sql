DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_user_email UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    description VARCHAR(512) NOT NULL,
    user_id BIGINT NOT NULL,
    created TIMESTAMP NOT NULL,
    CONSTRAINT pk_requests PRIMARY KEY (id),
    CONSTRAINT fk_requests_user_id FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    owner_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(512) NOT NULL,
    available BOOLEAN,
    review_id BIGINT,
    request_id BIGINT,
    CONSTRAINT pk_items PRIMARY KEY (id),
    CONSTRAINT fk_items_user_id FOREIGN KEY (owner_id) REFERENCES users (id),
    CONSTRAINT fk_items_request_id FOREIGN KEY (request_id) REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    start_date TIMESTAMP,
    end_date TIMESTAMP,
    item_id BIGINT,
    booker_id BIGINT,
    status VARCHAR,
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_bookings_user_id FOREIGN KEY (booker_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    text VARCHAR(512) NOT NULL,
    item_id BIGINT,
    author_id BIGINT,
    created TIMESTAMP,
    CONSTRAINT pk_comments PRIMARY KEY (id),
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items (id),
    CONSTRAINT fk_comments_user_id FOREIGN KEY (author_id) REFERENCES users (id)
);