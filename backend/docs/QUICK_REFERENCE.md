# 🎯 Quick Reference: User & Encryption

## Data Storage

| Field | Storage Type | Security Method |
|-------|-------------|-----------------|
| **Username** | `username_encrypted` (TEXT) | AES-256-GCM Encrypted |
| **Email** | `email_encrypted` (TEXT) | AES-256-GCM Encrypted |
| **Password** | `password_hash` (VARCHAR) | BCrypt Hashed |
| Display Name | `display_name` | Plain Text |
| Status | `status` | Plain Text (ONLINE/OFFLINE/AWAY/BUSY) |
| Other Fields | Plain Text | Not Sensitive |

## API Endpoints

```http
POST   /api/users/register    # Create new user
GET    /api/users/{id}        # Get user by ID
PUT    /api/users/{id}/status # Update status
```

## Example Request

```json
POST /api/users/register
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "displayName": "John Doe"
}
```

## Example Response

```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "displayName": "John Doe",
  "status": "OFFLINE",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-01-22T21:45:00",
  "lastSeen": null
}
```

## Important Notes

⚠️ **SAVE THE ENCRYPTION KEY** - Logged on first startup
⚠️ **Use Environment Variables** - Don't hardcode the key
⚠️ **HTTPS Required** - For production deployments
✅ **Data at Rest is Encrypted** - Even if DB is compromised
✅ **Passwords Cannot be Decrypted** - BCrypt is one-way

## Key Classes

- `EncryptionUtil` - Encrypts/decrypts username & email
- `PasswordHashUtil` - Hashes/verifies passwords
- `UserService` - Business logic with automatic encryption
- `UserController` - REST API endpoints

