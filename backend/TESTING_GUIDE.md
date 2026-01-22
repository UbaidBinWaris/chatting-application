# 🧪 Testing Guide - User Management System

## ✅ Application is Running Successfully

Your Spring Boot application is currently running on **http://localhost:8080**

### Connection Status
- ✅ PostgreSQL Database: **Connected**
- ✅ Flyway Migrations: **Applied (V1 - create users table)**
- ✅ Encryption Key: **Generated and logged**
- ✅ Server: **Running on port 8080**

---

## 📋 Manual Testing Guide

### 1. Test User Registration

**Endpoint:** `POST /api/users/register`

**PowerShell Command:**
```powershell
$body = @{
    username = 'john_doe'
    email = 'john@example.com'
    password = 'SecurePass123!'
    displayName = 'John Doe'
} | ConvertTo-Json

Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json' | ConvertTo-Json
```

**Expected Response:**
```json
{
  "id": 1,
  "username": "john_doe",
  "email": "john@example.com",
  "displayName": "John Doe",
  "status": "OFFLINE",
  "isActive": true,
  "isVerified": false,
  "createdAt": "2026-01-22T22:15:00",
  "lastSeen": null
}
```

---

### 2. Test Get User by ID

**Endpoint:** `GET /api/users/{id}`

**PowerShell Command:**
```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/api/users/1' -Method GET | ConvertTo-Json
```

---

### 3. Test Update User Status

**Endpoint:** `PUT /api/users/{id}/status?status={status}`

**PowerShell Command:**
```powershell
Invoke-RestMethod -Uri 'http://localhost:8080/api/users/1/status?status=ONLINE' -Method PUT
```

**Expected Response:**
```
Status updated successfully
```

---

### 4. Verify Data Encryption in Database

**SQL Query:**
```sql
-- Connect to database
psql -U chatting_username -d chatting_app

-- View encrypted data
SELECT 
    id, 
    username_encrypted, 
    email_encrypted, 
    display_name, 
    status 
FROM users;
```

**Expected Output:**
- `username_encrypted` should be a long encrypted string (not "john_doe")
- `email_encrypted` should be a long encrypted string (not "john@example.com")
- `display_name` should be plaintext "John Doe"
- Password hash is NOT shown (security)

---

## 🔒 Security Features Verification

### Test Encryption

1. **Register a user**
2. **Check database directly** - username and email should be encrypted
3. **Retrieve user via API** - username and email should be decrypted

### Test Password Hashing

1. **Register with password:** `MyPassword123!`
2. **Check database:**
   ```sql
   SELECT id, password_hash FROM users WHERE id = 1;
   ```
3. **Verify:** Password hash should start with `$2a$12$` (BCrypt)
4. **Note:** You can never decrypt the password (one-way hash)

---

## 🧪 Test Scenarios

### ✅ Happy Path Tests

| Test Case | Command | Expected Result |
|-----------|---------|-----------------|
| Register new user | See command above | 201 Created, user object returned |
| Get existing user | `GET /api/users/1` | 200 OK, user data returned (decrypted) |
| Update status to ONLINE | `PUT /api/users/1/status?status=ONLINE` | 200 OK, "Status updated successfully" |
| Update status to AWAY | `PUT /api/users/1/status?status=AWAY` | 200 OK |
| Update status to BUSY | `PUT /api/users/1/status?status=BUSY` | 200 OK |

### ❌ Error Handling Tests

| Test Case | Command | Expected Result |
|-----------|---------|-----------------|
| Register with duplicate username | Register same username twice | 400 Bad Request, "Username already exists" |
| Register with duplicate email | Register same email twice | 400 Bad Request, "Email already exists" |
| Register with short password | password = "short" | 400 Bad Request, "Password must be at least 8 characters" |
| Get non-existent user | `GET /api/users/999` | 404 Not Found |
| Update status for non-existent user | `PUT /api/users/999/status?status=ONLINE` | 400 Bad Request, "User not found" |

---

## 📊 Database Verification

### Check Table Structure
```sql
\d users
```

**Expected Columns:**
- id (bigserial, primary key)
- username_encrypted (text, unique)
- email_encrypted (text, unique)
- password_hash (varchar 255)
- display_name (varchar 100)
- status (varchar 20)
- is_active (boolean)
- is_verified (boolean)
- created_at (timestamp)
- updated_at (timestamp)
- last_seen (timestamp)

### Count Users
```sql
SELECT COUNT(*) FROM users;
```

### View All Users (encrypted)
```sql
SELECT * FROM users;
```

---

## 🔐 Encryption Verification

### Test 1: Create Multiple Users
```powershell
# User 1
$body1 = @{ username='user1'; email='user1@test.com'; password='Pass1234!'; displayName='User One' } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body1 -ContentType 'application/json'

# User 2
$body2 = @{ username='user2'; email='user2@test.com'; password='Pass5678!'; displayName='User Two' } | ConvertTo-Json
Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body2 -ContentType 'application/json'
```

### Test 2: Verify Encryption
```sql
SELECT 
    id,
    LENGTH(username_encrypted) as username_length,
    LENGTH(email_encrypted) as email_length,
    LENGTH(password_hash) as password_length
FROM users;
```

**Expected:**
- username_encrypted length: > 50 characters (encrypted)
- email_encrypted length: > 50 characters (encrypted)
- password_hash length: 60 characters (BCrypt standard)

---

## 🎯 Performance Test

### Create 10 Users
```powershell
1..10 | ForEach-Object {
    $body = @{
        username = "user$_"
        email = "user$_@test.com"
        password = "Password$_!"
        displayName = "Test User $_"
    } | ConvertTo-Json
    
    try {
        $response = Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json'
        Write-Host "✅ Created user $_" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to create user $_" -ForegroundColor Red
    }
}
```

---

## 📝 Test Checklist

### Basic Functionality
- [ ] User registration works
- [ ] User retrieval works
- [ ] Status update works
- [ ] Data is persisted in database

### Security
- [ ] Username is encrypted in database
- [ ] Email is encrypted in database
- [ ] Password is hashed (BCrypt)
- [ ] API returns decrypted data
- [ ] Duplicate username rejected
- [ ] Duplicate email rejected

### Error Handling
- [ ] Invalid data rejected
- [ ] Non-existent user handled
- [ ] Appropriate HTTP status codes returned
- [ ] Error messages are clear

### Data Integrity
- [ ] Timestamps auto-populated
- [ ] Default values applied
- [ ] Unique constraints enforced
- [ ] All required fields validated

---

## 🚀 Quick Test Script

Save this as `test-users.ps1`:

```powershell
# Test User Management API

Write-Host "🧪 Testing User Management System" -ForegroundColor Cyan
Write-Host "=================================" -ForegroundColor Cyan
Write-Host ""

# Test 1: Register User
Write-Host "Test 1: Register New User..." -ForegroundColor Yellow
try {
    $body = @{
        username = 'testuser'
        email = 'test@example.com'
        password = 'TestPass123!'
        displayName = 'Test User'
    } | ConvertTo-Json
    
    $user = Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json'
    Write-Host "✅ User created with ID: $($user.id)" -ForegroundColor Green
    $userId = $user.id
} catch {
    Write-Host "❌ Failed: $_" -ForegroundColor Red
}

Write-Host ""

# Test 2: Get User
Write-Host "Test 2: Retrieve User..." -ForegroundColor Yellow
try {
    $user = Invoke-RestMethod -Uri "http://localhost:8080/api/users/$userId" -Method GET
    Write-Host "✅ Retrieved user: $($user.username)" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed: $_" -ForegroundColor Red
}

Write-Host ""

# Test 3: Update Status
Write-Host "Test 3: Update User Status..." -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "http://localhost:8080/api/users/$userId/status?status=ONLINE" -Method PUT
    Write-Host "✅ Status updated: $result" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed: $_" -ForegroundColor Red
}

Write-Host ""

# Test 4: Duplicate Username
Write-Host "Test 4: Test Duplicate Username (should fail)..." -ForegroundColor Yellow
try {
    $body = @{
        username = 'testuser'
        email = 'different@example.com'
        password = 'TestPass123!'
        displayName = 'Different User'
    } | ConvertTo-Json
    
    Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json'
    Write-Host "❌ Should have failed but didn't" -ForegroundColor Red
} catch {
    Write-Host "✅ Correctly rejected duplicate username" -ForegroundColor Green
}

Write-Host ""
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "🎉 Testing Complete!" -ForegroundColor Cyan
```

**Run with:**
```powershell
.\test-users.ps1
```

---

## 📖 Summary

Your User Management System includes:

1. ✅ **Secure User Registration** with encrypted fields
2. ✅ **User Retrieval** with automatic decryption
3. ✅ **Status Management** (ONLINE/OFFLINE/AWAY/BUSY)
4. ✅ **Data Encryption** (AES-256-GCM for username & email)
5. ✅ **Password Security** (BCrypt hashing)
6. ✅ **Error Handling** (validation, duplicates, not found)
7. ✅ **Database Persistence** with PostgreSQL
8. ✅ **RESTful API** with proper HTTP status codes

**All systems operational! 🚀**

