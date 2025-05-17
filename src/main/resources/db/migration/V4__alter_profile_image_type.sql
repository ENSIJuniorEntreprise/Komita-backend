-- Create a backup table with all data
CREATE TABLE user_table_backup AS SELECT * FROM user_table;

-- Drop the original table with CASCADE to handle foreign key constraints
DROP TABLE user_table CASCADE;

-- Recreate the table with the correct column type
CREATE TABLE user_table (
    id BIGSERIAL PRIMARY KEY,
    custom_identifier VARCHAR(255) UNIQUE,
    firstname VARCHAR(255),
    lastname VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    role VARCHAR(255),
    status BOOLEAN,
    password VARCHAR(255),
    user_address_id BIGINT,
    profile_image bytea,
    enabled BOOLEAN DEFAULT true,
    username VARCHAR(255),
    CONSTRAINT fk_user_address FOREIGN KEY (user_address_id) REFERENCES adress(id)
);

-- Reset the sequence for user_table_id_seq to ensure it starts after the highest existing ID
SELECT setval('user_table_id_seq', COALESCE((SELECT MAX(id) FROM user_table_backup), 0), true);

-- Copy the data back, explicitly specifying the ID to preserve it
INSERT INTO user_table (id, custom_identifier, firstname, lastname, email, role, status, password, user_address_id, enabled, username)
SELECT id, custom_identifier, firstname, lastname, email, role, status, password, user_address_id, true, email
FROM user_table_backup;

-- Drop the backup table
DROP TABLE user_table_backup;

-- Recreate the foreign key constraints
ALTER TABLE verification_token ADD CONSTRAINT fk140t81x9fnhachhvw7b2eaeo6 FOREIGN KEY (user_id) REFERENCES user_table(id);

-- Check if service_table has a user_id column before adding the constraint
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'service_table' AND column_name = 'user_id') THEN
        ALTER TABLE service_table ADD CONSTRAINT fk9xhrag2vrf6lo33u9psiabp1j FOREIGN KEY (user_id) REFERENCES user_table(id);
    END IF;
END $$;

-- Check if consultation has a user_id column before adding the constraint
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'consultation' AND column_name = 'user_id') THEN
        ALTER TABLE consultation ADD CONSTRAINT fklq3u67svn43cgbcfcfe52mjjp FOREIGN KEY (user_id) REFERENCES user_table(id);
    END IF;
END $$;

-- Check if search_history has a user_id column before adding the constraint
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM information_schema.columns 
               WHERE table_name = 'search_history' AND column_name = 'user_id') THEN
        ALTER TABLE search_history ADD CONSTRAINT fkmommtuky12qrnm3dtquai90e3 FOREIGN KEY (user_id) REFERENCES user_table(id);
    END IF;
END $$;


ALTER TABLE service_table ALTER COLUMN description TYPE VARCHAR(3000);