-- Database Migration Script
-- This script fixes the users table schema issues

-- Step 1: Drop the existing users table if it has incompatible data
DROP TABLE IF EXISTS users CASCADE;

-- Step 2: Recreate the users table with correct schema
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    privilege_level INTEGER NOT NULL DEFAULT 1
);

-- Step 3: Grant permissions
GRANT ALL PRIVILEGES ON TABLE users TO chating_username;
GRANT USAGE, SELECT ON SEQUENCE users_id_seq TO chating_username;

