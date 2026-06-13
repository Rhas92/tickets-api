# 🎫 Tickets API

A RESTful **support-ticket management API** built with Java and Spring Boot, secured
with JWT, fully tested, dockerized and **deployed in production**.

🌍 **Live demo:** https://tickets-api-xiew.onrender.com/swagger-ui/index.html
*(Free tier — the first request may take ~30–60s while the app wakes up.)*
 
---

## ✨ Features

- 🎫 Full **CRUD** for support tickets
- 🔎 Filtering by **status** and **priority**
- 🔐 **JWT authentication** (stateless)
- 📦 **DTOs** decoupling the API from the database model
- ✅ Input **validation** with consistent JSON error responses
- 🧱 **Layered architecture** (Controller → Service → Repository)
- 🧪 **Three levels of tests**: unit, data integration, and web layer
- 🚀 **CI/CD**: tested automatically on every push, auto-deployed on merge
---

## 🛠️ Tech Stack

| Area | Technology |
|------|------------|
| Language | Java 21 |
| Framework | Spring Boot 4 |
| Database | MongoDB (Spring Data MongoDB) |
| Security | Spring Security + JWT (jjwt) |
| Docs | Swagger / OpenAPI (springdoc) |
| Testing | JUnit 5, Mockito, Testcontainers |
| Build | Maven |
| Containers | Docker + Docker Compose |
| CI | GitHub Actions |
| Hosting | Render + MongoDB Atlas |
 
---

## 🏗️ Architecture

```
controller/   → REST endpoints (HTTP layer)
service/      → business logic
repository/   → MongoDB data access
model/        → Ticket document + Status/Priority enums + AppUser
dto/          → request/response objects
config/       → security, JWT filter, OpenAPI
exceptions/   → custom exceptions + global error handler
```

> Following **SOLID** principles — single responsibility (DTOs vs entities) and
> dependency inversion (services depend on repository interfaces).
 
---

## 🔐 Authentication

This API is secured with JWT. Most endpoints require a valid token; only `/login`
and the Swagger docs are public.

**Demo user** (seeded automatically on startup):

| username | password |
|----------|----------|
| `admin`  | `admin123` |

**How to use it:**

1. `POST /login` with your credentials → receive a **JWT**.
```json
   { "username": "admin", "password": "admin123" }
```
2. Send the token on every protected request:
```
   Authorization: Bearer <your-token>
```
In Swagger, click the **Authorize 🔒** button and paste the token.
 
---

## 🚀 Getting Started

### Prerequisites

- Java 21+
- Docker (Docker Desktop)
### Run with Docker Compose (recommended)

Starts the app and a MongoDB container, already connected, with persistent storage:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Configuration

The app reads its config from environment variables, with safe local defaults:

| Variable | Description | Default |
|----------|-------------|---------|
| `MONGODB_URI` | MongoDB connection string | `mongodb://localhost:27017/ticketsdb` |
| `JWT_SECRET` | Secret key used to sign tokens | a development placeholder |

> 🔒 In production these are set as environment variables and never committed.
 
---

## 📚 API Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/login` | Authenticate and receive a JWT | 🔓 Public |
| `GET` | `/tickets` | Get all tickets | 🔐 |
| `GET` | `/tickets/{id}` | Get a ticket by id | 🔐 |
| `GET` | `/tickets/status/{status}` | Get tickets by status | 🔐 |
| `GET` | `/tickets/priority/{priority}` | Get tickets by priority | 🔐 |
| `POST` | `/tickets` | Create a ticket | 🔐 |
| `PUT` | `/tickets/{id}` | Update a ticket | 🔐 |
| `DELETE` | `/tickets/{id}` | Delete a ticket | 🔐 |

### Ticket body (POST / PUT)

```json
{
  "title": "Login broken",
  "description": "Users cannot log in on mobile",
  "status": "OPEN",
  "priority": "HIGH"
}
```

- `status`: `OPEN`, `IN_PROGRESS`, `CLOSED`
- `priority`: `LOW`, `MEDIUM`, `HIGH`
---

## 🧪 Running the Tests

```bash
./mvnw clean verify
```

Runs all test levels:

- 🧩 **Unit** — service logic with a mocked repository (Mockito)
- 🗄️ **Data integration** — against a real MongoDB started by **Testcontainers**
- 🌐 **Web layer** — real HTTP requests through the whole chain (MockMvc), with JWT
> Docker must be running (Testcontainers starts a throwaway MongoDB container).
 
---

## 📦 Project Status

✅ CRUD · ✅ MongoDB · ✅ DTOs · ✅ Validation & error handling · ✅ JWT security
✅ Docker Compose · ✅ Tests (3 levels) · ✅ CI · ✅ Deployed in production

## 📖 License

This project is for educational/portfolio purposes.