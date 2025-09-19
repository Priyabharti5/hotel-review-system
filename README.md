# 🏨 Hotel Review System – Microservices Application

![Java](https://img.shields.io/badge/Java-21-blue)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)  
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.0.0-lightgrey)  
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

---

## Table of contents

- ###### [Project Overview & Tech Stack](#project-overview--tech-stack)
- ###### [Architecture](#architecture)
- ###### [Design Decisions](#design-decisions)
- ###### [Configuration Management](#configuration-management)
- ###### [Services & Key Endpoints](#services--key-endpoints)
- ###### [Running Locally](#running-locally)
- ###### [Postman Collection](#postman-collection)
- ###### [Observability & Monitoring](#observability--monitoring)
- ###### [Testing & CI/CD](#testing--cicd)
- ###### [Contributing & License](#contributing--license)
- ###### [Future Enhancements](#future-enhancements)

---

## Project Overview & Tech Stack

- The **Hotel Review System** is a production-grade microservices application built with **Spring Boot 3.5.5** and *
  *Spring Cloud 2025.0.0**.
- It demonstrates enterprise-level concepts like **microservices communication, service discovery, centralized configuration, distributed security, observability, and role-based access control (RBAC)**.

##### This project implements a hotel-review platform with 7 services:

1. **Auth Service (8084)** → Handles JWT auth, registration, login, and token validation.
2. **User Service (8081)** → Manages user profiles (normal users and hotel managers).
3. **Hotel Service (8082)** → Manages hotel data and average ratings.
4. **Review Service (8083)** → Manages user reviews for hotels.
5. **Eureka Server (8761)** → Service discovery.
6. **API Gateway (8080)** → Entry point, routes traffic, validates JWT.
7. **Config Server (8888)** → Centralized configuration (backed by GitHub repo).

**The system provides functionality for:**

- 🔐 **Authentication & Authorization** (JWT, Role-based)
- 👤 **User Management** (Normal, Hotel Owner, Admin)
- 🏨 **Hotel Management** (CRUD, search, filtering, rating sync)
- ⭐ **Review Management** (User → Hotel reviews with rating sync)
- ☁️ **Centralized Config, API Gateway, and Service Discovery**
- Services communicate internally via **Feign clients** and are registered in **Eureka**.
- Config is served from **Config Server**.
- **API Gateway** sits in front for ingress control and routing.
- Each service carries its own **Swagger/OpenAPI** documentation.

---

### ⚙️ Tech Stack:

- **Language**: Java 21
- **Framework**: Spring Boot 3.5.x, Spring Cloud 2025.0.0
- **Databases**: MySQL (per service)
- **Service Discovery**: Eureka
- **API Gateway**: Spring Cloud Gateway (WebFlux)
- **Config Management**: Spring Cloud Config Server (GitHub-backed)
- **Security**: Spring Security + JWT + Role-based Access Control
- **Inter-service Communication**: OpenFeign
- **Resilience**: Resilience4j (planned)
- **Monitoring**: Actuator + Prometheus/Grafana (planned)

---

## Architecture

```ASCII diagram
============================================================================================================
                     HOTEL REVIEW SYSTEM - ASCII ARCHITECTURE
============================================================================================================

                    GITHUB CONFIG REPO (git)
        (externalized YAMLs for each service / branches for envs)
                              |
                              v
                      +-------------------+
                      |  CONFIG SERVER    |
                      |     (8888)        |
                      +-------------------+
                              |
         fetch on startup     |  (services load their config by spring.application.name)
                              v
============================================================================================================
            
        ┌──────────────────────────────────────────────────┐
        │                 User Agent                       │    Clients (Browser / Mobile / curl)
        └──────────────────────┬───────────────────────────┘
                               │ requests (login / API calls)
                               v
        ┌──────────────────────────────────────────────────┐
        │                     API GATEWAY                  │  (Spring Cloud Gateway, 8080)
        │                 - global JWT validation          │  (fail-fast)
        │                - routing, rate-limit, CORS       │
        └──────────────────────┬───────────────────────────┘
                               |
                               | routes (serviceId -> discovered via Eureka)
                               v
        ┌──────────────────────────────────────────────────┐
        |                     EUREKA                       |  (Service registry, 8761)
        |                   (Discovery)                    |
        └─────────────────────────────────────────────────-┘
                               ^
                               │
                               │
      ┌──────────────────────────────────────────────────────────────────┐
      | registers             | registers          | registers           | registers
      |                       |                    |                     |
+----------------+   +----------------+   +----------------+    +----------------+
|  AUTH SERVICE  |   |  USER SERVICE  |   |  HOTEL SERVICE |    | REVIEW SERVICE |
|    (8084)      |   |    (8081)      |   |    (8082)      |    |    (8083)      |
| - issues JWT   |   | - user CRUD    |   | - hotels CRUD  |    | - reviews CRUD |
| - admin ops    |   | - internal APIs|   | - status, rate |    | - avg rating   |
+-------┬--------+   +-------┬--------+   +-------┬--------+    +-------┬--------+
        |                    |                    |                     |
        |                    |                    |                     | 
        v                    v                    v                     v
    +-------------+      +--------------+     +---------------+    +---------------+ 
    |  auth_db    |      |   user_db    |     |   hotel_db    |    |review_db      |
    +-------------+      +--------------+     +---------------+    +---------------+ 

============================================================================================================
Inter-service communication (Feign) & typical flows (internal endpoints prefixed with /_internal):
 -----------------------------------------------------------------------------------------------
1) Login flow:
   Client -> [API GATEWAY] -> Auth Service (/api/auth/login)
   Auth Service -> returns JWT -> Client stores token
   All subsequent requests: Client -> API GATEWAY (Authorization: Bearer <JWT>)
   API GATEWAY validates JWT (and forwards Authorization header to downstream services)

2) Service Discovery:
   Each service (auth/user/hotel/review/gateway) registers itself with EUREKA on startup.

3) Config:
   Each service reads config from CONFIG SERVER (git repo). bootstrap.yml contains only config-server URI
   Example bootstrap:
   spring:
   application:
   name: <service-name>
   cloud:
   config:
   uri: http://localhost:8888

4) Feign/internal calls (examples used across services):
    - HOTEL_SERVICE -> USER_SERVICE
      /api/users/_internal/validate/{userId}    (validate hotel owner)
    - REVIEW_SERVICE -> HOTEL_SERVICE
      /api/hotels/_internal/review/{hotelId}     (validate hotel exists / owner checks)
      /api/hotels/_internal/review/rating/{id}   (update avg rating)
    - REVIEW_SERVICE -> USER_SERVICE
      /api/users/_internal/{userId}              (fetch minimal user info)
    - HOTEL_SERVICE -> REVIEW_SERVICE
      /api/reviews/_internal/user/{userId}       (get reviews by user to compute hotels reviewed)
    - USER_SERVICE -> REVIEW_SERVICE
      /api/reviews/_internal/{reviewId}          (resolve userId from review id)

Notes:
* Feign clients forward Authorization header (RequestInterceptor) when needed.
* Internal endpoints use URL path prefix `/_internal` and are intended only for service-to-service calls.

============================================================================================================
```

## Design Decisions

##### Role-based Access Control (RBAC):

- `ROLE_ADMIN` → full control over all entities (users, hotels, reviews).
- `ROLE_USER` → can register, manage own profile, and create/manage reviews.
- `ROLE_HOTEL_MANAGER` → can register and manage hotels they own, and view reviews of their hotels.

##### Authorization:

- Enforced at controller layer using `@PreAuthorize` with SpEL.
- Fine-grained ownership checks delegated to helper security components:

    - `UserSecurity` → validates if a user is accessing their own data or admin.
    - `HotelSecurity` → validates if the logged-in manager owns the hotel.
    - `ReviewSecurity` → validates review ownership or hotel ownership (for managers).
- **Defense in depth**:
    - API Gateway performs **JWT validation** (fail-fast).
    - Resource servers perform additional **JWT parsing + ownership checks**.
    - Feign clients forward the `Authorization` header via a `RequestInterceptor`.

##### Soft Delete & Status Management:

- Instead of hard deletes, entities use **status enums** to control visibility and lifecycle:

    - `UserStatus { ACTIVE, DELETED }`
    - `HotelStatus { ACTIVE, INACTIVE, DELETED, BLOCKED }`
    - `ReviewStatus { ACTIVE, HIDDEN, DELETED }`
- **Status semantics:**

    - `ACTIVE` → available for normal use.
    - `DELETED` → logically removed but retained for audit/history.
    - `BLOCKED / INACTIVE / HIDDEN` → restricted from user access but kept for internal/admin use.

##### Data & Soft Delete Strategy:

- Status enums are persisted using `@Enumerated(EnumType.STRING)` → avoids issues if enum order changes.
- Database indexing applied for uniqueness, performance, and query filtering:

    - **User** → `user_id` (unique), `email` (unique), `mobile` (unique)
    - **Hotel** → `hotel_id` (unique), `owner_username`, `status`
    - **Review** → `review_id` (unique), `user_id`, `hotel_id`, `status`

##### Auditing:

- All entities extend a common `BaseEntity` class that provides **automatic auditing** fields:
    - `createdAt` → Timestamp when the entity was first persisted
    - `createdBy` → Username of the creator (from authentication)
    - `updatedAt` → Timestamp of the last modification
    - `updatedBy` → Username of the last modifier

- This is powered by **Spring Data JPA Auditing** with `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, and
  `@LastModifiedBy`.
- The values are populated automatically from the `SecurityContext`.
    - For authenticated requests → uses the logged-in user’s username
    - For background/system operations → falls back to `"SYSTEM"`

- This ensures a consistent audit trail across all entities without manual handling.

##### Internal APIs (Feign):

- Dedicated endpoints prefixed with `/_internal/**` for inter-service communication.
- Example: User-service validates hotel owner, Hotel-service resolves review → hotel, Review-service fetches review
  owner.
- These APIs are **not exposed publicly**; only accessible via other services.

---

## Configuration Management

- The system uses **Spring Cloud Config Server** for centralized configuration management.
- **Externalized configs** are stored in a dedicated GitHub repository:
  👉 [Hotel Review Config Repo](https://github.com/Priyabharti5/config-server)
- **Config Server** runs on port **8888** and serves configurations to all services.
- **Bootstrap setup**:
    - Each service includes a `bootstrap.yml` with only minimal properties.
- **Config resolution flow**:
    - At startup, each service fetches its config by name from Config Server.
    - Example: `spring.application.name=user-service` → loads `user-service.yml` from the GitHub repo.
    - Only service identity + config import reference is in local `bootstrap.yml`; all other details (DB, JPA, Eureka,
      logging, etc.) live in GitHub repo.
- **Repo structure**:
  ```plaintext
  config-repo/
    ├── auth-service.yml
    ├── user-service.yml
    ├── hotel-service.yml
    ├── review-service.yml
    ├── api-gateway.yml
    └── eureka-service.yml
  ```
- **Benefits**:
    - Centralized management for all services
    - Easy environment promotion (dev → staging → prod) by switching Git branches
    - Secrets and sensitive configs can be managed via encrypted values or vault integration

---

## Services & Key Endpoints

### `auth-service` (auth, tokens, admin operations)

1. `POST /api/auth/user/register` — register normal user (public) (Feign)
2. `POST /api/auth/hotel-manager-user/register` — register hotel owner (public) (Feign)
3. `POST /api/auth/admin/register` — register admin (ADMIN only)
4. `POST /api/auth/login` — login → returns JWT (public)
5. `PUT /api/auth/change-password` — change password (authenticated user)
6. `POST /api/auth/logout` — logout, invalidate token
7. `GET /api/auth/admin/users` — get all admins (active + deleted) (ADMIN only)
8. `GET /api/auth/admin/users/active` — get all active admins (ADMIN only)
9. `GET /api/auth/admin/users/deleted` — get all deleted admins (ADMIN only)
10. `PUT /api/auth/admin/user/status` — update user status (ADMIN only) (Feign)
11. `PUT /api/auth/admin/hotel/status` — update hotel status (ADMIN only) (Feign)
12. `PUT /api/auth/admin/review/status` — update review status (ADMIN only) (Feign)

**Note:** Auth issues JWT that other services validate (via gateway or each service).

---

### `user-service`

1. `GET /api/users/{userId}` — get user by userId (owner or ADMIN)
2. `GET /api/users` — get all users (ADMIN only)
3. `GET /api/users/email/{email}` — get user by email (owner or ADMIN)
4. `GET /api/users/mobile/{mobile}` — get user by mobile (owner or ADMIN)
5. `PUT /api/users/{userId}` — update user (owner or ADMIN)
6. `DELETE /api/users/{userId}` — delete user (owner or ADMIN (soft delete `DELETED`)) (Feign)
7. `GET /api/users/active` — get all active users (normal + hotel-owner) (ADMIN only)
8. `GET /api/users/hotel-owner/active` — get all active hotel-owners (ADMIN only)
9. `GET /api/users/normal-user/active` — get all active normal users (ADMIN only)
10. `GET /api/users/deleted` — get all deleted users (normal + hotel-owner) (ADMIN only)
11. `GET /api/users/hotel-owner/deleted` — get all deleted hotel-owners (ADMIN only)
12. `GET /api/users/normal-user/deleted` — get all deleted normal-users (ADMIN only)
13. `GET /api/users/review/{reviewId}` — get user by reviewId (ADMIN only) (Feign)

---

### `hotel-service`

1. `POST /api/hotels/register` — register hotel (ADMIN, HOTEL_MANAGER) (Feign)
2. `GET /api/hotels/{hotelId}` — get hotel by hotelId (ADMIN, HOTEL_MANAGER (owner))
3. `GET /api/hotels` — get all hotels (ADMIN, USER, HOTEL_MANAGER) (role-specific filtering)
4. `GET /api/hotels/search/name/{name}` — get hotels by name (ADMIN, USER, HOTEL_MANAGER) (role-specific filtering)
5. `GET /api/hotels/search/location/{location}` — get hotels by location (ADMIN, USER, HOTEL_MANAGER) (role-specific
   filtering)
6. `PUT /api/hotels/{hotelId}` — update hotel (ADMIN, HOTEL_MANAGER (owner))
7. `DELETE /api/hotels/{hotelId}` — delete hotel (ADMIN, HOTEL_MANAGER (owner))
8. `GET /api/hotels/owner/{userId}` — get hotels by ownerId (ADMIN, HOTEL_MANAGER (owner)) (Feign)
9. `GET /api/hotels/owner/{userId}/location/{location}` — get hotels by ownerId and location (ADMIN, HOTEL_MANAGER (
   owner)) (Feign)
10. `GET /api/hotels/owner/{userId}/search/name/{name}` — get hotels by ownerId and hotel-name (ADMIN, HOTEL_MANAGER (
    owner)) (Feign)
11. `GET /api/hotels/rating/{operator}/{value}` — get hotels by rating (ADMIN, USER, HOTEL_MANAGER) (role-specific
    filtering) (Feign)
12. `GET /api/hotels/location/{location}/rating/{minRating}` — get hotels by minimum-rating and location (ADMIN, USER,
    HOTEL_MANAGER) (role-specific filtering) (Feign)
13. `GET /api/hotels/deleted` — get all deleted hotels (ADMIN, HOTEL_MANAGER (owner))
14. `GET /api/hotels/reviewed/{userId}` — get hotels reviewed by user (ADMIN, USER (owner)) (Feign)
15. `GET /api/hotels/review/{reviewId}` — get hotel by reviewId (ADMIN only) (Feign)

---

### `review-service`

1. `POST /api/reviews` — create review (USER only) (Feign)
2. `GET /api/reviews/{reviewId}` — get review by reviewId (USER (owner), ADMIN)
3. `GET /api/reviews` — get all reviews (ADMIN, HOTEL_MANAGER, USER) (role-specific filtering)
4. `GET /api/reviews/user/{userId}` — get reviews by userId (ADMIN, USER (owner)) (Feign)
5. `GET /api/reviews/hotel/{hotelId}` — get reviews by hotelId (ADMIN, HOTEL_MANAGER, USER) (role-specific filtering) (
   Feign)
6. `PUT /api/reviews/{reviewId}` — update review-comment (USER (owner))
7. `DELETE /api/reviews/{reviewId}` — delete review (ADMIN only (soft-delete → `DELETED`))
8. `GET /api/reviews/hotel/{hotelId}/avg-rating` — get average hotel-rating (ADMIN, HOTEL_MANAGER, USER) (role-specific
   filtering) (Feign)

---

## Running locally

### Prerequisites

- **JDK 21+**
- **Maven 3.9+**
- **MySQL running locally with schemas:** `auth_db`, `user_db`, `hotel_db`, `review_db`

### MySQL Database Setup:

```sql
-- Create databases
CREATE DATABASE auth_db;
CREATE DATABASE user_db;
CREATE DATABASE hotel_db;
CREATE DATABASE review_db;

```

### Setup

##### 1. Clone Repo

```bash
   git clone https://github.com/Priyabharti5/hotel-review-system.git
   cd hotel-review-system
```

##### 2. Build Project

- From project root (parent POM present):
    ```bash
        mvn clean install -U
    ```

##### 3. Start Services in Order

- **Start** `eureka-server`
- **Start** `config-server`
- **Start** `api-gateway`
- **Start** `auth-service`
- **Start** `user-service`
- **Start** `hotel-service`
- **Start** `review-service`

##### 4. Access Services

- **Eureka Dashboard →** [http://localhost:8761](http://localhost:8761)
- **API Gateway →** [http://localhost:8080](http://localhost:8080)
- **Swagger UI (per service) →** `http://localhost:<port>/swagger-ui.html`

##### 5. Health & docs:

- Enable and check actuator: `/actuator/health`
- Swagger/OpenAPI UI: `/swagger-ui/index.html`

##### 6. API Gateway Routes

- `/api/auth/**` → Auth Service
- `/api/users/**` → User Service
- `/api/hotels/**` → Hotel Service
- `/api/reviews/**` → Review Service

---

## Postman Collection

A complete [Postman Collection](./hotel-review-system.postman_collection.json) is provided to test all microservices:

- **Auth Service** → registration, login, JWT validation
- **User Service** → user CRUD and internal APIs
- **Hotel Service** → hotel CRUD, search, filtering, rating sync
- **Review Service** → create/update/delete reviews, fetch by user/hotel, average rating
- **API Gateway** → routes all requests, JWT validation
- **Eureka & Config Server** → optional endpoints for health check / config verification

**Features:**

- Organized by **microservice** and **role** (ADMIN, USER, HOTEL_MANAGER).
- Pre-configured environment variables for **base URLs** (`localhost` with ports).
- Includes **sample requests** for login, JWT-protected APIs, and inter-service calls.

👉 Import the collection into Postman and start testing the APIs after running all services locally.

---

## Observability & Monitoring

- **Actuator endpoints enabled** (`/actuator/health`, `/actuator/info`).
- **Future scope:** integrate Prometheus & Grafana for metrics.

---

## Testing & CI/CD

- **Unit and integration tests planned** (JUnit + Testcontainers).
- **GitHub Actions** can be used for build + test pipeline.

---

## Contributing & license

- Follow project coding conventions (formatting, log levels).
- Add unit tests for new features and update API docs (OpenAPI annotations).
- Contributions: open PRs with tests and updated docs.
- License: MIT

---

## Future Enhancements

- Containerization with **Docker** for each service.
- **Kubernetes (K8s)** or **Docker Compose** for orchestration.
- **Centralized logging** with ELK (Elasticsearch, Logstash, Kibana).
- **Distributed tracing** with Zipkin/Jaeger.
- **Resilience4j** circuit breakers and retries.
- API documentation publishing with **SwaggerHub** or **Redoc**.
- CI/CD deployment pipelines to **AWS EKS** or **GCP**.

---

###### *Built with ❤️ using Spring Boot and Spring Cloud* 🍃

---

## Connect with Me

##### Priya Bharti

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Priyabharti5)  
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/priya-bharti-pb315/)  
[![Email](https://img.shields.io/badge/Email-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:priyabharti315@gmail.com)

