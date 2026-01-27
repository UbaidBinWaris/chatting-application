# Chat System Setup Guide

This guide will walk you through setting up and using the complete chat system with direct messaging, group chats, and admin features.

## 🎯 Features Overview

### Direct Messaging
- Create one-on-one conversations with any user
- Real-time message delivery via WebSocket
- Message history and persistence
- Read receipts tracking

### Group Chats
- Create group conversations with multiple participants
- Named groups with custom group names
- Multiple participants in a single conversation
- Group admin management

### Admin Features
- Add/remove participants from groups
- Promote users to group admins
- Only admins can manage group membership
- Group creator is automatically an admin

## 📋 Complete Setup Steps

### 1. Database Setup

First, ensure PostgreSQL is running and create the database:

```bash
# Connect to PostgreSQL as superuser
psql -U postgres

# Run setup commands
CREATE DATABASE chatting_app;
CREATE USER chating_username WITH PASSWORD 'chatting_password';
GRANT ALL PRIVILEGES ON DATABASE chatting_app TO chating_username;
\c chatting_app
GRANT ALL ON SCHEMA public TO chating_username;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON TABLES TO chating_username;
ALTER DEFAULT PRIVILEGES IN SCHEMA public GRANT ALL ON SEQUENCES TO chating_username;
\q
```

### 2. Run Chat Schema Migration

Apply the chat schema to create all necessary tables:

```bash
# From the project root directory
psql -U chatting_username -d chatting_app -h localhost -f backend/database/chat_schema.sql
```

**Expected output:**
```
CREATE TABLE
CREATE TABLE
CREATE TABLE
CREATE TABLE
CREATE INDEX
...
GRANT
```

### 3. Start Backend Server

```bash
# Navigate to backend directory
cd backend

# Windows
.\gradlew bootRun

# macOS/Linux
./gradlew bootRun
```

**Backend should start on:** `http://localhost:8080`

**Check logs for:**
```
Started BackendApplication in X.XXX seconds
```

### 4. Start Frontend Application

Open a new terminal:

```bash
# Navigate to frontend directory
cd frontend

# Install dependencies (first time only)
npm install

# Start development server
npm run dev
```

**Frontend should start on:** `http://localhost:3000`

## 🚀 Using the Chat System

### Step 1: Register Users

1. Navigate to `http://localhost:3000/register`
2. Create at least 2-3 test users:
   - User 1: `alice@example.com` / `password123`
   - User 2: `bob@example.com` / `password123`
   - User 3: `charlie@example.com` / `password123`

### Step 2: Login

1. Navigate to `http://localhost:3000/login`
2. Login with one of your test users

### Step 3: Start a Direct Conversation

1. After login, you'll be redirected to `/chat`
2. Click "New Chat" button
3. Search for a user by email (e.g., `bob@example.com`)
4. Click on the user to start a direct conversation
5. Type a message and press Enter or click Send

### Step 4: Create a Group Chat

1. Click "New Group" button
2. Enter a group name (e.g., "Team Discussion")
3. Search and select multiple participants
4. Click "Create Group"
5. Start messaging in the group

### Step 5: Manage Group (Admin Only)

As a group admin, you can:

1. **Add Participants:**
   - Click the group in the conversation list
   - Click "Add Participant" (if you're an admin)
   - Search and select a user to add

2. **Remove Participants:**
   - View the participant list
   - Click "Remove" next to a participant's name

3. **Make Someone Admin:**
   - Click "Make Admin" next to a participant's name
   - They will now have admin privileges

## 🔍 Technical Details

### Database Schema

**conversations**
- `id` - Unique conversation ID
- `name` - Group name (NULL for direct chats)
- `is_group` - Boolean flag
- `created_at`, `updated_at` - Timestamps

**conversation_participants**
- `id` - Unique participant record ID
- `conversation_id` - References conversations
- `user_id` - References users
- `is_admin` - Boolean flag for group admins
- `joined_at` - Timestamp

**messages**
- `id` - Unique message ID
- `conversation_id` - References conversations
- `sender_id` - References users
- `content` - Message text
- `message_type` - TEXT, IMAGE, FILE, etc.
- `is_read` - Boolean flag
- `created_at` - Timestamp

**message_read_receipts**
- `id` - Unique receipt ID
- `message_id` - References messages
- `user_id` - References users
- `read_at` - Timestamp

### WebSocket Flow

1. **Connection:**
   ```javascript
   // Frontend connects with JWT token
   websocketService.connect(token);
   ```

2. **Subscribe to Conversation:**
   ```javascript
   websocketService.subscribeToConversation(conversationId, (message) => {
     // Handle incoming message
   });
   ```

3. **Send Message:**
   ```javascript
   websocketService.sendMessage(conversationId, content, 'TEXT');
   ```

### API Flow

1. **Get Conversations:**
   ```
   GET /api/chat/conversations
   Authorization: Bearer {token}
   ```

2. **Create Direct Chat:**
   ```
   POST /api/chat/conversations/direct?otherUserId=2
   Authorization: Bearer {token}
   ```

3. **Create Group:**
   ```
   POST /api/chat/conversations/group
   Authorization: Bearer {token}
   Content-Type: application/json
   
   {
     "name": "Team Discussion",
     "participantIds": [2, 3, 4]
   }
   ```

4. **Send Message:**
   ```
   POST /api/chat/messages
   Authorization: Bearer {token}
   Content-Type: application/json
   
   {
     "conversationId": 1,
     "content": "Hello, everyone!",
     "messageType": "TEXT"
   }
   ```

## 🧪 Testing the System

### Test Scenario 1: Direct Messaging

1. Open two browser windows (or use incognito mode)
2. Login as Alice in window 1
3. Login as Bob in window 2
4. In Alice's window, create a direct chat with Bob
5. Send a message from Alice to Bob
6. See the message appear in real-time in Bob's window
7. Reply from Bob and see it appear in Alice's window

### Test Scenario 2: Group Chat

1. Login as Alice (group creator)
2. Create a group with Bob and Charlie
3. Send a message in the group
4. Login as Bob in another window
5. See Alice's message in the group
6. Send a reply from Bob
7. All participants should see all messages

### Test Scenario 3: Admin Management

1. As Alice (group creator/admin):
   - Add a new participant (e.g., David)
   - Make Bob an admin
2. Login as Bob (now admin):
   - Add another participant
   - Remove Charlie from the group
3. Login as Charlie:
   - Verify you no longer see the group
4. Login as David:
   - Verify you can see the group and messages

## 🔧 Troubleshooting

### WebSocket Connection Issues

**Problem:** Messages not appearing in real-time

**Solution:**
1. Check browser console for WebSocket errors
2. Verify backend is running on port 8080
3. Check CORS configuration in `WebSocketConfig.java`:
   ```java
   .setAllowedOrigins("http://localhost:3000")
   ```
4. Ensure JWT token is valid (check localStorage)

### Database Connection Issues

**Problem:** Backend fails to start with database errors

**Solution:**
1. Verify PostgreSQL is running:
   ```bash
   # Windows
   pg_ctl status
   
   # macOS/Linux
   systemctl status postgresql
   ```

2. Check database credentials in `application.properties`

3. Test connection manually:
   ```bash
   psql -U chatting_username -d chatting_app -h localhost
   ```

### Missing Messages

**Problem:** Old messages not showing

**Solution:**
1. Check if chat_schema.sql was run successfully
2. Verify all tables exist:
   ```sql
   \dt
   ```
3. Check for data:
   ```sql
   SELECT COUNT(*) FROM messages;
   SELECT COUNT(*) FROM conversations;
   ```

### Permission Errors

**Problem:** "Access denied" when managing groups

**Solution:**
1. Verify you're a group admin:
   ```sql
   SELECT * FROM conversation_participants 
   WHERE user_id = {your_user_id} AND conversation_id = {group_id};
   ```
2. Check `is_admin` column is TRUE

### Build Errors

**Problem:** Frontend build fails

**Solution:**
```bash
# Clear cache and rebuild
cd frontend
rm -rf .next node_modules package-lock.json
npm install
npm run build
```

**Problem:** Backend build fails

**Solution:**
```bash
# Clean and rebuild
cd backend
./gradlew clean build --refresh-dependencies
```

## 📊 Monitoring & Debugging

### Check WebSocket Connection Status

Open browser DevTools > Network > WS tab to see WebSocket frames

### View Real-time Database Changes

```sql
-- Watch for new messages
SELECT * FROM messages ORDER BY created_at DESC LIMIT 10;

-- Check active conversations
SELECT c.*, COUNT(m.id) as message_count
FROM conversations c
LEFT JOIN messages m ON m.conversation_id = c.id
GROUP BY c.id;

-- View group participants
SELECT cp.*, u.email
FROM conversation_participants cp
JOIN users u ON u.id = cp.user_id
WHERE cp.conversation_id = {group_id};
```

### Backend Logs

Check console output for:
- WebSocket connections: `WebSocket Connected`
- Message delivery: `Message sent to conversation {id}`
- Authentication: `JWT token validated for user {email}`

## 🎓 Next Steps

1. **Add File Upload:** Implement image and file sharing
2. **Message Reactions:** Add emoji reactions to messages
3. **Typing Indicators:** Show when users are typing
4. **Online Status:** Display user online/offline status
5. **Push Notifications:** Add browser push notifications
6. **Message Search:** Implement full-text search
7. **Voice/Video Calls:** Integrate WebRTC for calls

## 📚 Additional Resources

- [Spring WebSocket Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#websocket)
- [STOMP Protocol](https://stomp.github.io/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Next.js Documentation](https://nextjs.org/docs)

---

**Need Help?** Check the logs, verify database setup, and ensure all services are running on correct ports.
