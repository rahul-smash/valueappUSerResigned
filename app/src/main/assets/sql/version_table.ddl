CREATE TABLE IF NOT EXISTS version_table (
id TEXT UNIQUE PRIMARY KEY NOT NULL,
type TEXT,
db_version TEXT,
identifier_id TEXT,
random_number INTEGER
);
