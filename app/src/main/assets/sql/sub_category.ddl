CREATE TABLE IF NOT EXISTS sub_category (
id TEXT UNIQUE PRIMARY KEY NOT NULL,
title TEXT,
image TEXT,
parent_id TEXT,
product_id TEXT,
version TEXT,
old_version TEXT,
image_small TEXT,
image_medium TEXT
);
