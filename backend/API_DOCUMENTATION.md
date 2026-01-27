# API Documentation

Base URL: `http://localhost:8080/api`

## Authentication

### Register User
**Endpoint:** `POST /auth/register`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
- `200 OK`: "User Registered"
- `500 Internal Server Error`: "User already exists"

### Login
**Endpoint:** `POST /auth/login`

**Request Body:**
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

**Response:**
- `200 OK`:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "user@example.com",
  "role": "USER",
  "privilegeLevel": 1
}
```
- `500 Internal Server Error`: "User not found" or "Invalid password"

### Generate Password Hash (Localhost Only)
**Endpoint:** `GET /auth/hash`

**Query Parameters:**
- `password`: The password to hash

**Response:**
- `200 OK`: The hashed password string.

---

## Admin

**Headers Required:**
- `Authorization`: `Bearer <your_jwt_token>` (User must have `ADMIN` role)

### Get All Users
**Endpoint:** `GET /admin/users`

**Query Parameters:**
- `page` (optional, default: 0): Page number (0-indexed)
- `size` (optional, default: 10): Number of items per page

**Response:**
- `200 OK`:
```json
{
  "content": [
    {
      "id": 1,
      "email": "admin@example.com",
      "password": "$2a$10$...",
      "role": "ADMIN",
      "privilegeLevel": 1
    },
    {
      "id": 2,
      "email": "user@example.com",
      "password": "$2a$10$...",
      "role": "USER",
      "privilegeLevel": 1
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    ...
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "size": 10,
  "number": 0,
  ...
}
```
- `403 Forbidden`: If the user does not have the `ADMIN` role.
