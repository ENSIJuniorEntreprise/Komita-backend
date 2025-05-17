-- First, add a new column for the enum type
ALTER TABLE service_table ADD COLUMN state_enum VARCHAR(20);

-- Convert existing boolean values to enum values
UPDATE service_table SET state_enum = 
    CASE 
        WHEN state = true THEN 'ACTIVE' 
        WHEN state = false THEN 'INACTIVE'
        ELSE 'INACTIVE'
    END;

-- Drop the old column
ALTER TABLE service_table DROP COLUMN state;

-- Rename the new column to the original name
ALTER TABLE service_table RENAME COLUMN state_enum TO state;