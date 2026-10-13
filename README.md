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
- Appointments in an inclusive date range, sorted by appointment date:
  `GET /api/appointments/between?start=2026-10-01&end=2026-10-31`
- Appointments in pages with total-page and total-element metadata:
  `GET /api/appointments/page?page=0&size=5&sort=appointmentDate,desc`
- Appointment counts grouped by status:
  `GET /api/appointments/stats/by-status`
- The office with the most appointments (office name, number, and count):
  `GET /api/offices/busiest`
- Cancel a doctor's appointments on a date, except completed appointments:
  `PATCH /api/appointments/cancel-day?doctorId=1&date=2026-10-20`
- Delete cancelled appointments before a date:
  `DELETE /api/appointments/cancelled-before?date=2026-10-15`
- Doctors by case-insensitive specialization name:
  `GET /api/doctors/by-specialization?name=cardiology`
- Doctors without an office, sorted by last name:
  `GET /api/doctors/without-office`
- Specializations that no doctor offers:
  `GET /api/specializations/unused`
- Patients with at least one appointment with a doctor:
  `GET /api/patients/of-doctor/1`
- Patients with at least a minimum number of appointments, most frequent first:
  `GET /api/patients/frequent?min=3`
- Create an appointment for a doctor and patient on a date (the doctor's date must be free):
  `POST /api/appointments/save?doctorId=1&patientId=1&appointmentDate=2026-10-20`
  Past dates are rejected with HTTP 400 and `Appointment date cannot be in the past`. An optional JSON body may include a `status`, but new appointments always start as `SCHEDULED`.
- Change appointment status with `PATCH /api/appointments/1/status?status=CONFIRMED`. Allowed transitions are `SCHEDULED` to `CONFIRMED` or `CANCELLED`, and `CONFIRMED` to `COMPLETED` or `CANCELLED`; other transitions return HTTP 400.

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
