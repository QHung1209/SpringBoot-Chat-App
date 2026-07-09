CREATE TABLE conversations (
  id uuid PRIMARY KEY,
  type VARCHAR(20) NOT NULL,
  name VARCHAR(255),
  avatar_url TEXT,
  created_by UUID NOT NULL,
  pair_key VARCHAR(32) UNIQUE,
  last_message_id uuid,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_conversations_updated_at
ON conversations (updated_at DESC);

CREATE TABLE conversation_members (
  id UUID PRIMARY KEY,
  name VARCHAR(255),
  conversation_id uuid NOT NULL,
  user_id uuid NOT NULL,
  role VARCHAR(20) DEFAULT 'MEMBER',
  hidden_at_message_id uuid DEFAULT NULL,
  last_read_message_id uuid,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_conversation_members_conversation_id FOREIGN KEY (conversation_id) REFERENCES conversations(id),
  CONSTRAINT fk_conversation_members_user_id FOREIGN KEY (user_id) REFERENCES users(id)
);

ALTER TABLE conversation_members 
ADD CONSTRAINT uk_conversation_user UNIQUE (conversation_id, user_id);

ALTER TABLE conversation_members 
ADD CONSTRAINT chk_conversation_role 
CHECK (role IN ('ADMIN', 'MEMBER'));

CREATE TABLE messages (
  id UUID PRIMARY KEY,
  conversation_id UUID NOT NULL,
  sender_id UUID NOT NULL,
  type VARCHAR(20) DEFAULT 'TEXT',
  content TEXT NOT NULL,
  metadata JSONB,
  reply_to_message_id uuid DEFAULT NULL,
  media_id uuid DEFAULT NULL,
  is_pinned BOOLEAN DEFAULT FALSE,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  deleted_at TIMESTAMPTZ DEFAULT NULL,

  CONSTRAINT fk_messages_conversation_id FOREIGN KEY (conversation_id) REFERENCES conversations(id),
  CONSTRAINT fk_messages_sender_id FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE INDEX idx_messages_conversation_created ON messages(conversation_id, created_at DESC);

ALTER TABLE conversations 
ADD CONSTRAINT fk_last_message_id 
FOREIGN KEY (last_message_id) 
REFERENCES messages(id);

CREATE TABLE message_reactions (
    message_id UUID NOT NULL,
    user_id UUID NOT NULL,
    reaction VARCHAR(20) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (message_id, user_id),

    CONSTRAINT fk_message_reactions_message
        FOREIGN KEY (message_id)
        REFERENCES messages(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_message_reactions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);
