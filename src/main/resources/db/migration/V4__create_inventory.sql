CREATE TABLE inventories (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    quantity INTEGER NOT NULL,
    updated_at TIMESTAMP NOT NULL,

    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT fk_inventory_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),

    CONSTRAINT unique_product_warehouse UNIQUE (product_id, warehouse_id)
);

CREATE INDEX idx_inventory_product ON inventories(product_id);
CREATE INDEX idx_inventory_warehouse ON inventories(warehouse_id);