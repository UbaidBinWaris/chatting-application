# Admin User Setup Guide

## Overview
This guide explains how to create and manage the admin user for the chatting application backend.

## Admin User Credentials

- **Email**: `admin@chatme.com`
- **Password**: `admin123`
- **Role**: `ADMIN`
- **Privilege Level**: `99`

## Quick Setup

### Method 1: Using the Batch File (Windows)
Simply run the batch file from the backend directory:

```batch
create-admin-user.bat
```

This will automatically:
1. Connect to the PostgreSQL database
2. Delete any existing admin user with the same email
3. Create a new admin user with the credentials above
4. Display confirmation

### Method 2: Using psql Directly
Run the SQL script manually:

```bash
# Set the password environment variable
$env:PGPASSWORD="chatting_password"

# Run the SQL script
psql -U chating_username -d chatting_app -f database\create_admin_user.sql
```

### Method 3: Manual SQL Execution
Connect to PostgreSQL and run:

```sql
-- Delete existing admin user if present
DELETE FROM users WHERE email = 'admin@chatme.com';

-- Insert admin user with BCrypt-hashed password
INSERT INTO users (email, password, role, privilege_level)
VALUES (
    'admin@chatme.com',
    '$2a$10$dXsORXHRUJDFsimawBkz4u6OiE5PBbSsofkmzmlCQmsHrnSthWLyi',
    'ADMIN',
    99
);
```

## Password Details

The password `admin123` is stored using BCrypt hashing with the following hash:
```
$2a$10$dXsORXHRUJDFsimawBkz4u6OiE5PBbSsofkmzmlCQmsHrnSthWLyi
```

This hash is generated using Spring Security's `BCryptPasswordEncoder` with:
- **Algorithm**: BCrypt
- **Strength**: 10 (default)
- **Salt**: Automatically generated and included in the hash
- **Status**: ✅ Tested and verified working

## Testing the Admin User

### Using the API

1. **Login Request**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@chatme.com",
    "password": "admin123"
  }'
```

2. **Expected Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@chatme.com",
  "role": "ADMIN",
  "privilegeLevel": 99
}
```

3. **Use the token** in subsequent requests:
```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Security Considerations

### Development Environment
- The current password (`admin123`) is suitable for development
- The credentials are documented in `SETUP_COMPLETE.md`
- Password is stored securely using BCrypt

### Production Environment
⚠️ **IMPORTANT**: Before deploying to production:

1. **Change the admin password**:
   - Use a strong, unique password
   - Update the BCrypt hash in the SQL script
   - Do not commit production passwords to version control

2. **Update the JWT secret**:
   - Generate a new, random secret key
   - Update `jwt.secret` in `application.properties`
   - Use environment variables for sensitive configuration

3. **Restrict admin access**:
   - Implement IP whitelisting for admin endpoints
   - Add multi-factor authentication
   - Monitor admin activity logs

## Troubleshooting

### Admin user already exists
If you get a unique constraint violation:
```sql
-- Delete the existing admin user first
DELETE FROM users WHERE email = 'admin@chatme.com';
```
Then run the creation script again.

### Password doesn't work
1. Verify the BCrypt hash in the database:
```sql
SELECT email, password FROM users WHERE email = 'admin@chatme.com';
```

2. The hash should be:
```
$2a$10$dXsORXHRUJDFsimawBkz4u6OiE5PBbSsofkmzmlCQmsHrnSthWLyi
```

3. If different, re-run the creation script

### Cannot connect to database
Ensure PostgreSQL is running and credentials are correct:
- **Database**: `chatting_app`
- **User**: `chating_username`
- **Password**: `chatting_password`
- **Host**: `localhost`
- **Port**: `5432`

## Generating New Password Hashes

If you need to generate a new BCrypt hash for a different password:

### Option 1: Using the Test Class
Run the test that generates password hashes:
```bash
./gradlew test --tests "com.example.backend.util.AdminPasswordHashTest"
```

### Option 2: Using the Utility Class
The `PasswordHashGenerator.java` utility can generate BCrypt hashes:
```java
public static void main(String[] args) {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    String hash = encoder.encode("your_new_password");
    System.out.println("BCrypt Hash: " + hash);
}
```

### Option 3: Using Online Tools
You can use online BCrypt generators with strength 10 (default), but ensure you:
- Use trusted, secure tools
- Clear your browser history
- Never use this for production passwords

## Files Reference

- **SQL Script**: `database/create_admin_user.sql`
- **Batch Script**: `create-admin-user.bat`
- **Test Utility**: `src/test/java/com/example/backend/util/AdminPasswordHashTest.java`
- **Password Utility**: `src/main/java/com/example/backend/util/PasswordHashGenerator.java`
- **Setup Documentation**: `SETUP_COMPLETE.md`

## Additional Notes

- The admin user has the highest privilege level (99)
- Admin users have access to all `/api/admin/**` endpoints
- Regular users have privilege level 1 by default
- The role field can be: `USER`, `ADMIN`, or custom values
- Privilege levels allow fine-grained permission control

---
**Created**: January 28, 2026
**Version**: 1.0
