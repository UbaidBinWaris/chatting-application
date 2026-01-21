# Database Schema Documentation

## Users Table

The `users` table has been created with the following security features:

### Table Structure

| Column | Type | Description | Security |
|--------|------|-------------|----------|
| id | BIGSERIAL | Primary key | - |
| username | VARCHAR(255) | User's unique username | **ENCRYPTED** (AES) |
| password | VARCHAR(255) | User's password | **HASHED** (BCrypt) |
| email | VARCHAR(255) | User's unique email | **ENCRYPTED** (AES) |
| phone_number | VARCHAR(50) | User's phone number | Plain text |
| status | VARCHAR(50) | Online status | Plain text (ONLINE, OFFLINE, AWAY, BUSY) |
| created_at | TIMESTAMP | Account creation time | Auto-generated |
| updated_at | TIMESTAMP | Last update time | Auto-updated |

### Security Implementation

#### Password Hashing (BCrypt)
- Passwords are hashed using BCrypt algorithm
- One-way hashing - cannot be decrypted
- Automatically salted for additional security
- Verification done using `BCryptPasswordEncoder.matches()`

#### Username & Email Encryption (AES)
- Username and email are encrypted using AES algorithm
- Two-way encryption - can be decrypted when needed
- Encryption key stored in `application.properties`
- **IMPORTANT**: Change the encryption key in production!

### Migration File

The Flyway migration file `V1__Create_users_table.sql` creates:
- The users table with all columns
- Indexes on username, email, and status for performance
- Comments documenting which fields are encrypted/hashed

### Usage Example

```java
// Register a new user
User user = userService.registerUser(
    "john_doe",           // plain username (will be encrypted)
    "john@example.com",   // plain email (will be encrypted)
    "myPassword123",      // plain password (will be hashed)
    "+1234567890"         // phone number
);

// Find user by username
Optional<User> user = userService.findByUsername("john_doe");

// Verify password
boolean isValid = userService.verifyPassword(user, "myPassword123");

// Get decrypted values
String username = userService.getDecryptedUsername(user);
String email = userService.getDecryptedEmail(user);

// Update status
userService.updateUserStatus(userId, User.UserStatus.ONLINE);
```

### Important Notes

1. **Change Encryption Key**: The default encryption key in `application.properties` must be changed in production
2. **Key Length**: The AES key must be exactly 16 characters for AES-128
3. **Environment Variables**: Consider using environment variables for the encryption key instead of hardcoding
4. **Backup**: Store the encryption key securely - if lost, encrypted data cannot be recovered

### Next Steps

1. Update `encryption.secret.key` in `application.properties` with a secure key
2. Consider using environment variables for sensitive configuration
3. Run the application to execute the Flyway migration
4. The table will be automatically created in your PostgreSQL database

