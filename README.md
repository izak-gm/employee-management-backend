# Employee Management Backend

A Spring Boot REST API for managing employee accounts with role-based access control (Employee, Admin, Super Admin), JWT
authentication, and profile self-service.

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.7**
    - Spring Data JPA
    - Spring Security
    - Spring Validation
    - Spring Web MVC
- **PostgreSQL** — primary datastore
- **Flyway** — database migrations
- **JJWT 0.12.7** — JWT generation/validation
- **springdoc-openapi 3.0.2** — OpenAPI/Swagger documentation
- **Lombok**
- **Maven** — build tool

## Prerequisites

- Java 25 JDK
- Maven 3.9+ (or use the included `./mvnw` wrapper, if present)
- PostgreSQL 14+ running locally or accessible remotely
- A PostgreSQL database created ahead of time (Flyway will migrate the schema, but the database itself must exist)

## Environment Variables

This project reads configuration from environment variables — no secrets are committed to `application.yml`. Set the
following before running:

| Variable      | Description                                                                 | Example                            |
|---------------|-----------------------------------------------------------------------------|------------------------------------|
| `DB_HOST`     | PostgreSQL host                                                             | `localhost`                        |
| `DB_PORT`     | PostgreSQL port                                                             | `5432`                             |
| `DB_NAME`     | Database name                                                               | `employee_management`              |
| `DB_USER`     | Database username                                                           | `postgres`                         |
| `DB_PASSWORD` | Database password                                                           | `yourpassword`                     |
| `SECRET_KEY`  | HMAC signing key used to sign/verify JWTs (should be a long, random string) | `a-very-long-random-secret-string` |

### Setting env vars locally

**Linux/macOS (bash/zsh):**

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=employee_management
export DB_USER=postgres
export DB_PASSWORD=yourpassword
export SECRET_KEY=a-very-long-random-secret-string
```

**Or with a `.env` file + IDE run configuration**, or a `.env`-loading plugin of your choice. Just ensure these are
present in the process environment before Spring Boot starts — `application.yml` will fail to resolve placeholders (
`${DB_HOST}`, etc.) otherwise.

## Running the Application

1. **Create the database** (Flyway migrates the schema, but won't create the database itself):
   ```sql
   CREATE DATABASE employee_management;
   ```

2. **Set environment variables** (see above).

3. **Build and run:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   Or run the packaged jar:
   ```bash
   mvn clean package
   java -jar target/employee-management-backend-0.0.1-SNAPSHOT.jar
   ```

4. The server starts on **`http://localhost:8080`** by default (configurable via `server.port` in `application.yml`).

## Database Migrations

Migrations are managed with **Flyway**. On startup:

- `baseline-on-migrate: true` — allows Flyway to baseline an existing non-empty schema.
- `validate-on-migrate: true` — validates applied migrations against migration files before running.
- Migration scripts live under `src/main/resources/db/migration` (standard Flyway convention) named like `V1__init.sql`,
  `V2__add_role_column.sql`, etc.

`spring.jpa.hibernate.ddl-auto` is set to `update` — note that in a Flyway-managed project, schema changes should
ideally be driven through migration scripts rather than relying on Hibernate auto-update, to keep schema history
explicit and reproducible across environments.

## API Documentation

Once running, interactive API docs are available via springdoc-openapi:

- **Swagger UI:** `http://localhost:8080/swagger-ui.html` (or `/swagger-ui/index.html` depending on version)
- **Raw OpenAPI spec (JSON):** `http://localhost:8080/v3/api-docs`

The frontend generates TypeScript types directly from this spec using `openapi-typescript` — regenerate types on the
frontend whenever backend endpoints change.

## Authentication & Authorization

Authentication uses **JWT bearer tokens**. On successful login/registration, the API returns a signed token containing:

```json
{
  "sub": "user@example.com",
  "id": "employee-uuid",
  "email": "user@example.com",
  "role": "EMPLOYEE",
  "iat": 1234567890,
  "exp": 1234567890
}
```

Include the token on subsequent requests:

```
Authorization: Bearer <token>
```

Tokens are valid for **7 days** from issuance.

### Roles

| Role                                                  | Responsibilities                                                                                                                                           |
|-------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Super Admin**                                       | Full system access. Manages system configuration, creates admins, manages users, payroll settings, departments, roles, reports, and has all permissions.   |
| **Admin (HR Officer)**                                | Manages employees, approves leave, processes payroll, generates reports, updates employee records, but cannot manage Super Admins or system-wide settings. |
| **Manager / Supervisor** *(optional but recommended)* | Oversees a department or team. Approves leave for direct reports, views team information, but cannot manage payroll or system settings.                    |
| **Software_Engineer**                                 | Views profile, applies for leave, views leave balance, downloads payslips, updates limited personal information (if allowed).                              |
| **Senior_Engineer**                                   | Views profile, applies for leave, views leave balance, downloads payslips, updates limited personal information (if allowed).                              |
| **Payroll Officer** *(optional)*                      | Configures salaries, processes payroll, generates payslips, statutory reports, and bank transfer reports, but cannot manage employees or system settings.  |
| **Finance Officer** *(optional)*                      | Views payroll, approves payments, downloads payroll reports, but cannot edit employee information.                                                         |

## API Endpoints

### Auth

| Method | Endpoint                      | Access             | Description                        |
|--------|-------------------------------|--------------------|------------------------------------|
| `POST` | `/api/v1/auth/register`       | Public             | Self-register as an Employee       |
| `POST` | `/api/v1/auth/login`          | Public             | Log in, returns a JWT              |
| `POST` | `/api/v1/auth/admin/register` | Admin, Super Admin | Create a user with a specific role |

### Employees

| Method   | Endpoint                                        | Access             | Description                                                      |
|----------|-------------------------------------------------|--------------------|------------------------------------------------------------------|
| `GET`    | `/api/v1/employees/me`                          | Authenticated      | Get the current user's own profile                               |
| `PUT`    | `/api/v1/employees/update-profile/me`           | Authenticated      | Update the current user's own profile                            |
| `GET`    | `/api/v1/employees`                             | Admin, Super Admin | List/search employees (supports `filter`, `page`, `size`, `ids`) |
| `GET`    | `/api/v1/employees/{employeeId}`                | Admin, Super Admin | Get a specific employee's profile                                |
| `PUT`    | `/api/v1/employees/update-profile/{employeeId}` | Admin, Super Admin | Update a specific employee's profile                             |
| `DELETE` | `/api/v1/employees/{employeeId}`                | Admin, Super Admin | Delete an employee account                                       |

## Project Structure

```
src/main/java/com/riverbank/employee_management_backend/
├── EmployeeManagementBackendApplication.java   # Spring Boot entry point
├── config/
│   ├── ApplicationConfiguration.java   # PasswordEncoder, UserDetailsService, AuthenticationManager, CORS
│   ├── JwtAuthenticationFilter.java    # Validates JWT on each request, sets SecurityContext
│   ├── OpenApiConfig.java              # springdoc/Swagger configuration
│   └── SecurityConfig.java             # Security filter chain, route access rules
├── controller/
│   └── AuthController.java             # All REST endpoints (auth + employee management)
├── dto/
│   ├── AdminRegisterRequest.java
│   ├── AuthResponse.java
│   ├── EmployeeRequest.java            # Query params for listing/search (filter, page, size)
│   ├── EmployeeResponse.java           # Public-safe employee response
│   ├── ErrorResponse.java              # Standard error body shape
│   ├── MessageResponse.java
│   ├── RegisterLoginRequest.java
│   └── UpdateEmployee.java
├── entity/
│   └── Employee.java                   # JPA entity, implements UserDetails
├── enums/
│   └── Role.java                       # ADMIN, SUPERADMIN, EMPLOYEE
├── exception/
│   ├── EmployeeNotFoundException.java
│   ├── GlobalExceptionHandler.java     # @ControllerAdvice — maps exceptions to ErrorResponse
│   └── UserAlreadyExistsException.java
├── mapper/
│   └── AuthMapper.java                 # Entity <-> DTO mapping (manual, not MapStruct)
├── repository/
│   └── EmployeeRepository.java         # Spring Data JPA repository
├── service/
│   ├── AuthService.java                # Service interface
│   ├── AuthServiceImpl.java            # Business logic implementation
│   ├── JwtService.java                 # Token generation/validation
│   └── UserServiceImpl.java
└── util/
    └── StringUtils.java

src/test/java/com/riverbank/employee_management_backend/
├── EmployeeManagementBackendApplicationTests.java   # Context load test
└── service/
    └── AuthServiceImplTest.java                     # Unit tests for AuthService
```

## CORS

CORS is currently configured to allow requests from:

```
http://localhost:5173
```

(the local Vite frontend dev server). Update `ApplicationConfiguration.corsConfigurationSource()` to add production
frontend origins before deploying.

## Error Handling

Exceptions are centrally handled by a `GlobalExceptionHandler` (`@ControllerAdvice`), which maps known exceptions to a
consistent `ErrorResponse` body. Known exception types include:

- `UserAlreadyExistsException` — thrown on registration with a duplicate email
- `EmployeeNotFoundException` — thrown when looking up an employee that doesn't exist
- `UsernameNotFoundException` (Spring Security) — thrown during authentication if no matching user is found

Check `GlobalExceptionHandler.java` for the exact HTTP status codes and response shape returned for each.

## Testing

Run the test suite with:

```bash
mvn test
```

## Notes / Known Limitations

- `PUT` update-profile endpoints currently return the full `Employee` entity rather than a sanitized response DTO —
  avoid exposing this response directly in client UIs beyond the fields you intend to show (password hash and Spring
  Security internals are present in the raw entity).
- Ensure `SECRET_KEY` is sufficiently long and random in all environments — a weak key undermines JWT signature
  security.