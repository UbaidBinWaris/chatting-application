-- Database Setup Script for Chatting Application
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO chating_username;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO chating_username;
-- Set default privileges for future tables

GRANT ALL PRIVILEGES ON ALL SEQUENCES IN SCHEMA public TO chating_username;
GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO chating_username;
GRANT ALL ON SCHEMA public TO chating_username;
-- Grant schema privileges (PostgreSQL 15+)

\c chatting_app
-- Connect to the database

GRANT ALL PRIVILEGES ON DATABASE chatting_app TO chating_username;
-- Grant privileges

CREATE USER chating_username WITH PASSWORD 'chatting_password';
-- Create user

CREATE DATABASE chatting_app;
-- Create database

-- Run this script in PostgreSQL to create the database and user

