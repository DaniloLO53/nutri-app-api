ALTER TABLE notifications ADD COLUMN sender_id UUID;
ALTER TABLE notifications ADD CONSTRAINT fk_sender FOREIGN KEY(sender_id) REFERENCES users(id) ON DELETE CASCADE;