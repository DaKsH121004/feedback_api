# Faculty Feedback Portal — Backend API

> **Live API:** [https://feedback-api-gcbr.onrender.com](https://feedback-api-gcbr.onrender.com)  
> **Frontend:** [https://final-feedback-ui.vercel.app](https://final-feedback-ui.vercel.app)

A RESTful Spring Boot backend for the **Faculty Feedback Portal** at Manav Rachna University (MRU). Handles admin authentication, institutional data management, feedback collection, and analytics.

---

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Framework | Spring Boot 3.4.3 |
| Language | Java 17 |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| DTO Mapping | ModelMapper 3.2.6 + Manual mapping |
| Excel Processing | Apache POI 5.2.5 |
| Boilerplate | Lombok |
| Validation | Jakarta Bean Validation |
| Build | Maven |
| Container | Docker (multi-stage) |

---

## Project Structure

```
src/main/java/com/feedback/feedback/
├── Application.java
├── controllers/          # 9 REST controllers
│   ├── AuthController.java
│   ├── SchoolController.java
│   ├── DepartmentController.java
│   ├── FacultyController.java
│   ├── CourseController.java
│   ├── AssignmentController.java
│   ├── FeedbackController.java
│   ├── DashboardController.java
│   └── AppConfigController.java
├── services/             # Service interfaces
│   └── impl/             # 10 implementations
│       ├── UserServiceImpl.java
│       ├── SchoolServiceImpl.java
│       ├── DepartmentServiceImpl.java
│       ├── FacultyServiceImpl.java
│       ├── CourseServiceImpl.java
│       ├── FacultyCourseAssignmentServiceImpl.java
│       ├── FeedbackServiceImpl.java
│       ├── DashboardServiceImpl.java
│       ├── AppConfigServiceImpl.java
│       └── FormScheduler.java
├── entities/             # 8 JPA entities
│   ├── School.java
│   ├── Department.java
│   ├── Course.java
│   ├── Faculty.java
│   ├── FacultyCourseAssignment.java
│   ├── Feedback.java
│   ├── AppConfig.java
│   └── User.java
├── repositories/         # 8 Spring Data JPA repositories
├── dto/                  # 21 request/response DTOs
├── security/
│   ├── SecurityFilter.java         # Spring Security config
│   ├── AuthFilter.java             # JWT filter (per-request)
│   ├── JwtUtils.java               # Token generation & validation
│   ├── CorsConfig.java             # CORS configuration
│   ├── AuthUser.java               # UserDetails wrapper
│   └── CustomUserDetailsService.java
└── exceptions/
    ├── GlobalException.java        # @ControllerAdvice handler
    ├── CustomAccessDenialHandler.java
    └── CustomAuthenticationEntryPoint.java
```

---

## API Endpoints

All endpoints are prefixed with `/api/v1`.

### Authentication
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/auth/login` | Public |
| POST | `/auth/register` | Public |

### Master Data (Schools / Departments / Faculty / Courses)
All four resources follow the same CRUD pattern:

| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/{resource}/all` | Public |
| POST | `/{resource}/add` | SUPERADMIN |
| PUT | `/{resource}/update/{id}` | SUPERADMIN |
| DELETE | `/{resource}/delete/{id}` | SUPERADMIN |

### Faculty-Course Assignments
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/assignment/all` | SUPERADMIN |
| GET | `/assignment/assigned-courses?facultyId=&departmentId=&semester=&section=` | Public |
| POST | `/assignment/add` | SUPERADMIN |
| POST | `/assignment/bulk-upload` | SUPERADMIN |
| PUT | `/assignment/update/{id}` | SUPERADMIN |
| DELETE | `/assignment/delete/{id}` | SUPERADMIN |

### Feedback
| Method | Endpoint | Access |
|--------|----------|--------|
| POST | `/feedback/add` | Public (form-gated) |
| GET | `/feedback/all` | SUPERADMIN |
| POST | `/feedback/upload` | SUPERADMIN |

### Dashboard
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/dashboard` | SUPERADMIN |

### Form Config
| Method | Endpoint | Access |
|--------|----------|--------|
| GET | `/form/status` | Public |
| POST | `/form/schedule` | SUPERADMIN |
| GET | `/form/validate/{token}` | Public |

---

## Database Schema

```
School
  └── Department  (FK: schoolId)
        └── Course  (FK: department_id)

Faculty  ←→  Department  (Many-to-Many: faculty_departments)

FacultyCourseAssignment
  ├── faculty_id, department_id, course_id
  ├── semester (nullable)
  └── classSection
  [UNIQUE on all 5 columns]

Feedback
  ├── studentName, studentRollNo, studentEmail
  ├── schoolId, departmentId, semester, classSection
  ├── facultyId, courseId
  ├── q1, q2, q3, q4, q5  (1–5 rating)
  ├── remarks
  └── createdAt

AppConfig  (singleton, id = 1)
  ├── feedbackEnabled (Boolean)
  ├── startTime / endTime (OffsetDateTime)
  └── formToken (UUID)
```

---

## Key Features

### Time-Gated Feedback Form
1. Admin POSTs to `/form/schedule` with an end time → backend generates a UUID token.
2. A shareable URL is returned: `https://final-feedback-ui.vercel.app/create-form/{token}`.
3. Students visit the URL → token is validated against the current time window.
4. On submission the system validates: school/dept hierarchy, faculty→dept membership, faculty→course assignment.

### Faculty Rating (Running Average)
```
newAvg = (currentAvg × totalResponses + feedbackAvg) / (totalResponses + 1)
feedbackAvg = (q1 + q2 + q3 + q4 + q5) / 5.0
```
Updated atomically on every feedback submission.

### Bulk Assignment Upload (Excel)
- Auto-detects column positions from the header row.
- Supports multiple faculty names per cell (split by `,`, `/`, `&`, or `and`).
- Three-level fuzzy name matching: exact → contains → first-name prefix.
- Strips titles (Prof., Dr., Mr.) and bracket annotations before matching.
- Returns a detailed report of successes, duplicates, and missing entities.

### Security
- **Stateless JWT** (HmacSHA256, 24-hour expiry).
- **BCrypt** password hashing.
- `@PreAuthorize("hasAuthority('SUPERADMIN')")` on all admin operations.
- Public endpoints: all `GET` master data, `POST /feedback/add`, form status/validate, CORS preflight.

---

## Getting Started

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL database

### Environment Variables

Set the following (e.g. in `application.properties` or as system env vars):

```properties
spring.datasource.url=jdbc:mysql://<host>/<db>
spring.datasource.username=<user>
spring.datasource.password=<password>
spring.jpa.hibernate.ddl-auto=update
secretJwtString=<your-32+-char-secret>
```

### Run Locally

```bash
./mvnw spring-boot:run
# API available at http://localhost:8080
```

### Build JAR

```bash
./mvnw clean package -DskipTests
java -jar target/*.jar
```

---

## Docker

### Build Image

```bash
docker build -t feedback-api .
```

### Run Container

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://... \
  -e SPRING_DATASOURCE_USERNAME=... \
  -e SPRING_DATASOURCE_PASSWORD=... \
  -e SECRETJWTSTRING=... \
  feedback-api
```

The Dockerfile uses a **multi-stage build** (Maven build → JRE-only runtime) with `-Xmx400m -Xms400m` JVM flags optimized for Render's free-tier 512MB containers.

---

## Deployment

Deployed on **Render** as a Docker web service. The container exposes port `8080` and all environment variables are configured via Render's dashboard.

---

## Related Repository

- **Frontend:** [github.com/DaKsH121004/final_feedback_ui](https://github.com/DaKsH121004/final_feedback_ui)
