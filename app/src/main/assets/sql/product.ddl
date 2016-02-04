CREATE TABLE IF NOT EXISTS product (
id TEXT UNIQUE PRIMARY KEY NOT NULL,
store_id TEXT,
category_ids TEXT,
title TEXT,
brand TEXT,
nutrient TEXT,
description TEXT,
image TEXT,
show_price TEXT,
favorites TEXT,
image_100_80 TEXT,
image_300_200 TEXT,
variants TEXT,
selectedVariant TEXT
);

