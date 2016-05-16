CREATE TABLE IF NOT EXISTS cart_table (
variant_id TEXT UNIQUE PRIMARY KEY NOT NULL,
product_id TEXT,
weight TEXT,
mrp_price TEXT,
price TEXT,
discount TEXT,
unit_type TEXT,
quantity TEXT,
tax TEXT
);
