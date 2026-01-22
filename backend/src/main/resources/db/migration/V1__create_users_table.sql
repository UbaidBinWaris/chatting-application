-- Create users table with encrypted fields
-- Version 2: User management with encryption

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,

    -- Encrypted fields (stored as TEXT to accommodate encrypted data)
    username_encrypted TEXT NOT NULL,
    email_encrypted TEXT NOT NULL,

    -- Hashed password (using BCrypt or similar)
    password_hash VARCHAR(255) NOT NULL,

    -- Basic info (non-encrypted)
    display_name VARCHAR(100),
    status VARCHAR(20) DEFAULT 'OFFLINE',
    is_active BOOLEAN DEFAULT true,
    is_verified BOOLEAN DEFAULT false,

    -- Timestamps
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_seen TIMESTAMP,

    -- Indexes for searching (on encrypted fields we'll use hash-based lookups)
    CONSTRAINT unique_username_encrypted UNIQUE (username_encrypted),
    CONSTRAINT unique_email_encrypted UNIQUE (email_encrypted)
);

-- Create index for status queries
CREATE INDEX IF NOT EXISTS idx_users_status ON users(status);
CREATE INDEX IF NOT EXISTS idx_users_active ON users(is_active);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);

