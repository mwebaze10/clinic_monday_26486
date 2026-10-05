# Clinic API

Spring Boot REST API starter for clinic records.

## Included

- Java 17, Spring Web MVC, Spring Data JPA, PostgreSQL JDBC, and Lombok
- CRUD endpoints for doctors, offices, and patients
- Appointment creation using doctor and patient IDs

The application uses `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`,
and `SPRING_DATASOURCE_PASSWORD` for database connection settings.

## Build

```powershell
.\mvnw.cmd -DskipTests package
```

The application and context test require a PostgreSQL datasource. Set the
database connection environment variables before running them.

## Run

```powershell
.\mvnw.cmd spring-boot:run
```
