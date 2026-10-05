# Clinic API

Spring Boot REST API starter for clinic records.

## Included

- Java 17, Spring Web MVC, Spring Data JPA, PostgreSQL JDBC, and Lombok
- CRUD endpoints for doctors, offices, and patients
- Appointment creation using doctor and patient IDs
- Patient last-name search, ignoring case and sorted by first name:
  `GET /api/patients/by-last-name?lastName=uwase`
- Appointments by status, sorted by appointment date:
  `GET /api/appointments/by-status?status=SCHEDULED`

## Build

```powershell
.\mvnw.cmd -DskipTests package
```

The application and context test require a PostgreSQL datasource. Set
`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, and
`SPRING_DATASOURCE_PASSWORD` before running them.

## Run

```powershell
.\mvnw.cmd spring-boot:run
```
