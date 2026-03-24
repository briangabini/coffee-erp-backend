-- V2__upgrade_timestamps_to_tz.sql

-- Update Coffee Beans
ALTER TABLE coffee_beans ALTER COLUMN created_date TYPE TIMESTAMPTZ USING created_date AT TIME ZONE 'UTC';
ALTER TABLE coffee_beans ALTER COLUMN last_modified_date TYPE TIMESTAMPTZ USING created_date AT TIME ZONE 'UTC';

-- Update Suppliers
ALTER TABLE suppliers ALTER COLUMN created_date TYPE TIMESTAMPTZ USING created_date AT TIME ZONE 'UTC';
ALTER TABLE suppliers ALTER COLUMN last_modified_date TYPE TIMESTAMPTZ USING created_date AT TIME ZONE 'UTC';

-- Update Inventory Stocks
ALTER TABLE inventory_stocks ALTER COLUMN created_date TYPE TIMESTAMPTZ USING created_date AT TIME ZONE 'UTC';
ALTER TABLE inventory_stocks ALTER COLUMN last_modified_date TYPE TIMESTAMPTZ USING created_date AT TIME ZONE 'UTC';