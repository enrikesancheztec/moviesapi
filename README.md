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
- MySQL running locally (or reachable from your environment)
- Local environment properties configured in `env.properties`

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

## Configuration Notes
- The database connection depends on external values from `env.properties` (imported by `application.properties`).
- The `target/` folder contains build artifacts and must not be versioned.
