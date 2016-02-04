CREATE TABLE IF NOT EXISTS store (
id TEXT UNIQUE PRIMARY KEY NOT NULL,
store_name TEXT,
location TEXT,
city TEXT,
state TEXT,
country TEXT,
zipcode TEXT,
contact_person TEXT,
contact_number TEXT,
banner TEXT,
about_us TEXT,
version TEXT,
lat TEXT,
lng TEXT,
otp_skip TEXT,
store_status TEXT,
android_app_share TEXT,
type TEXT,
theme TEXT
);

