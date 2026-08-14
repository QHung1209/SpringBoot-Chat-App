ALTER TABLE users ADD COLUMN first_name VARCHAR(100);
ALTER TABLE users ADD COLUMN last_name VARCHAR(100);

UPDATE users 
SET first_name = COALESCE(NULLIF(split_part(full_name, ' ', 1), ''), 'User'),
    last_name = NULLIF(substr(full_name, length(split_part(full_name, ' ', 1)) + 2), '')
WHERE full_name IS NOT NULL;

ALTER TABLE users ALTER COLUMN first_name SET NOT NULL;

ALTER TABLE users DROP COLUMN full_name;
