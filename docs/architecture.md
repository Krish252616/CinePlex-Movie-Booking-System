# CinePlex — Architecture & Design Notes

## 1. Layered architecture

The application is organised into four well-separated layers. Each layer
depends only on the one directly below it, never sideways or upward.

```
  ┌──────────────────────────────────────────────┐
  │  UI Layer       (com.movieticket.ui)         │
  │  Swing JFrames, event handlers, navigation   │
  ├──────────────────────────────────────────────┤
  │  Service Layer  (com.movieticket.service)    │
  │  Business rules, validation, pricing         │
  ├──────────────────────────────────────────────┤
  │  DAO Layer      (com.movieticket.dao)        │
  │  JDBC PreparedStatement queries              │
  ├──────────────────────────────────────────────┤
  │  Database       (MySQL 8.0, cineplex_db)     │
  │  5 tables + foreign keys + cascades          │
  └──────────────────────────────────────────────┘
```

This layout was chosen so that every concern lives in exactly one place.
Want to switch the UI to JavaFX? Replace one package. Want to swap MySQL for
PostgreSQL? Edit `db.properties` and a couple of DAO queries — nothing else
changes.

## 2. Why these specific design choices

### POJOs, not JPA entities
Plain Old Java Objects keep the project lightweight and dependency-free, which
matches the syllabus emphasis on understanding JDBC fundamentals before
introducing ORMs.

### DAO pattern with `PreparedStatement`
Every SQL statement is parameterised. There is no string concatenation
anywhere in the DAO layer, which makes SQL injection structurally impossible.
This is the single most important security decision in the project.

### Externalised credentials
`db.properties` lives outside source code and is gitignored. The repository
ships with `db.properties.example` so a new developer can copy it and fill in
their own values. Real credentials never end up in version control.

### SHA-256 password hashing
`PasswordUtil` hashes every password before insertion. The DB schema only ever
sees the hex digest. This means even if the database is leaked, plaintext
passwords are not exposed. (For a real production system, BCrypt or Argon2
with a per-user salt would be preferred — that is a documented future
upgrade.)

### Singleton `Session`
Holds the currently-logged-in `User`. Frames never pass the user through
constructors; they consult `Session.get()` directly. This keeps frame APIs
simple and lets the logout button instantly invalidate state everywhere.

### Reusable `UI` factory and `Theme` constants
Every styled button, text field, card, and label comes from one factory. This
is what makes the dark cinema look feel uniform without copying styling code
across eight frames.

## 3. Data model summary

| Table       | Holds                                      | Notes                                |
|-------------|--------------------------------------------|--------------------------------------|
| `users`     | Accounts (USER or ADMIN)                   | email is UNIQUE, password is hashed  |
| `theatres`  | Physical venues                            | totalSeats default 60 (6×10 grid)    |
| `movies`    | Catalogue                                  | `now_showing` toggle                 |
| `showtimes` | A movie × theatre × date × time × price    | FK → movies, theatres                |
| `bookings`  | A user's booking for a showtime            | seats stored as CSV string           |

Relationships:
- `users` 1 ── N `bookings`
- `showtimes` 1 ── N `bookings`
- `movies` 1 ── N `showtimes`
- `theatres` 1 ── N `showtimes`

All FKs are `ON DELETE CASCADE`, which makes admin cleanup straightforward.

## 4. Pricing logic

`BookingService.calculateTotal(basePrice, seats)` walks the seat list and
adds `VIP_EXTRA` (₹100) for any seat in row A or B. This is intentionally
simple, deterministic, and unit-tested — no DB round-trips, no surprises.

## 5. Concurrency note

This is a single-user desktop app, so we do not worry about row-level locking
or distributed seat reservations. `BookingService.book()` does perform a
last-second check against `BookingDAO.seatsTakenFor()` so that two stale
clients cannot accidentally double-book the same seat. In a multi-user
scenario this would need a transaction with `SELECT ... FOR UPDATE` or
optimistic concurrency control.

## 6. Testing strategy

| Type          | What we test                                    | Where                          |
|---------------|--------------------------------------------------|--------------------------------|
| Unit          | `PasswordUtil` hash determinism, known vectors  | `PasswordUtilTest`             |
| Unit          | `BookingService` price math, VIP rules           | `BookingServiceTest`           |
| Unit          | POJO methods (`isAdmin`, `countSeats`)           | `ModelTest`                    |
| Integration   | DAO ↔ MySQL (manual, via running schema.sql)     | exercised by the running app   |

Every test in the automated suite runs without a database, so the build is
fast and reproducible. Integration testing is performed by running the app
against the seeded database and exercising each user journey end-to-end.

## 7. Build & artifact pipeline

```
mvn clean       → wipes target/
mvn compile     → compiles to target/classes
mvn test        → runs all JUnit tests
mvn package     → produces target/MovieBookingSystem-1.0.0.jar (fat JAR via Shade)
mvn deploy      → would upload to Artifactory if a repo is configured
```

The Shade plugin embeds the MySQL Connector/J driver inside the final JAR so
end users only need a JRE — no manual classpath wrangling.

## 8. Threat model (brief)

| Threat                          | Mitigation                                 |
|--------------------------------|--------------------------------------------|
| SQL injection                  | `PreparedStatement` everywhere             |
| Plaintext passwords in DB      | SHA-256 hashing in `PasswordUtil`          |
| Credentials leaked via Git     | `db.properties` is `.gitignore`d           |
| Double-booking of seats        | Last-second seat-availability re-check     |
| Admin actions by non-admins    | UI hides admin nav; backend trusts session |
| Untrusted user input in fields | Email regex, length limits, numeric parse  |

The remaining caveats (no salt on hashes, single-process app, no rate-limiting)
are acceptable for an academic desktop project but would all need attention
before a production deployment.
