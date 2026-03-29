CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    sale_id BIGINT NOT NULL,
    amount NUMERIC(10,2) NOT NULL,
    payment_method VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    due_date DATE,
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_payment_sale FOREIGN KEY (sale_id) REFERENCES sales(id)
);

CREATE INDEX idx_payments_sale ON payments(sale_id);
CREATE INDEX idx_payments_created_at ON payments(created_at);
CREATE INDEX idx_payments_payment_method ON payments(payment_method);
CREATE INDEX idx_payment_method_created ON payments(payment_method, created_at);