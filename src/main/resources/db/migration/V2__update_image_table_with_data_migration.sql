-- Create temporary column to store old URLs
ALTER TABLE public.image ADD COLUMN temp_url VARCHAR(255);

-- Backup existing URLs
UPDATE public.image SET temp_url = imageurl WHERE imageurl IS NOT NULL;

-- Drop the existing imageurl column
ALTER TABLE public.image DROP COLUMN imageurl;

-- Add new columns for BLOB storage
ALTER TABLE public.image 
    ADD COLUMN image_data bytea,
    ADD COLUMN content_type VARCHAR(255),
    ADD COLUMN file_name VARCHAR(255);

-- Add comments
COMMENT ON TABLE public.image IS 'Store images as BLOBs with metadata';
COMMENT ON COLUMN public.image.image_data IS 'Binary content of the image';
COMMENT ON COLUMN public.image.content_type IS 'MIME type of the image';
COMMENT ON COLUMN public.image.file_name IS 'Original filename of the image';

-- Note: You'll need to handle the actual data migration in your application code
-- The temp_url column can be used to migrate the data
-- After migration is complete, you can remove the temp_url column using:
-- ALTER TABLE public.image DROP COLUMN temp_url; 