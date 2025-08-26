CREATE TABLE product (
     id serial PRIMARY KEY,
     name text NOT NULL,
     price numeric(12,2) NOT NULL,
     stock integer NOT NULL
);


CREATE TABLE customer_order (
    id serial PRIMARY KEY,
    public_id uuid NOT NULL UNIQUE,
    product_id integer NOT NULL,
    qty integer NOT NULL,
    status text NOT NULL,
    created_at timestamptz NOT NULL DEFAULT now()
);

-- seed
INSERT INTO product (name, price, stock) VALUES
     ('FastPhone', 499.99, 1000),
     ('ProLaptop', 1299.00, 500);