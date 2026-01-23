# Chatting Application Backend

A Spring Boot REST API backend for a chatting application with JWT authentication.

## Prerequisites

- Java 25 or higher
- PostgreSQL database
- Gradle (included via wrapper)

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/example/backend/
│   │       ├── BackendApplication.java       # Main application class
│   │       ├── controller/
│   │       │   └── AuthController.java       # Authentication endpoints
│   │       ├── dto/
│   │       │   ├── LoginRequest.java         # Login request DTO
│   │       │   └── LoginResponse.java        # Login response DTO
│   │       ├── entity/
│   │       │   └── User.java                 # User entity
│   │       ├── repo/
│   │       │   └── UserRepository.java       # User repository
│   │       └── security/
│   │           ├── JwtUtil.java              # JWT utility class
│   │           └── SecurityConfig.java       # Security configuration
│   └── resources/
│       └── application.properties            # Application configuration
```

## Setup Instructions

### 1. Database Setup

Create a PostgreSQL database:

```sql
CREATE DATABASE chatting_app;
CREATE USER chating_username WITH PASSWORD 'chatting_password';
GRANT ALL PRIVILEGES ON DATABASE chatting_app TO chating_username;
```

### 2. Configure Application

The `application.properties` file is already configured with:
- Database connection settings
- JWT secret key (256-bit hex encoded)
- JWT token expiration (24 hours)

**Important:** Update the database credentials and JWT secret in production!

### 3. Build the Project

```bash
./gradlew clean build
```

### 4. Run the Application

```bash
./gradlew bootRun
```

Or run the JAR file:

```bash
java -jar build/libs/backend-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

#### Register a new user
```
POST /api/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Response: `"User Registered"`

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com"
}
```

## Technologies Used

- **Spring Boot 4.1.0-M1** - Application framework
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - Database access
- **PostgreSQL** - Database
- **JWT (jjwt 0.11.5)** - Token-based authentication
- **BCrypt** - Password encryption
- **Lombok** - Boilerplate code reduction

## Security Features

- Passwords are encrypted using BCrypt
- JWT tokens for stateless authentication
- CSRF protection disabled (for API use)
- CORS enabled for `http://localhost:3000` (frontend)
- Session management is stateless

## Helper Files

### Scripts
- **`run.bat`** - Start the application (Windows)
- **`build.bat`** - Build the project (Windows)

### Database
- **`database/setup.sql`** - Database setup script
- **`database/reset.sql`** - Database reset script (use with caution!)

### API Testing
- **`Chatting-App-API.postman_collection.json`** - Postman collection for testing API endpoints

### Configuration
- **`src/main/resources/application-local.properties.template`** - Template for local configuration

## Development

### Run Tests

```bash
./gradlew test
```

### Clean Build

```bash
./gradlew clean
```

### Import Postman Collection

Import the `Chatting-App-API.postman_collection.json` file into Postman to easily test the API endpoints.

## Configuration Properties

Key properties in `application.properties`:

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.url` | Database URL | `jdbc:postgresql://localhost:5432/chatting_app` |
| `spring.datasource.username` | Database username | `chating_username` |
| `spring.datasource.password` | Database password | `chatting_password` |
| `spring.jpa.hibernate.ddl-auto` | Hibernate DDL mode | `update` |
| `jwt.secret` | JWT signing key | (256-bit hex key) |
| `jwt.expiration` | Token expiration time | `86400000` (24 hours) |

## Next Steps

To complete the chatting application, you may want to add:

1. Message entity and endpoints
2. WebSocket support for real-time messaging
3. User profile management
4. Friend/contact management
5. Message history and persistence
6. File/image sharing
7. Group chat functionality
8. Online status tracking

## License

This project is licensed under the MIT License.

