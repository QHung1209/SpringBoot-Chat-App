CREATE TABLE user_devices (
  id uuid PRIMARY KEY,
  user_id uuid NOT NULL,
  name VARCHAR(255),
  user_agent TEXT,
  ip_address VARCHAR(45),
  os VARCHAR(255),
  browser VARCHAR(255),
  token_version INTEGER,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_devices_user_id ON user_devices(user_id);
