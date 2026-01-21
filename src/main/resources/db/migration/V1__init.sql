CREATE TABLE IF NOT EXISTS user
(
    id         VARCHAR(255) PRIMARY KEY,
    name       VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    email      VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS authority
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    authority VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS user_authority
(
    user_id      VARCHAR(255) NOT NULL,
    authority_id BIGINT       NOT NULL,
    PRIMARY KEY (user_id, authority_id),
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (authority_id) REFERENCES authority (id) ON DELETE CASCADE
);


CREATE TABLE IF NOT EXISTS currency
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    VARCHAR(255) NOT NULL,
    name       VARCHAR(50)  NOT NULL,
    symbol     VARCHAR(10)  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS strategy_type
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    VARCHAR(255) NOT NULL,
    name        VARCHAR(50) NOT NULL ,
    description VARCHAR(255), created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS category
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(255) NOT NULL,
    name        VARCHAR(50)  NOT NULL,
    description VARCHAR(255),
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS income
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(255)   NOT NULL,
    currency_id BIGINT         NOT NULL,
    amount      DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    date        DATE           NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES currency (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS expense
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(255)   NOT NULL,
    currency_id BIGINT         NOT NULL,
    category_id BIGINT         NOT NULL,
    amount      DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    date        DATE           NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES currency (id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES category (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS investment
(
    id               BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id          VARCHAR(255)   NOT NULL,
    currency_id      BIGINT         NOT NULL,
    amount           DECIMAL(10, 2) NOT NULL,
    strategy_type_id BIGINT         NOT NULL,
    description      VARCHAR(255),
    date             DATE           NOT NULL,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES currency (id) ON DELETE CASCADE,
    FOREIGN KEY (strategy_type_id) REFERENCES strategy_type (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS wish
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(255)   NOT NULL,
    currency_id BIGINT         NOT NULL,
    amount      DECIMAL(10, 2) NOT NULL,
    description VARCHAR(255),
    date        DATE           NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES currency (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS loan
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id       VARCHAR(255)   NOT NULL,
    currency_id   BIGINT         NOT NULL,
    amount        DECIMAL(10, 2) NOT NULL,
    interest_rate DECIMAL(5, 2)  NOT NULL,
    description   VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES currency (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS objective
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id     VARCHAR(255)   NOT NULL,
    currency_id BIGINT         NOT NULL,
    amount      DECIMAL(10, 2) NULL,
    description VARCHAR(255),
    target      INTEGER           NOT NULL,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
    FOREIGN KEY (currency_id) REFERENCES currency (id) ON DELETE CASCADE
);







