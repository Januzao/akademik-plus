# Akademik Plus

A dormitory management system for managing residents, rooms, payments, and maintenance requests.

---

## Repository Structure

```
akademik-plus/
├── backend/         # Spring Boot REST API
├── frontend/        # Frontend application (in development)
└── docker/          # Docker configuration (PostgreSQL)
```

---

## Tech Stack

### Backend
| Technology | Version | Purpose |
|---|---|---|
| Java | 25 | Programming language |
| Spring Boot | 4.0.5 | Core framework |
| Spring Web MVC | — | REST API |
| Spring Data JPA | — | Database access via ORM |
| Hibernate | — | JPA provider |
| Spring Validation | — | Request validation |
| PostgreSQL | 17 | Relational database |
| Lombok | — | Boilerplate code generation |
| Maven | — | Build tool |

### Infrastructure
| Technology | Purpose |
|---|---|
| Docker / Docker Compose | PostgreSQL database containerization |

---

## Architecture

```
HTTP Request → Controller → Service → Repository → PostgreSQL
                   ↕                       ↕
                  DTO                    Entity
                   ↑                       ↑
                 Mapper ←——————————————————
```

| Layer | Responsibility |
|---|---|
| **Controller** | Receives HTTP requests, validates input DTOs, returns response DTOs |
| **Service** | Business logic, orchestrates operations |
| **Repository** | Spring Data JPA interfaces for CRUD database access |
| **Entity** | JPA classes mapped to PostgreSQL tables |
| **DTO** | Request/response objects that decouple the API from the internal domain model |
| **Mapper** | Converts between Entity and DTO |

---

## Domain Model

| Entity | Table | Description |
|---|---|---|
| `User` | `users` | Dormitory resident or staff member |
| `Room` | `rooms` | Room (number, type, floor, capacity) |
| `Payment` | `payments` | Resident payment (amount, date, status) |
| `MaintenanceRequest` | `maintenance_requests` | Repair/maintenance request |

### Relationships

```
User  ──(ManyToOne)──►  Room
Payment  ──(ManyToOne)──►  User
MaintenanceRequest  ──(ManyToOne)──►  User
MaintenanceRequest  ──(ManyToOne)──►  Room
```

### Entity Fields

**User**

| Field | Type | Notes |
|---|---|---|
| id | Integer | Primary key |
| firstName | String | Required |
| lastName | String | Required |
| email | String | Unique |
| passwordHash | String | Stored hash |
| phone | String | |
| role | String | e.g. resident, staff |
| profilePhoto | String | File path |
| room | Room (FK) | Assigned room |
| isActive | Boolean | Account status |

**Room**

| Field | Type | Notes |
|---|---|---|
| id | Integer | Primary key |
| roomNumber | String | Required |
| roomType | String | e.g. single, double |
| floorNumber | Integer | |
| occupancyStatus | String | `VACANT` / `FULL` — auto-managed |
| occupiedPlaces | Integer | Current occupancy count |
| totalPlaces | Integer | Maximum capacity |

**Payment**

| Field | Type | Notes |
|---|---|---|
| id | Integer | Primary key |
| user | User (FK) | Required |
| amount | BigDecimal | precision=10, scale=2 |
| paidFor | String | e.g. rent, utilities |
| paymentDate | LocalDate | |
| status | String | e.g. completed, pending, failed |

**MaintenanceRequest**

| Field | Type | Notes |
|---|---|---|
| id | Integer | Primary key |
| user | User (FK) | Required — who submitted |
| room | Room (FK) | Required — which room |
| category | String | e.g. plumbing, electrical |
| priority | String | high / medium / low |
| status | String | open / in_progress / completed |
| requestDate | LocalDate | |
| description | String | Issue details |

---

## API Endpoints

Base URL: `http://localhost:8080`

### Users `/api/users`

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/users` | List all users | 200 |
| GET | `/api/users/{id}` | Get user by ID | 200 / 404 |
| POST | `/api/users` | Create user | 201 |
| PUT | `/api/users/{id}` | Update user | 200 / 404 |
| DELETE | `/api/users/{id}` | Delete user | 204 / 404 |

**POST /api/users — Request body:**
```json
{
  "firstName": "Jan",
  "lastName": "Kowalski",
  "email": "jan.kowalski@example.com",
  "password": "secret123",
  "phone": "+48123456789",
  "role": "resident",
  "profilePhoto": "/photos/jan.jpg",
  "isActive": true,
  "roomId": 1
}
```

**Response:**
```json
{
  "id": 1,
  "firstName": "Jan",
  "lastName": "Kowalski",
  "email": "jan.kowalski@example.com",
  "phone": "+48123456789",
  "role": "resident",
  "isActive": true,
  "profilePhoto": "/photos/jan.jpg",
  "roomId": 1
}
```

---

### Rooms `/api/rooms`

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/rooms` | List all rooms | 200 |
| GET | `/api/rooms/{id}` | Get room by ID | 200 / 404 |
| POST | `/api/rooms` | Create room | 201 |
| PUT | `/api/rooms/{id}` | Update room | 200 / 404 |
| DELETE | `/api/rooms/{id}` | Delete room | 204 / 404 |

**POST /api/rooms — Request body:**
```json
{
  "roomNumber": "101",
  "roomType": "double",
  "totalPlaces": 2,
  "floorNumber": 1
}
```

**Response:**
```json
{
  "id": 1,
  "roomNumber": "101",
  "roomType": "double",
  "occupancyStatus": "VACANT",
  "occupiedPlaces": 0,
  "totalPlaces": 2,
  "floorNumber": 1
}
```

> `occupancyStatus` and `occupiedPlaces` are managed automatically by the service layer. Reducing `totalPlaces` below the current `occupiedPlaces` will return an error.

---

### Payments `/api/payments`

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/payments` | List all payments | 200 |
| GET | `/api/payments/{id}` | Get payment by ID | 200 / 404 |
| POST | `/api/payments` | Create payment | 201 |
| DELETE | `/api/payments/{id}` | Delete payment | 204 / 404 |

---

### Maintenance Requests `/api/maintenance-requests`

| Method | Path | Description | Status |
|---|---|---|---|
| GET | `/api/maintenance-requests` | List all requests | 200 |
| GET | `/api/maintenance-requests/{id}` | Get request by ID | 200 / 404 |
| POST | `/api/maintenance-requests` | Create request | 201 |
| DELETE | `/api/maintenance-requests/{id}` | Delete request | 204 / 404 |

---

## Setup & Running

### Prerequisites

- Java 25+
- Maven
- Docker & Docker Compose (for the database)

### 1. Clone the repository

```bash
git clone <repository-url>
cd akademik-plus
```

### 2. Configure environment variables

Copy the example file and fill in your values:

```bash
cp .env.example .env
```

`.env` contents:
```env
POSTGRES_DB=akademik-plus
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_password
DB_PORT=5432
```

### 3. Start the database

```bash
cd docker
docker compose up -d
```

This starts a PostgreSQL 17 container with a persistent volume (`postgres_data`).

### 4. Run the backend

```bash
cd backend
./mvnw spring-boot:run
```

Or build and run a JAR:

```bash
./mvnw clean package
java -jar target/akademik-plus-0.0.1-SNAPSHOT.jar
```

The API will be available at `http://localhost:8080`.

---

## Database

Hibernate is configured with `ddl-auto=update` — the schema is created and updated automatically on startup based on the JPA entities. SQL queries are logged to the console in formatted style.

To reset the database, stop the Docker container and remove the volume:

```bash
docker compose down -v
docker compose up -d
```

---

## Project Status

| Module | Status |
|---|---|
| User CRUD | Complete |
| Room CRUD | Complete |
| Payment CRUD | In progress |
| Maintenance Request CRUD | In progress |
| Frontend | In development |
| Authentication | Planned |
