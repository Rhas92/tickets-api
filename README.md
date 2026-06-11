# Tickets API

A RESTful support-ticket management API built with Spring Boot and MongoDB.
Tickets have a title, description, status and priority, and can be created,
listed, filtered, updated and deleted.

## Technologies

- Java 21
- Spring Boot 4.0.6
- Spring Data MongoDB
- Bean Validation
- Docker & Docker Compose
- Swagger / OpenAPI (springdoc)
- Testcontainers (integration tests)
- GitHub Actions (CI)

## Features

- Full CRUD for tickets
- Filtering by status and priority
- Request/response DTOs (API contract decoupled from the database model)
- Bean validation on input with consistent JSON error responses
- Global exception handling (`@RestControllerAdvice`)
- Layered architecture: Controller → Service → Repository
- Three levels of tests: unit (Mockito), data integration (Testcontainers),
  and web layer (MockMvc)
- Continuous Integration: builds and tests run automatically on every push/PR

## Architecture

```
controller/   REST endpoints (HTTP)
service/      business logic
repository/    MongoDB data access
model/        Ticket entity + Status/Priority enums
dto/          TicketRequest (input) / TicketResponse (output)
exceptions/   custom exceptions + global handler
```

## Getting Started

### Prerequisites

- Java 21+
- Docker (Docker Desktop)

### Run with Docker Compose (recommended)

Starts the app and a MongoDB container, already connected, with persistent storage:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### Run locally (without Docker Compose)

Requires a MongoDB instance reachable at `mongodb://localhost:27017`:

```bash
docker run -d -p 27017:27017 --name mongodb mongo:8
./mvnw spring-boot:run
```

### Configuration

The MongoDB connection is read from the `MONGODB_URI` environment variable,
falling back to a local default (`application.yml`):

```yaml
spring:
  mongodb:
    uri: ${MONGODB_URI:mongodb://localhost:27017/ticketsdb}
```

In Docker Compose, `MONGODB_URI` points to the `mongodb` service.

## API Documentation

Interactive documentation (Swagger UI):

```
http://localhost:8080/swagger-ui.html
```

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/tickets` | Get all tickets |
| GET | `/tickets/{id}` | Get a ticket by id |
| GET | `/tickets/status/{status}` | Get tickets by status |
| GET | `/tickets/priority/{priority}` | Get tickets by priority |
| POST | `/tickets` | Create a ticket |
| PUT | `/tickets/{id}` | Update a ticket |
| DELETE | `/tickets/{id}` | Delete a ticket |

### Request body (POST / PUT)

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

## Running the Tests

```bash
./mvnw clean verify
```

Runs unit tests (mocked repository) and integration tests. The integration
tests spin up a real MongoDB container automatically via Testcontainers, so
Docker must be running.

## License

This project is for educational/portfolio purposes.