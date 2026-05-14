CREATE TABLE orders (
    id VARCHAR(36) PRIMARY KEY,

    state VARCHAR(20) NOT NULL,
    category VARCHAR(10) NOT NULL,

    customer_id VARCHAR(50) NOT NULL,
    site_id VARCHAR(50) NOT NULL,

    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);