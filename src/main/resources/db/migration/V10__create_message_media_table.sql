-- Tạo bảng junction message_media
CREATE TABLE message_media (
  message_id UUID NOT NULL,
  media_id   UUID NOT NULL,
  position   INT  NOT NULL DEFAULT 0,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  PRIMARY KEY (message_id, media_id),

  CONSTRAINT fk_message_media_message
    FOREIGN KEY (message_id) REFERENCES messages(id) ON DELETE CASCADE,

  CONSTRAINT fk_message_media_media
    FOREIGN KEY (media_id) REFERENCES media(id) ON DELETE CASCADE
);

CREATE INDEX idx_message_media_message_id ON message_media(message_id);

-- Migrate data cũ từ messages.media_id
INSERT INTO message_media (message_id, media_id, position)
SELECT id, media_id, 0
FROM messages
WHERE media_id IS NOT NULL;

-- Xóa cột media_id cũ
ALTER TABLE messages DROP COLUMN media_id;
