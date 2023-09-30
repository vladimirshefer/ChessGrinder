CREATE TABLE oauth2_authorized_client
(
    client_registration_id  VARCHAR(100)                            NOT NULL,
    principal_name          VARCHAR(200)                            NOT NULL,
    access_token_type       VARCHAR(100)                            NOT NULL,
    access_token_value      bytea                                   NOT NULL,
    access_token_issued_at  TIMESTAMP                               NOT NULL,
    access_token_expires_at TIMESTAMP                               NOT NULL,
    access_token_scopes     VARCHAR(1000) DEFAULT NULL,
    refresh_token_value     bytea         DEFAULT NULL,
    refresh_token_issued_at TIMESTAMP     DEFAULT NULL,
    created_at              TIMESTAMP     DEFAULT CURRENT_TIMESTAMP NOT NULL,
    PRIMARY KEY (client_registration_id, principal_name)
);
