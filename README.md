# Movies API

A Spring Boot REST API for managing movie information.

## Tech Stack
- Spring Boot 4.0.2
- Java 17
- Maven Wrapper (`./mvnw`)
- MySQL
- Spring Data JPA
- SpringDoc OpenAPI (Swagger UI)

## Project Origin
This project was created using Spring Boot Initializr:

- https://start.spring.io/

## Code Structure
Main project structure:

```text
src/main/java/com/kikesoft/moviesapi/
  controller/
  service/
  dao/
  repository/
  entity/
  mapper/
  config/
src/main/resources/
  application.properties
src/test/java/
```

## Prerequisites
Before running the project, make sure you have:

- Java 17+
- MySQL running locally (or reachable from your environment) for local app runtime
- Local environment properties configured in `env.properties` for local app runtime

For automated tests, MySQL is not required. Tests run with an in-memory H2 database using the `test` profile.

### env.properties required keys
Create an `env.properties` file in the project root with at least these keys:

```properties
DB_URL=jdbc:mysql://localhost:3306/movies?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
DB_USERNAME=root
DB_PASSWORD=changeme
JPA_DDL_AUTO=update
JPA_SHOW_SQL=true
APP_CORS_ALLOWED_ORIGINS=http://localhost:3000
APP_CORS_ALLOW_CREDENTIALS=true
logging.level.root=INFO
logging.level.com.kikesoft.moviesapi=DEBUG
```

## Logging Configuration (Development)
Recommended local logging configuration is:

- `logging.level.root=INFO`
- `logging.level.com.kikesoft.moviesapi=DEBUG`

Why this is recommended for development:

- `root=INFO` reduces framework noise from Spring/Hibernate.
- `com.kikesoft.moviesapi=DEBUG` keeps detailed logs for application code (controller/service/dao/mapper).
- This balance improves troubleshooting without flooding the console.

Where to configure it:

- Local runtime: `env.properties`
- Tests: `src/test/resources/application-test.properties` (independent from `env.properties`)

## Maven Commands (Daily Usage)
From the project root:

- Compile

```bash
./mvnw compile
```

- Run the application

```bash
./mvnw spring-boot:run
```

- Run tests

```bash
./mvnw test
```

Notes for tests:

- The test suite runs with profile `test`.
- Test datasource is H2 in-memory (`src/test/resources/application-test.properties`).
- `env.properties` is not required to run tests.

- Package artifact

```bash
./mvnw package
```

- Install to local Maven repository

```bash
./mvnw install
```

- Generate Javadoc

```bash
./mvnw javadoc:javadoc
```

## Javadoc
Generated Javadoc output:

```text
target/site/apidocs/index.html
```

Open it on macOS:

```bash
open target/site/apidocs/index.html
```

## How to Test the API with Swagger
1. Start the application.
2. Open Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

3. Open the OpenAPI JSON document:

```text
http://localhost:8080/v3/api-docs
```

4. Use Swagger UI to execute at least one endpoint.

## API Endpoints
Base URL:

```text
http://localhost:8080
```

Available endpoints:

| Method | Path | Description | Success Response |
| --- | --- | --- | --- |
| GET | `/movies` | Returns all movies. | `200 OK` with a JSON array of movies. |
| GET | `/movies/{id}` | Returns one movie by id. | `200 OK` with a movie JSON object, or `404 Not Found` when missing. |
| POST | `/movies` | Creates a new movie. | `201 Created` with the saved movie JSON object, or `400 Bad Request` for invalid payload. |
| PUT | `/movies/{id}` | Updates an existing movie by id. | `200 OK` with the updated movie JSON object. |

Movie JSON fields:

```text
id, name, launchDate, duration, rating, description
```

Example POST payload:

```json
{
  "name": "Inception",
  "launchDate": "2010-07-16",
  "duration": 148,
  "rating": "PG_13",
  "description": "A thief enters dreams to steal corporate secrets."
}
```

Example update request:

```text
PUT /movies/2
```

Example PUT payload:

```json
{
  "id": 2,
  "name": "Speed",
  "launchDate": "1994-06-10",
  "duration": 116,
  "rating": "R",
  "description": "A bomb is planted on a city bus that will explode if its speed drops below 50 mph."
}
```

Update endpoint error responses:

- `400 Bad Request` when payload validation fails.
- `400 Bad Request` when path id and payload id do not match.
- `404 Not Found` when the target movie id does not exist.
- `409 Conflict` when another movie already has the same `name` and `launchDate`.

Notes for update:

- The resource id is taken from the path (`/movies/{id}`).
- If `id` is present in the payload, it must match the path id.

## Configuration Notes
- Runtime database connection depends on external values from `env.properties` (imported by `application.properties`).
- Runtime log levels can be configured in `env.properties` (recommended: `root=INFO`, `com.kikesoft.moviesapi=DEBUG`).
- Test configuration is self-contained in `src/test/resources/application-test.properties` and uses H2 in-memory.
- The `target/` folder contains build artifacts and must not be versioned.

## Troubleshooting
- Application fails at startup with datasource errors:
  Verify `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` in `env.properties` and ensure MySQL is running.
- Tests fail with datasource errors:
  Verify tests are running with profile `test` and that `src/test/resources/application-test.properties` is present.
- Swagger UI not available:
  Confirm the app is started and open `http://localhost:8080/swagger-ui.html`.
- CORS issues from frontend clients:
  Set `APP_CORS_ALLOWED_ORIGINS` and `APP_CORS_ALLOW_CREDENTIALS` correctly in `env.properties`.
- Too much logging noise in local console:
  Use `logging.level.root=INFO` and keep app-level debug with `logging.level.com.kikesoft.moviesapi=DEBUG`.
