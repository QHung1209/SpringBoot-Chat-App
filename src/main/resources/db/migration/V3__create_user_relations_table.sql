
CREATE TABLE user_relations (
  id uuid PRIMARY KEY,
  user_low_id uuid NOT NULL,
  user_high_id uuid NOT NULL,
  status varchar(20) NOT NULL,
  action_user_id uuid NOT NULL,
  created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT fk_user_relations_user_low_id FOREIGN KEY (user_low_id) REFERENCES users(id),
  CONSTRAINT fk_user_relations_user_high_id FOREIGN KEY (user_high_id) REFERENCES users(id),
  CONSTRAINT fk_user_relations_action_user_id FOREIGN KEY (action_user_id) REFERENCES users(id),

  CONSTRAINT uq_user_relations_pair UNIQUE (user_low_id, user_high_id),
  CONSTRAINT chk_no_self_relation CHECK (user_low_id <> user_high_id),
  CONSTRAINT chk_user_relations_status CHECK (status IN ('PENDING', 'ACCEPTED', 'BLOCKED'))
);
