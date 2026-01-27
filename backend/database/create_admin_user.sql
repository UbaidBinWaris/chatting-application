-- Admin User Setup Script
-- This script creates an admin user with the following credentials:
-- Email: admin@chatme.com
-- Password: admin123
-- Role: ADMIN
-- Privilege Level: 99

-- Delete the admin user if it already exists
DELETE FROM users WHERE email = 'admin@chatme.com';

-- Insert the admin user with BCrypt-hashed password
-- Password: admin123
-- BCrypt hash: $2a$10$dXsORXHRUJDFsimawBkz4u6OiE5PBbSsofkmzmlCQmsHrnSthWLyi
-- This hash is generated using BCryptPasswordEncoder with strength 10 (default)
-- This hash has been tested and verified to work with the password "admin123"
INSERT INTO users (email, password, role, privilege_level)
VALUES (
    'admin@chatme.com',
    '$2a$10$dXsORXHRUJDFsimawBkz4u6OiE5PBbSsofkmzmlCQmsHrnSthWLyi',
    'ADMIN',
    99
);

-- Verify the admin user was created
SELECT id, email, role, privilege_level FROM users WHERE email = 'admin@chatme.com';
