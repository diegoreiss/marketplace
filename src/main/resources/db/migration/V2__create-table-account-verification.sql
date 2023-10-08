CREATE TABLE tb_account_verification(
    id VARCHAR(100) PRIMARY KEY UNIQUE NOT NULL,
    verification_token VARCHAR(100) UNIQUE NOT NULL,
    tb_app_user_id VARCHAR(100) UNIQUE NOT NULL,

    CONSTRAINT fk_account_verification_app_user
        FOREIGN KEY (tb_app_user_id)
        REFERENCES tb_app_user(id)
        ON DELETE CASCADE
);
