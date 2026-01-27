# Quick Start Guide - Chatting Application

Get your chatting application up and running in minutes!

## 📋 Prerequisites Checklist

- [ ] PostgreSQL installed and running
- [ ] Java 17+ installed
- [ ] Node.js 18+ installed
- [ ] Git (for cloning the repository)

## 🚀 5-Minute Setup

### Step 1: Database Setup (2 minutes)

```bash
# Connect to PostgreSQL
psql -U postgres

# Create database and user
CREATE DATABASE chatting_app;
CREATE USER chating_username WITH PASSWORD 'chatting_password';
GRANT ALL PRIVILEGES ON DATABASE chatting_app TO chating_username;
\c chatting_app
GRANT ALL ON SCHEMA public TO chating_username;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO chating_username;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO chating_username;
\q

# Run chat schema migration
psql -U chatting_username -d chatting_app -h localhost -f backend/database/chat_schema.sql
```

### Step 2: Start Backend (1 minute)

```bash
# Navigate to backend directory
cd backend

# Run the application (Windows)
.\gradlew bootRun

# Run the application (macOS/Linux)
./gradlew bootRun
```

Wait for: `Started BackendApplication in X.XXX seconds`

### Step 3: Start Frontend (2 minutes)

**Open a new terminal:**

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

Wait for: `Ready in X.XXXs`

### Step 4: Access the Application

Open your browser and navigate to:
```
http://localhost:3000
```

## 🎯 First-Time Usage

### 1. Register Users

1. Click **"Register"** on the home page
2. Create at least 2 test users:
   - **User 1:** alice@example.com / password123
   - **User 2:** bob@example.com / password123

### 2. Login

1. Click **"Login"**
2. Enter credentials for User 1 (Alice)
3. You'll be redirected to the **Dashboard**

### 3. Access Chat

From the Dashboard:
- Click the **"Go to Chat"** button
- You'll see the chat interface

### 4. Start a Conversation

**Option A: Direct Chat**
1. Click **"+ New Chat"**
2. Search for `bob@example.com`
3. Click on Bob to start chatting
4. Send a message!

**Option B: Group Chat**
1. Click **"+ New Group"**
2. Enter a group name (e.g., "Team Chat")
3. Search and add Bob (and other users if you created more)
4. Click **"Create Group"**
5. Start group messaging!

### 5. Test Real-Time Messaging

1. Open a second browser window (or incognito mode)
2. Login as User 2 (Bob)
3. Navigate to Dashboard → Chat
4. See the conversation from Alice
5. Send a message from Bob → It appears instantly in Alice's window!

## 🔄 Application Flow

```
Homepage (/)
    ↓
Register (/register) → Create Account
    ↓
Login (/login) → Enter Credentials
    ↓
Dashboard (/dashboard) → Welcome Screen
    ↓
Chat (/chat) → Messaging Interface
    ↓
    ├─ Direct Chats (1-on-1)
    ├─ Group Chats (Multiple users)
    └─ Real-time Messages
```

## ⚙️ Application Ports

| Service  | Port | URL |
|----------|------|-----|
| Frontend | 3000 | http://localhost:3000 |
| Backend  | 8080 | http://localhost:8080 |
| Database | 5432 | localhost:5432 |
| WebSocket| 8080 | ws://localhost:8080/ws |

## 🛠️ Useful Commands

### Backend Commands

```bash
# Start server
cd backend
.\gradlew bootRun          # Windows
./gradlew bootRun          # macOS/Linux

# Build without tests
.\gradlew build -x test    # Windows
./gradlew build -x test    # macOS/Linux

# Clean build
.\gradlew clean build      # Windows
./gradlew clean build      # macOS/Linux
```

### Frontend Commands

```bash
# Development mode
npm run dev

# Production build
npm run build

# Start production server
npm start

# Type checking
npm run type-check

# Lint code
npm run lint
```

### Database Commands

```bash
# Connect to database
psql -U chatting_username -d chatting_app -h localhost

# List tables
\dt

# View users
SELECT * FROM users;

# View conversations
SELECT * FROM conversations;

# View messages
SELECT * FROM messages ORDER BY created_at DESC LIMIT 10;

# View participants
SELECT * FROM conversation_participants;

# Exit
\q
```

## 🔍 Verify Setup

### Check Backend

```bash
# Test backend health
curl http://localhost:8080/api/health

# Or open in browser:
http://localhost:8080/api/health
```

### Check Frontend

```bash
# Open in browser:
http://localhost:3000
```

### Check Database

```sql
-- Connect and verify tables
psql -U chatting_username -d chatting_app -h localhost
\dt

-- Should show:
-- conversations
-- conversation_participants  
-- messages
-- message_read_receipts
-- users
```

### Check WebSocket

1. Login to the application
2. Open browser DevTools (F12)
3. Go to Network tab → WS filter
4. You should see a WebSocket connection to `ws://localhost:8080/ws`

## 🐛 Common Issues & Solutions

### Issue: Database Connection Failed

**Solution:**
```bash
# Check if PostgreSQL is running
# Windows:
services.msc  # Look for PostgreSQL service

# macOS/Linux:
sudo systemctl status postgresql

# Restart if needed
sudo systemctl restart postgresql
```

### Issue: Port 8080 Already in Use

**Solution:**
```bash
# Find and kill process using port 8080
# Windows:
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# macOS/Linux:
lsof -ti:8080 | xargs kill -9
```

### Issue: Port 3000 Already in Use

**Solution:**
```bash
# Use a different port
PORT=3001 npm run dev
```

### Issue: WebSocket Not Connecting

**Solution:**
1. Ensure backend is running on port 8080
2. Clear browser cache and localStorage
3. Check browser console for errors
4. Verify JWT token exists in localStorage

### Issue: Build Fails

**Solution:**
```bash
# Frontend
cd frontend
rm -rf .next node_modules package-lock.json
npm install
npm run build

# Backend
cd backend
.\gradlew clean build --refresh-dependencies
```

## 📱 Testing Checklist

- [ ] Register a new user
- [ ] Login successfully
- [ ] Navigate to Dashboard
- [ ] Access Chat page
- [ ] Create a direct conversation
- [ ] Send a message in direct chat
- [ ] Create a group conversation
- [ ] Send a message in group chat
- [ ] See real-time message delivery (use 2 browsers)
- [ ] Logout and login again
- [ ] Messages persist after reload

## 🎓 Next Steps

After basic setup:
1. Read [CHAT_SETUP.md](CHAT_SETUP.md) for detailed chat features
2. Review [README.md](README.md) for complete documentation
3. Check [API_DOCUMENTATION.md](backend/API_DOCUMENTATION.md) for API details
4. Explore admin features (add/remove participants, manage groups)

## 📞 Need Help?

1. **Check logs:**
   - Backend: Terminal where gradlew is running
   - Frontend: Browser console (F12)
   - Database: PostgreSQL logs

2. **Common files to check:**
   - `backend/src/main/resources/application.properties`
   - `frontend/.env.local` (create if needed)
   - `backend/database/chat_schema.sql`

3. **Verify versions:**
   ```bash
   java -version    # Should be 17+
   node -v          # Should be 18+
   psql --version   # Should be 12+
   ```

## ✅ Success Indicators

You'll know everything is working when:

1. ✅ Backend shows: `Started BackendApplication in X.XXX seconds`
2. ✅ Frontend shows: `Ready in X.XXXs` and opens browser automatically
3. ✅ You can register and login
4. ✅ Dashboard loads with your email
5. ✅ Chat page shows conversation list
6. ✅ Messages appear instantly when sent
7. ✅ Messages persist after page refresh

---

**Ready to chat! 🚀💬**
