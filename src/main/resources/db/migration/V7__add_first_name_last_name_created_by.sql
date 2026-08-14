ALTER TABLE users ADD COLUMN IF NOT EXISTS first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_name VARCHAR(100);

UPDATE users 
SET first_name = COALESCE(NULLIF(split_part(full_name, ' ', 1), ''), 'User'),
    last_name = NULLIF(substr(full_name, length(split_part(full_name, ' ', 1)) + 2), '')
WHERE full_name IS NOT NULL AND first_name IS NULL;

ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;
ALTER TABLE users DROP COLUMN IF EXISTS full_name;

ALTER TABLE conversation_members ADD COLUMN IF NOT EXISTS created_by UUID;
