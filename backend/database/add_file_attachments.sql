-- Migration to add file attachment support to messages

-- Add file-related columns to messages table
ALTER TABLE messages 
ADD COLUMN file_url VARCHAR(500),
ADD COLUMN file_name VARCHAR(255),
ADD COLUMN file_type VARCHAR(100),
ADD COLUMN file_size BIGINT,
ADD COLUMN thumbnail_url VARCHAR(500);

-- Update message_type to support various file types
-- Existing: TEXT
-- New types: IMAGE, VIDEO, DOCUMENT, AUDIO, FILE

-- Add index for better query performance
CREATE INDEX idx_messages_message_type ON messages(message_type);
