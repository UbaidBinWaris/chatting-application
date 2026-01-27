# Authentication Debugging Guide

## Quick Steps to Test Chat Access

### 1. Clear Browser Storage (IMPORTANT!)

Before testing, clear your browser's localStorage:

**Option A: Using Browser Console (F12)**
```javascript
localStorage.clear();
sessionStorage.clear();
location.reload();
```

**Option B: Manual Clear**
1. Press F12 to open DevTools
2. Go to "Application" tab
3. Click "Local Storage" → `http://localhost:3000`
4. Right-click → Clear
5. Refresh page (F5)

### 2. Register a New User

1. Go to `http://localhost:3000/register`
2. Enter email: `happy@gmail.com`
3. Enter password: `password123`
4. Click "Register"
5. You should be redirected to `/dashboard`

### 3. Check Token is Saved

**Open Browser Console (F12) and type:**
```javascript
localStorage.getItem('token')
localStorage.getItem('userEmail')
```

**Expected output:**
```javascript
// token should show a long JWT string like:
"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXBweUBnbWFpbC5jb20i..."

// userEmail should show:
"happy@gmail.com"
```

If these return `null`, the login didn't save properly!

### 4. Test Dashboard Access

1. After login/register, you should see the Dashboard
2. Verify your email shows at top right
3. You should see the "Go to Chat" button

### 5. Test Chat Page Access

**Method 1: From Dashboard**
- Click the "Go to Chat" button

**Method 2: Direct URL**
- Navigate to `http://localhost:3000/chat`

**Expected Behavior:**
- ✅ Page loads with conversation list
- ✅ Shows "New Chat" and "New Group" buttons
- ✅ No redirect to login

**If it redirects to login:**
- Check browser console for error messages
- Verify token exists in localStorage (see step 3)

### 6. Check Browser Console for Errors

Open Console (F12) and look for:

**Good Messages:**
```
User authenticated: happy@gmail.com
Initializing chat...
User loaded: happy@gmail.com
Conversations loaded: 0
WebSocket connected successfully
```

**Bad Messages (Problems):**
```
No authentication found, redirecting to login
Error initializing chat: ...
401 Unauthorized
```

## Common Issues & Solutions

### Issue 1: Immediately Redirects to Login

**Cause:** Token not saved or expired

**Solution:**
```javascript
// Check in console:
localStorage.getItem('token')

// If null, re-login:
1. Go to /login
2. Enter: happy@gmail.com / password123
3. Check token is saved after login
```

### Issue 2: Token Exists But Still Redirects

**Cause:** Token might be invalid or backend not accepting it

**Solution:**
```javascript
// Test token validity:
fetch('http://localhost:8080/api/users/me', {
  headers: {
    'Authorization': 'Bearer ' + localStorage.getItem('token'),
    'Content-Type': 'application/json'
  }
})
.then(r => r.json())
.then(d => console.log('User:', d))
.catch(e => console.error('Error:', e))
```

**Expected:** Should return user object  
**If 401:** Token is invalid, re-login required

### Issue 3: Page Loads Then Redirects

**Cause:** API call failing (backend not running or CORS issue)

**Solution:**
1. Verify backend is running: `http://localhost:8080/api/users/me`
2. Check backend console for errors
3. Verify CORS settings in `SecurityConfig.java`

### Issue 4: Infinite Redirect Loop

**Cause:** localStorage not accessible or auth check logic issue

**Solution:**
```javascript
// Force set auth manually in console:
localStorage.setItem('token', 'your_token_here');
localStorage.setItem('userEmail', 'happy@gmail.com');
location.reload();
```

## Step-by-Step Test Procedure

### Complete Fresh Test

```bash
# 1. Clear everything
localStorage.clear();
sessionStorage.clear();

# 2. Go to register
window.location.href = '/register';

# 3. Register with:
Email: happy@gmail.com
Password: password123

# 4. After redirect to dashboard, check:
localStorage.getItem('token') // Should have value
localStorage.getItem('userEmail') // Should be "happy@gmail.com"

# 5. Click "Go to Chat"
# Should work!
```

## Backend Verification

### 1. Check Backend is Running

```bash
curl http://localhost:8080/api/users/me
# Should return 401 (expected without token)
```

### 2. Test Login Endpoint

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"happy@gmail.com","password":"password123"}'
```

**Expected Response:**
```json
{
  "token": "eyJhbGc...",
  "email": "happy@gmail.com"
}
```

### 3. Test Protected Endpoint with Token

```bash
# Replace YOUR_TOKEN with actual token from step 2
curl http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Expected Response:**
```json
{
  "id": 1,
  "email": "happy@gmail.com",
  "role": "USER"
}
```

## Network Tab Debugging

### Check API Calls

1. Open DevTools (F12) → Network tab
2. Navigate to `/chat`
3. Look for these requests:

**Request 1: Get Current User**
```
GET http://localhost:8080/api/users/me
Status: 200 OK
Headers:
  Authorization: Bearer eyJ...
Response:
  {"id":1,"email":"happy@gmail.com","role":"USER"}
```

**Request 2: Get Conversations**
```
GET http://localhost:8080/api/chat/conversations
Status: 200 OK
Response:
  [] or [conversation objects]
```

**Request 3: WebSocket Connection**
```
WS http://localhost:8080/ws
Status: 101 Switching Protocols
```

If any fail with **401** or **403**, there's an auth issue.

## Manual Fix for Stuck State

If you're completely stuck:

```javascript
// Run in browser console:

// 1. Clear everything
localStorage.clear();
sessionStorage.clear();

// 2. Go to login
window.location.href = '/login';

// 3. After successful login, verify:
console.log('Token:', localStorage.getItem('token'));
console.log('Email:', localStorage.getItem('userEmail'));

// 4. If token exists, manually navigate:
window.location.href = '/chat';
```

## Testing Checklist

- [ ] Backend running on port 8080
- [ ] Frontend running on port 3000
- [ ] Database tables created
- [ ] Can register a new user
- [ ] Token saved in localStorage after register
- [ ] Can login with credentials
- [ ] Token saved in localStorage after login
- [ ] Dashboard loads after login
- [ ] Email shows in dashboard header
- [ ] "Go to Chat" button visible
- [ ] Chat page loads (no redirect)
- [ ] Console shows "User authenticated" message
- [ ] Console shows "WebSocket connected" message
- [ ] Can create new conversation

## Success Indicators

When everything works:

**Browser Console:**
```
User authenticated: happy@gmail.com
Initializing chat...
User loaded: happy@gmail.com
Conversations loaded: 0
STOMP Debug: ...
WebSocket Connected
WebSocket connected successfully
```

**Network Tab:**
- ✅ GET /api/users/me → 200
- ✅ GET /api/chat/conversations → 200  
- ✅ WS /ws → 101 (WebSocket connected)

**UI:**
- ✅ Chat page displays
- ✅ Conversation list visible
- ✅ "New Chat" and "New Group" buttons work
- ✅ No error messages

---

**If you're still having issues after following this guide, share:**
1. Browser console output
2. Network tab screenshot
3. Backend console logs
4. What happens when you navigate to `/chat`
