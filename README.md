# CloudStore

Short description of what this application does.





---

## Table of Contents

- [Requirements](#requirements)
- [Environments](#environments)
- [Running Locally](#running-locally)
- [Running Tests](#running-tests)
- [CI/CD](#cicd)
- [Releasing to Production](#releasing-to-production)

---

## Requirements

- Java 21+
- Gradle (wrapper included)
- PostgreSQL (staging & production only)

---

## Environments

| Profile  | Database    | Purpose                        |
|----------|-------------|--------------------------------|
| `dev`    | H2 in-memory | Local development, no setup required |
| `stage`  | PostgreSQL  | Pre-production validation      |
| `prod`   | PostgreSQL  | Live production environment    |

---

## Running Locally

The default profile is `dev`. H2 starts automatically — no database installation needed.

```bash
./gradlew bootRun
```

To run against a specific profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=stage'
```

### Environment Variables (stage & prod)

| Variable              | Description              |
|-----------------------|--------------------------|
| `DB_URL`              | JDBC connection URL      |
| `DB_USERNAME`         | Database username        |
| `DB_PASSWORD`         | Database password        |

Example:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/mydb
export DB_USERNAME=myuser
export DB_PASSWORD=secret
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### H2 Console (dev only)

The H2 web console is available at `http://localhost:8080/h2-console` when running in `dev`.

| Field    | Value                          |
|----------|-------------------------------|
| JDBC URL | `jdbc:h2:mem:testdb`          |
| Username | `sa`                          |
| Password | *(leave blank)*               |

---

## Running Tests

Tests run against H2 and require no external database.

```bash
./gradlew check
```

To force re-execution when nothing has changed:

```bash
./gradlew check --rerun-tasks
```

---

## CI/CD

Tests are run automatically on every push and pull request via GitHub Actions.

**Workflow:** `.github/workflows/ci.yml`

| Trigger        | Action              |
|----------------|---------------------|
| `push`         | Runs `./gradlew check` |
| `pull_request` | Runs `./gradlew check` |

Status badge — add to the top of this README once the workflow has run once:

```markdown
![CI](https://github.com/<org>/<repo>/actions/workflows/ci.yml/badge.svg)
```

---

## Releasing to Production

Deployments to production are triggered by publishing a **GitHub Release**.

### Steps

1. Ensure all changes are merged to `main` and CI is green.

2. Go to **GitHub → Releases → Draft a new release**.

3. Fill in the release details:

   | Field           | Description                                              |
      |-----------------|----------------------------------------------------------|
   | **Tag**         | Semantic version, e.g. `v1.2.0`                         |
   | **Title**       | Short summary, e.g. `v1.2.0 — Add user registration`    |
   | **Description** | Changelog for this release (features, fixes, notes)     |

4. Click **Publish release**.

This triggers the `release` workflow (`.github/workflows/release.yml`), which builds and deploys to production.

> **Note:** The release description is required. Releases published without a description will be rejected by the workflow.

### Versioning

This project follows [Semantic Versioning](https://semver.org/):

```
v<MAJOR>.<MINOR>.<PATCH>

MAJOR — breaking changes
MINOR — new features, backwards compatible
PATCH — bug fixes
```
## model
```mermaid
classDiagram

direction TB

%% Domain Models

class AppUser {

+String username

+String email

+String password

+getUsername() String

+getEmail() String

+getPassword() String

}

class Product {

+Long id

+String title

+Double price

+String description

+String category

+String image

}

class User {

-Long id

-String name

-String email

+User(name, email)

+getId() Long

+getName() String

+getEmail() String

+equals(Object) boolean

+hashCode() int

}

%% DTOs

class LoginDto {

+String username

+String password

}

class RegisterDto {

+String username

+String email

+String password

}

%% Repositories

class UserRepository {

<<interface>>

+findByName(String) Optional~User~

+findByEmail(String) Optional~User~

+findAll() List~User~

}

class FakeUserRepository {

-ConcurrentHashMap store

-ConcurrentHashMap emailIndex

-ConcurrentHashMap nameIndex

-AtomicLong idSequence

+save(User) User

+findById(Long) Optional~User~

+findByName(String) Optional~User~

+findByEmail(String) Optional~User~

+delete(User) void

+count() long

}

%% Services

class UserService {

-Map~String,AppUser~ users

-UserRepository userRepository

-PasswordEncoder passwordEncoder

-JwtUtil jwtUtil

+register(String, String, String) void

+login(String, String) String

+findByUsername(String) AppUser

+findAll() List~AppUser~

}

class ProductService {

-Map~Long,Product~ products

-RestTemplate restTemplate

+loadProductsOnStartup() void

+refreshProducts() void

+getAllProducts() Collection~Product~

+getProductById(Long) Product

}

%% Controllers

class AuthController {

-UserService userService

+registerPage(Model) String

+register(RegisterDto, Model) String

+loginPage() String

+login(String, String, HttpServletResponse, Model) String

}

class AuthRestController {

-UserService userService

+login(LoginDto) ResponseEntity

+register(RegisterDto) ResponseEntity

}

class ProductController {

-ProductService productService

+products(Model) String

}

class OrderController {

+orders(Model) String

+createOrder() String

+orderConfirmation() String

}

class HomeController {

+index(Model) String

}

class RedirectController {

+redirectToSwagger() String

}

%% Security

class JwtUtil {

-String secret

-long EXPIRATION

+generateToken(String) String

+extractUsername(String) String

+validateToken(String) boolean

-getKey() SecretKey

}

class JwtFilter {

-JwtUtil jwtUtil

#doFilterInternal(request, response, chain) void

}

class SecurityConfig {

-JwtFilter jwtFilter

+passwordEncoder() PasswordEncoder

+securityFilterChain(HttpSecurity) SecurityFilterChain

}

class OncePerRequestFilter {

<<abstract>>

}

class CrudRepository {

<<interface>>

}

%% Relationships

FakeUserRepository ..|> UserRepository : implements

UserRepository --|> CrudRepository : extends

JwtFilter --|> OncePerRequestFilter : extends

UserService --> UserRepository : uses

UserService --> JwtUtil : uses

UserService --> AppUser : manages

ProductService --> Product : manages

AuthController --> UserService : uses

AuthController --> RegisterDto : uses

AuthRestController --> UserService : uses

AuthRestController --> LoginDto : uses

AuthRestController --> RegisterDto : uses

ProductController --> ProductService : uses

JwtFilter --> JwtUtil : uses

SecurityConfig --> JwtFilter : uses

UserRepository ..> User : manages

FakeUserRepository ..> User : manages
```
