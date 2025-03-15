-- Rollback changes to image table
ALTER TABLE public.image 
    DROP COLUMN IF EXISTS image_data,
    DROP COLUMN IF EXISTS content_type,
    DROP COLUMN IF EXISTS file_name;

-- Restore the original column
ALTER TABLE public.image 
    ADD COLUMN imageurl VARCHAR(255); 