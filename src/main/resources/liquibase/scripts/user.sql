-- liquibase formatted sql

-- changeset aklfirst_bot:1
CREATE TABLE IF NOT EXISTS notification_task
(
    id        BIGSERIAL PRIMARY KEY,
    chat_id   BIGINT NOT NULL,
    message   VARCHAR NOT NULL,
    date_time TIMESTAMP NOT NULL
)

