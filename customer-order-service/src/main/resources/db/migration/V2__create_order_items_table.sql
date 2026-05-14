CREATE TABLE order_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    order_id VARCHAR(36) NOT NULL,

    product_offering_id VARCHAR(100) NOT NULL,

    quantity INT NOT NULL CHECK (quantity >= 1),

    CONSTRAINT fk_order_items_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE
);