ALTER TABLE category ADD COLUMN is_enable TEXT DEFAULT (1);
ALTER TABLE category ADD COLUMN is_deleted TEXT DEFAULT (0);
ALTER TABLE product ADD COLUMN is_enable TEXT DEFAULT (1);
ALTER TABLE product ADD COLUMN is_deleted TEXT DEFAULT (0);
ALTER TABLE product ADD COLUMN sort_order INTEGER DEFAULT (0);
ALTER TABLE category ADD COLUMN sort_order INTEGER DEFAULT (0);
ALTER TABLE sub_category ADD COLUMN sort_order INTEGER DEFAULT (0)