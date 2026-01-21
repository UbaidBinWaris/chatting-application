# Chatting Application

A full-stack real-time chatting application built with **Next.js** (frontend) and **Spring Boot** (backend), connected to a **PostgreSQL** database.

## 📁 Project Structure

```
chatting-application/
├── frontend/          # Next.js frontend application
│   ├── app/          # Next.js app directory
│   ├── package.json
│   └── ...
├── backend/          # Spring Boot backend application
│   ├── src/
│   ├── pom.xml
│   └── ...
└── README.md
```

## 🛠️ Technologies Used

### Frontend
- **Next.js 15.1.3** - React framework (Latest)
- **React 19.0.0** - JavaScript library (Latest)
- **TypeScript 5.7.2** - Type-safe JavaScript (Latest)
- **Axios 1.7.9** - HTTP client (Latest)
- **Socket.io-client 4.8.1** - WebSocket client (Latest)

### Backend
- **Spring Boot 3.4.1** - Java framework (Latest)
- **Java 21** - LTS Version (Latest LTS)
- **Spring Data JPA** - Database access
- **PostgreSQL** - Database
- **WebSocket** - Real-time communication
- **Lombok** - Reduce boilerplate code

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

1. **Node.js** (v20 or higher) - [Download](https://nodejs.org/)
2. **Java JDK** (v21 - LTS) - [Download](https://adoptium.net/)
3. **PostgreSQL** (v16 or higher) - [Download](https://www.postgresql.org/download/)

> **Note:** Maven is NOT required! This project uses **Spring Boot** which can run directly with Java.

## 🗄️ Database Setup

### 1. Install PostgreSQL

Download and install PostgreSQL from the official website.

### 2. Create Database and User

Open PostgreSQL command line or pgAdmin and run:

```sql
-- Create database
CREATE DATABASE chatting_app;

-- Create user
CREATE USER chatting_username WITH PASSWORD 'chatting_password';

-- Grant privileges
GRANT ALL PRIVILEGES ON DATABASE chatting_app TO chatting_username;

-- Connect to the database
\c chatting_app

-- Grant schema privileges (PostgreSQL 15+)
GRANT ALL ON SCHEMA public TO chatting_username;
```

### 3. Verify Connection

Test the connection:
```bash
psql -U chatting_username -d chatting_app -h localhost
# Enter password: chatting_password
```

## 🚀 Installation & Setup

### Backend Setup

1. **Navigate to backend directory:**
   ```bash
   cd backend
   ```

2. **Verify database configuration:**
   
   The database configuration is in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/chatting_app
   spring.datasource.username=chatting_username
   spring.datasource.password=chatting_password
   ```

3. **Build and run the backend:**
   
   **Windows:**
   ```bash
   .\build.cmd clean install -DskipTests
   .\run.cmd
   ```
   
   **macOS/Linux:**
   ```bash
   chmod +x mvnw
   ./mvnw spring-boot:run
   ```

   The backend will start on `http://localhost:8080`

   > **Important for Windows:** The build scripts (`build.cmd` and `run.cmd`) automatically set JAVA_HOME for you. If you encounter issues, ensure Java 21 is installed at `C:\Program Files\Java\jdk-21`

   You should see:
   ```
   Started ChattingBackendApplication in X.XXX seconds
   ```

### Frontend Setup

1. **Open a new terminal and navigate to frontend directory:**
   ```bash
   cd frontend
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Run the development server:**
   ```bash
   npm run dev
   ```

   The frontend will start on `http://localhost:3000`


**Windows PowerShell:**
```bash
cd backend
.\mvnw.cmd spring-boot:run
```

**macOS/Linux:**
```bash
cd backend
./mvnwNavigate to [http://localhost:3000](http://localhost:3000)

## 🎯 Running the Application

### Start Backend (Terminal 1)
```bash
cd backend
mvn spring-boot:run
```

### Start Frontend (Terminal 2)
```bash
cd frontend
npm run dev
```

### Access the Application
- **Frontend:** http://localhost:3000
- **Backend API:** http://localhost:8080/api
- **Health Check:** http://localhost:8080/api/health

## 📡 API Endpoints

### Health Check
```
GET /api/health
```

### Messages
```
GET  /api/messages          # Get all messages

**Windows:**
```bash
cd backend
.\mvnw.cmd clean package
java -jar target/chatting-backend-1.0.0.jar
```

**macOS/Linux:**
```bash
cd backend
./mvnw

### Example Request (POST /api/messages)
```json
{
  "sender": "John Doe",
  "content": "Hello, World!"
}
```

## 🏗️ Build for Production

### Backend
```bash
cd backend
mvn clean package
java -jar target/chatting-backend-1.0.0.jar
```

### Frontend
```bash
cd frontend
npm run build
npm start
```

## 🔧 Troubleshooting

### Database Connection Issues

1. **Check if PostgreSQL is running:**
   ```bash
   # Windows
   pg_ctl status
   
   # Or check services
   services.msc
   ```

2. **Verify database exists:**
   ```bash
   psql -U postgres -l
   ```

3. **Check credentials:**
   - Username: `chatting_username`
   - Password: `chatting_password`
   - Database: `chatting_app`

### Backend Issues

1. **Port 8080 already in use:**
   
   Change port in `application.properties`:
   ```properties
   server.port=8081
   ```

2. **Check Java version:**
   ```bash
   java -version
   # Should be 21 (LTS) or higher
   ```

### Frontend Issues

1. **Port 3000 already in use:**
   
   Use a different port:
   ```bash
   PORT=3001 npm run dev
   ```

2. **Backend connection error:**
   
   Update `.env.local`:
   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080/api
   ```

## 📚 Project Features

- ✅ Real-time messaging
- ✅ PostgreSQL database integration
- ✅ RESTful API
- ✅ WebSocket support (configured)
- ✅ CORS enabled
- ✅ TypeScript support
- ✅ Responsive design

## 🔐 Environment Variables

### Frontend (.env.local)
```
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### Backend (Windows)
```bash
.\mvnw.cmd spring-boot:run    # Run application
.\mvnw.cmd clean install      # Build and install dependencies
.\mvnw.cmd clean package      # Package as JAR
.\mvnw.cmd test              # Run tests
```

### Backend (macOS/Linux)
```bash
./mvnw spring-boot:run    # Run application
./mvnw clean install      # Build and install dependencies
./mvnw clean package      # Package as JAR
./mvnw

## 📖 Additional Scripts

### Frontend
```bash
npm run dev      # Start development server
npm run build    # Build for production
npm start        # Start production server
npm run lint     # Run ESLint
```

### Backend
```bash
mvn spring-boot:run    # Run application
mvn clean install      # Build and install dependencies
mvn clean package      # Package as JAR
mvn test              # Run tests
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 License

This project is open source and available under the [MIT License](LICENSE).

## 👥 Authors

- Your Name - Initial work

## 🙏 Acknowledgments

- Next.js team for the amazing framework
- Spring Boot team for the robust backend framework
- PostgreSQL for the reliable database

---

**Happy Coding! 🚀**
