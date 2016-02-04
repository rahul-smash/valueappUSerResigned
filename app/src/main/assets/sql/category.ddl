CREATE TABLE IF NOT EXISTS category (
id TEXT UNIQUE PRIMARY KEY NOT NULL,
title TEXT,
image TEXT,
sub_category TEXT,
version TEXT,
image_small TEXT,
image_medium TEXT
);

