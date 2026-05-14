CREATE TABLE payment_method (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    order_id VARCHAR(36) NOT NULL UNIQUE,

    type VARCHAR(20) NOT NULL,

    iban VARCHAR(34),

    CONSTRAINT fk_payment_order
        FOREIGN KEY (order_id)
        REFERENCES orders(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_payment_method
        CHECK (
            (type = 'INVOICE' AND iban IS NULL)
            OR
            (type = 'DIRECT_DEBIT' AND iban IS NOT NULL)
        )
);