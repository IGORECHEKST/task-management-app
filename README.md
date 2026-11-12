# Task Management App

A production-ready **Task Management REST API** built with Java 21 and Spring Boot 3.4. It enables teams to manage projects, tasks, labels, comments, cloud file attachments (via Dropbox API), and automated email notifications.

---

## Tech Stack

- **Language & Framework**: Java 21, Spring Boot 3.4.3
- **Security**: Spring Security 6, Stateless JWT Authentication (`jjwt`), Role-Based Access Control (`ROLE_USER`, `ROLE_ADMIN`)
- **Database & Migrations**: PostgreSQL 15, Spring Data JPA (Hibernate), Liquibase
- **Mapping & Boilerplate**: MapStruct, Lombok
- **Cloud Storage**: Dropbox Core SDK v7 (`com.dropbox.core:dropbox-core-sdk`)
- **Notifications & Scheduling**: Spring Boot Starter Mail, `@Async` event notifications, `@Scheduled` deadline reminders
- **Observability & Docs**: Spring Boot Actuator (`/actuator/health`), Springdoc OpenAPI / Swagger UI (`/swagger-ui.html`)
- **Quality & CI/CD**: Maven Checkstyle Plugin, JUnit 5, Mockito, JaCoCo (60%+ coverage check), Docker & Docker Compose, GitHub Actions CI

---

## Getting Started

### 1. Environment Configuration

Copy the sample environment file and adjust values as needed:

```bash
cp .env.sample .env
```

Environment variables in `.env`:
- `DB_URL`, `DB_USER`, `DB_PASSWORD` — PostgreSQL connection settings
- `JWT_SECRET`, `JWT_EXPIRATION` — JWT signing key and token TTL in ms
- `DROPBOX_ACCESS_TOKEN` — Dropbox API OAuth2 access token
- `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_FROM`, `MAIL_ENABLED` — SMTP configuration

### 2. Run with Docker Compose (Recommended)

Build and start both PostgreSQL and the Spring Boot application:

```bash
docker compose up --build
```

The API will be available at `http://localhost:8080`.

### 3. Run Locally with Maven

Start PostgreSQL (`docker compose up -d postgres`) and run:

```bash
mvn spring-boot:run
```

### 4. Run Tests, Checkstyle & Coverage Verification

```bash
mvn clean verify
```

---

## API Documentation & Endpoints

Interactive Swagger UI is available at:
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **Health Check**: `http://localhost:8080/actuator/health`

### Endpoint Summary

| Module | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/auth/register` | Register a new user account |
| **Auth** | `POST` | `/api/auth/login` | Authenticate user and receive JWT token |
| **Users** | `PUT` | `/users/{id}/role` | Update user roles (`ADMIN` only) |
| **Users** | `GET` | `/users/me` | Get current authenticated user's profile |
| **Users** | `PUT` / `PATCH` | `/users/me` | Update current authenticated user's profile |
| **Projects** | `POST` | `/api/projects` | Create a new project |
| **Projects** | `GET` | `/api/projects` | List projects (supports `status`, `search`, `page`, `size`) |
| **Projects** | `GET` | `/api/projects/{id}` | Retrieve project details by ID |
| **Projects** | `PUT` | `/api/projects/{id}` | Update project by ID |
| **Projects** | `DELETE` | `/api/projects/{id}` | Delete project by ID |
| **Tasks** | `POST` | `/api/tasks` | Create a new task and notify assignee |
| **Tasks** | `GET` | `/api/tasks` | List tasks (supports `projectId`, `status`, `priority`, `assigneeId`, `search`) |
| **Tasks** | `GET` | `/api/tasks/{id}` | Retrieve task details by ID |
| **Tasks** | `PUT` | `/api/tasks/{id}` | Update task by ID |
| **Tasks** | `DELETE` | `/api/tasks/{id}` | Delete task by ID |
| **Labels** | `POST` | `/api/labels` | Create a new label |
| **Labels** | `GET` | `/api/labels` | Retrieve paginated labels |
| **Labels** | `GET` | `/api/labels/{id}` | Retrieve label details by ID |
| **Labels** | `PUT` | `/api/labels/{id}` | Update label by ID |
| **Labels** | `DELETE` | `/api/labels/{id}` | Delete label by ID |
| **Comments** | `POST` | `/api/comments` | Add a comment to a task and notify assignee |
| **Comments** | `GET` | `/api/comments?taskId={taskId}` | Retrieve comments for a task |
| **Comments** | `DELETE` | `/api/comments/{id}` | Delete comment by ID |
| **Attachments** | `POST` | `/api/attachments?taskId={taskId}` | Upload multipart file to Dropbox and save metadata |
| **Attachments** | `GET` | `/api/attachments?taskId={taskId}` | Retrieve attachment metadata for a task |
| **Attachments** | `GET` | `/api/attachments/{id}/download` | Download attachment file from Dropbox |
| **Attachments** | `DELETE` | `/api/attachments/{id}` | Delete attachment from Dropbox and DB |
| **Actuator** | `GET` | `/actuator/health` | Application & Database health status |
