ALTER TABLE message_reactions
DROP CONSTRAINT message_reactions_pkey;

ALTER TABLE message_reactions
ADD COLUMN id UUID,
ADD COLUMN updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

UPDATE message_reactions
SET id = gen_random_uuid()
WHERE id IS NULL;

ALTER TABLE message_reactions
ALTER COLUMN id SET NOT NULL;

ALTER TABLE message_reactions
ADD CONSTRAINT message_reactions_pkey PRIMARY KEY (id);

ALTER TABLE message_reactions
ADD CONSTRAINT uq_message_reactions_message_user UNIQUE (message_id, user_id);
