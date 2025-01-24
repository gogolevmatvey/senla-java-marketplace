CREATE TYPE user_role AS ENUM ('USER', 'ADMIN');
CREATE TYPE ads_status AS ENUM ('ACTIVE', 'INACTIVE', 'SOLD', 'DELETED');

CREATE TABLE users (
    user_id BIGSERIAL PRIMARY KEY,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    user_email VARCHAR(255) NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    user_role user_role,
    user_seller_rating DOUBLE PRECISION DEFAULT 0.0,
    user_balance DOUBLE PRECISION DEFAULT 0.0,
    user_picture BYTEA
);

CREATE TABLE advertisements (
    ads_id BIGSERIAL PRIMARY KEY,
    ads_title VARCHAR(255) NOT NULL,
    ads_category VARCHAR(255) NOT NULL,
    ads_description VARCHAR(1000) NOT NULL,
    ads_price DOUBLE PRECISION NOT NULL,
    user_id BIGINT REFERENCES users(user_id),
    buyer_id BIGINT REFERENCES users(user_id),
    ads_creation_date DATE,
    ads_promotion_end_date DATE,
    ads_status ads_status,
    ads_main_image BYTEA,
    is_promoted BOOLEAN NOT NULL DEFAULT FALSE,
    promotion_start_date DATE
);

CREATE TABLE ads_images (
    id BIGSERIAL PRIMARY KEY,
    ads_id BIGINT REFERENCES advertisements(ads_id),
    image BYTEA
);

CREATE TABLE comments (
    comment_id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(user_id),
    ads_id BIGINT REFERENCES advertisements(ads_id),
    comment_content TEXT NOT NULL,
    comment_rating INTEGER,
    comment_creation_date DATE
);

CREATE TABLE chats (
    chat_id BIGSERIAL PRIMARY KEY,
    ads_id BIGINT REFERENCES advertisements(ads_id),
    buyer_id BIGINT REFERENCES users(user_id)
);

CREATE TABLE messages (
    message_id BIGSERIAL PRIMARY KEY,
    chat_id BIGINT REFERENCES chats(chat_id),
    sender_id BIGINT REFERENCES users(user_id),
    receiver_id BIGINT REFERENCES users(user_id),
    message_content TEXT NOT NULL,
    message_send_date TIMESTAMP
);

CREATE TABLE sale_histories (
    id BIGSERIAL PRIMARY KEY,
    seller_id BIGINT REFERENCES users(user_id),
    buyer_id BIGINT REFERENCES users(user_id),
    ads_id BIGINT REFERENCES advertisements(ads_id),
    sale_date TIMESTAMP,
    sale_price DOUBLE PRECISION
);
