CREATE TABLE IF NOT EXISTS orders (
    id BIGINT(20) NOT NULL AUTO_INCREMENT,
    book_isbn VARCHAR(255) NOT NULL,
    book_name VARCHAR(255),
    book_price FLOAT8,
    quantity INT NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_date TIMESTAMP NOT NULL,
    last_modified_date TIMESTAMP NOT NULL,
    version INT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;