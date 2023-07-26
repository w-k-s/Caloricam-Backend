CREATE TABLE users
(
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (username)
);

CREATE TABLE foods
(
    food_id BIGINT       NOT NULL AUTO_INCREMENT,
    name    VARCHAR(255) NOT NULL,
    PRIMARY KEY (food_id),
    UNIQUE KEY unique_name (name)
);

CREATE TABLE images
(
    image_id     VARCHAR(255) NOT NULL,
    food_id      BIGINT,
    size         BIGINT       NOT NULL,
    is_finalized BOOLEAN      NOT NULL,
    PRIMARY KEY (image_id),
    FOREIGN KEY (food_id) REFERENCES foods (food_id) ON DELETE SET NULL
);

INSERT INTO users (username, password) VALUES ('admin','123456');