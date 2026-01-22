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
    Write-Host "   Username: $($user.username)" -ForegroundColor Gray
    Write-Host "   Email: $($user.email)" -ForegroundColor Gray
    Write-Host "   Display Name: $($user.displayName)" -ForegroundColor Gray
    Write-Host "   Status: $($user.status)" -ForegroundColor Gray
    $userId = $user.id
} catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 2: Get User
Write-Host "Test 2: Retrieve User by ID..." -ForegroundColor Yellow
try {
    $user = Invoke-RestMethod -Uri "http://localhost:8080/api/users/$userId" -Method GET
    Write-Host "✅ Retrieved user: $($user.username) ($($user.email))" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 3: Update Status to ONLINE
Write-Host "Test 3: Update User Status to ONLINE..." -ForegroundColor Yellow
try {
    $result = Invoke-RestMethod -Uri "http://localhost:8080/api/users/$userId/status?status=ONLINE" -Method PUT
    Write-Host "✅ $result" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 4: Verify Status Update
Write-Host "Test 4: Verify Status Update..." -ForegroundColor Yellow
try {
    $user = Invoke-RestMethod -Uri "http://localhost:8080/api/users/$userId" -Method GET
    Write-Host "✅ Current status: $($user.status)" -ForegroundColor Green
    Write-Host "   Last seen: $($user.lastSeen)" -ForegroundColor Gray
} catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""

# Test 5: Duplicate Username (should fail)
Write-Host "Test 5: Test Duplicate Username (should fail)..." -ForegroundColor Yellow
try {
    $body = @{
        username = 'testuser'
        email = 'different@example.com'
        password = 'TestPass123!'
        displayName = 'Different User'
    } | ConvertTo-Json

    Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json'
    Write-Host "❌ Should have failed but didn't!" -ForegroundColor Red
} catch {
    Write-Host "✅ Correctly rejected duplicate username" -ForegroundColor Green
}

Write-Host ""

# Test 6: Duplicate Email (should fail)
Write-Host "Test 6: Test Duplicate Email (should fail)..." -ForegroundColor Yellow
try {
    $body = @{
        username = 'differentuser'
        email = 'test@example.com'
        password = 'TestPass123!'
        displayName = 'Different User'
    } | ConvertTo-Json

    Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json'
    Write-Host "❌ Should have failed but didn't!" -ForegroundColor Red
} catch {
    Write-Host "✅ Correctly rejected duplicate email" -ForegroundColor Green
}

Write-Host ""

# Test 7: Short Password (should fail)
Write-Host "Test 7: Test Short Password (should fail)..." -ForegroundColor Yellow
try {
    $body = @{
        username = 'newuser'
        email = 'new@example.com'
        password = 'short'
        displayName = 'New User'
    } | ConvertTo-Json

    Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json'
    Write-Host "❌ Should have failed but didn't!" -ForegroundColor Red
} catch {
    Write-Host "✅ Correctly rejected short password" -ForegroundColor Green
}

Write-Host ""

# Test 8: Get Non-existent User (should fail)
Write-Host "Test 8: Get Non-existent User (should fail)..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "http://localhost:8080/api/users/999" -Method GET
    Write-Host "❌ Should have failed but didn't!" -ForegroundColor Red
} catch {
    Write-Host "✅ Correctly returned 404 Not Found" -ForegroundColor Green
}

Write-Host ""

# Test 9: Register Another User
Write-Host "Test 9: Register Second User..." -ForegroundColor Yellow
try {
    $body = @{
        username = 'jane_doe'
        email = 'jane@example.com'
        password = 'SecurePass456!'
        displayName = 'Jane Doe'
    } | ConvertTo-Json

    $user2 = Invoke-RestMethod -Uri 'http://localhost:8080/api/users/register' -Method POST -Body $body -ContentType 'application/json'
    Write-Host "✅ Second user created with ID: $($user2.id)" -ForegroundColor Green
} catch {
    Write-Host "❌ Failed: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "=================================" -ForegroundColor Cyan
Write-Host "🎉 Testing Complete!" -ForegroundColor Cyan
Write-Host ""
Write-Host "📊 Database Verification:" -ForegroundColor Cyan
Write-Host "   Run this SQL to see encrypted data:" -ForegroundColor Gray
Write-Host "   psql -U chatting_username -d chatting_app -c 'SELECT id, username_encrypted, email_encrypted, display_name, status FROM users;'" -ForegroundColor White

