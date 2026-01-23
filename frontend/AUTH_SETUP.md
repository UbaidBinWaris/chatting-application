# 🔐 Authentication System - Setup Complete!

## ✅ What's Implemented

### Frontend Components Created:

1. **Login Page** ([app/login/page.tsx](app/login/page.tsx))
   - Email/password authentication
   - JWT token management
   - Error handling
   - Link to registration page

2. **Register Page** ([app/register/page.tsx](app/register/page.tsx))
   - User registration form
   - Password confirmation
   - Validation (min 6 characters)
   - Auto-redirect to login after successful registration

3. **Dashboard** ([app/dashboard/page.tsx](app/dashboard/page.tsx))
   - Protected route (requires authentication)
   - Displays user email
   - Logout button
   - Session information

4. **Home Page** ([app/page.tsx](app/page.tsx))
   - Landing page with navigation
   - Auto-redirects to dashboard if already logged in
   - Links to login and register

### Authentication Utilities:

5. **Auth Library** ([lib/auth.ts](lib/auth.ts))
   - `setAuth()` - Store token and email
   - `getToken()` - Retrieve JWT token
   - `getUserEmail()` - Get logged-in user's email
   - `getAuthUser()` - Get complete auth data
   - `isAuthenticated()` - Check if user is logged in
   - `clearAuth()` - Logout (clear tokens)
   - `fetchWithAuth()` - Make authenticated API requests

6. **Auth Context** ([contexts/AuthContext.tsx](contexts/AuthContext.tsx))
   - Global authentication state management
   - `useAuth()` hook for easy access across components
   - Persistent login (survives page refresh)

7. **Protected Route Component** ([components/ProtectedRoute.tsx](components/ProtectedRoute.tsx))
   - Wrapper for protected pages
   - Auto-redirects to login if not authenticated

## 🚀 How to Use

### Start Backend Server:
```bash
cd backend
./run.bat
```
Backend runs on: `http://localhost:8080`

### Start Frontend:
```bash
cd frontend
npm run dev
```
Frontend runs on: `http://localhost:3000`

## 📋 User Flow

### 1. **First-Time User:**
   - Visit `http://localhost:3000`
   - Click "Sign Up"
   - Enter email and password (min 6 characters)
   - Confirm password
   - Auto-redirected to login page
   - Login with credentials
   - Redirected to dashboard

### 2. **Returning User:**
   - Visit `http://localhost:3000`
   - If already logged in → auto-redirect to dashboard
   - If not logged in → click "Login"
   - Enter credentials
   - Access dashboard

### 3. **Logout:**
   - Click "Logout" button in dashboard header
   - Tokens cleared from localStorage
   - Redirected to login page

## 🔒 Security Features

- ✅ **JWT Token Authentication** - Secure token-based auth
- ✅ **Password Hashing** - BCrypt encryption in backend
- ✅ **Protected Routes** - Unauthorized users redirected to login
- ✅ **CORS Configuration** - Backend allows frontend origin
- ✅ **Token Storage** - Tokens stored in localStorage
- ✅ **Session Persistence** - Login survives page refresh

## 📁 File Structure

```
frontend/
├── app/
│   ├── page.tsx                    # Home/Landing page
│   ├── layout.tsx                  # Root layout with AuthProvider
│   ├── login/
│   │   └── page.tsx               # Login page
│   ├── register/
│   │   └── page.tsx               # Registration page
│   └── dashboard/
│       └── page.tsx               # Protected dashboard
├── lib/
│   └── auth.ts                    # Auth utility functions
├── contexts/
│   └── AuthContext.tsx            # Global auth state
└── components/
    └── ProtectedRoute.tsx         # Route protection wrapper
```

## 🔗 API Endpoints (Backend)

### Register User
```http
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:** `"User Registered"`

### Login
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "email": "user@example.com"
}
```

## 🛠️ Tech Stack

**Frontend:**
- Next.js 15 (App Router)
- TypeScript
- Tailwind CSS
- React Hooks

**Backend:**
- Spring Boot 3
- Spring Security
- JWT (io.jsonwebtoken)
- PostgreSQL
- BCrypt password encoding

## 📝 Usage Examples

### Using Auth in Components:

```tsx
import { getAuthUser, isAuthenticated, clearAuth } from "@/lib/auth";

// Check if user is logged in
if (isAuthenticated()) {
  console.log("User is authenticated");
}

// Get current user
const user = getAuthUser();
console.log(user?.email);

// Logout
clearAuth();
```

### Making Authenticated API Calls:

```tsx
import { fetchWithAuth } from "@/lib/auth";

const response = await fetchWithAuth("http://localhost:8080/api/some-endpoint", {
  method: "GET"
});
```

## ⚠️ Important Notes

1. **Database Required:** Make sure PostgreSQL is running and database is set up:
   ```bash
   cd backend/database
   # Run setup.sql in PostgreSQL
   ```

2. **Environment Variables:** For production, move sensitive configs to `.env` files

3. **Token Expiration:** JWT tokens expire after 24 hours (configurable in backend)

4. **CORS:** Backend is configured to accept requests from `http://localhost:3000`

## 🎨 Customization

### Change Token Expiration:
Edit `backend/src/main/resources/application.properties`:
```properties
jwt.expiration=86400000  # 24 hours in milliseconds
```

### Add More User Fields:
1. Update `User` entity in backend
2. Update `LoginRequest`/`LoginResponse` DTOs
3. Update frontend forms and auth utilities

## 🐛 Troubleshooting

**Issue: "User not found" on login**
- Make sure you registered the user first
- Check backend logs for database connection

**Issue: Auto-redirect not working**
- Clear browser localStorage
- Check browser console for errors

**Issue: CORS errors**
- Verify backend is running on port 8080
- Check CORS configuration in `AuthController.java`

## ✨ Next Steps

You can now:
- Add real-time chat functionality
- Create chat rooms
- Add user profiles
- Implement WebSockets
- Add friend system
- Enhance UI/UX

## 📞 Support

Check the backend documentation:
- [backend/README.md](../backend/README.md)
- [backend/SETUP_COMPLETE.md](../backend/SETUP_COMPLETE.md)

---

**🎉 Your authentication system is fully functional and ready to use!**
