CREATE TABLE sales (
    id BIGSERIAL PRIMARY KEY,
    warehouse_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    total_amount NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_sales_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
    CONSTRAINT fk_sales_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE INDEX idx_sales_warehouse ON sales(warehouse_id);
CREATE INDEX idx_sales_created_at ON sales(created_at);