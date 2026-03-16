CREATE TABLE greeting_log
(
    id         BIGSERIAL    NOT NULL,
    name       VARCHAR(255) NOT NULL,
    message    VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_greeting_log PRIMARY KEY (id)
);
