-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone_number VARCHAR(50),
    status VARCHAR(50) DEFAULT 'OFFLINE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username for faster lookups
CREATE INDEX idx_users_username ON users(username);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on status for filtering online/offline users
CREATE INDEX idx_users_status ON users(status);

-- Add comments to document encrypted/hashed fields
COMMENT ON COLUMN users.username IS 'Encrypted username';
COMMENT ON COLUMN users.password IS 'Hashed password (BCrypt)';
COMMENT ON COLUMN users.email IS 'Encrypted email address';
COMMENT ON COLUMN users.phone_number IS 'Phone number (plain text)';
COMMENT ON COLUMN users.status IS 'User status: ONLINE, OFFLINE, AWAY, BUSY';

