# 🎬 CinePlex — Online Movie Ticket Booking System

> **A standalone Java desktop application built with Swing and MySQL.**
> Developed as a final-year project covering UML design, GUI development,
> database integration, JUnit testing, and Git/GitHub collaboration.

---

## 📋 Features

| Module            | Capability                                                                   |
|-------------------|-------------------------------------------------------------------------------|
| 🔐 Authentication | Register / Sign-in with SHA-256 hashed passwords, email + phone validation   |
| 🎬 Browse         | Filter by genre, language, search by title, clickable movie cards            |
| 📅 Showtimes      | View upcoming shows across multiple theatres with date/time/price            |
| 🪑 Seat picker    | 6 × 10 interactive grid, VIP rows (A & B) with surcharge, max 8 per booking  |
| 🎫 My bookings    | View personal history, cancel confirmed bookings                             |
| 👑 Admin panel    | Stats overview, manage movies (add / toggle / delete), cancel any booking, list all users |

---

## 🏗️ Architecture

Clean four-layer separation:

```
  ┌─────────────────────────────────────────┐
  │  UI Layer          (Swing JFrames)      │  ← presentation
  ├─────────────────────────────────────────┤
  │  Service Layer     (business logic)     │  ← validation, pricing
  ├─────────────────────────────────────────┤
  │  DAO Layer         (JDBC + PreparedStmt)│  ← SQL access
  ├─────────────────────────────────────────┤
  │  MySQL Database    (cineplex_db)        │  ← persistence
  └─────────────────────────────────────────┘
```

Every SQL call uses **`PreparedStatement`** — no string concatenation, no SQL-injection risk.
Passwords are **SHA-256 hashed** before insert; plaintext passwords never touch the database.

---

## 🛠️ Requirements

| Tool          | Minimum version | Check with          |
|---------------|-----------------|---------------------|
| JDK           | 17              | `javac -version`    |
| Maven         | 3.8             | `mvn -v`            |
| MySQL Server  | 8.0             | `mysql --version`   |

---

## 🚀 Getting Started

### 1. Create the database

Open MySQL client (CLI or Workbench) and run the schema script:

```bash
mysql -u root -p < database/schema.sql
```

This creates the `cineplex_db` database, all tables, and inserts seed data.

### 2. Configure credentials

Copy the template and fill in **your own** MySQL credentials:

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
# then edit db.properties — set db.user and db.password
```

`db.properties` is gitignored so your real credentials never leave your machine.

### 3. Run the application

Two options:

**A. Run directly via Maven (fastest):**
```bash
mvn compile exec:java
```

**B. Build a fat JAR and run it:**
```bash
mvn clean package
java -jar target/MovieBookingSystem-1.0.0.jar
```

### 4. Sign in

| Role  | Email                  | Password   |
|-------|------------------------|------------|
| User  | `rahul@gmail.com`      | `user123`  |
| User  | `priya@gmail.com`      | `user123`  |
| Admin | `admin@cineplex.com`   | `admin123` |

You can also register a brand-new account via the "Create new account" button.

---

## 🧪 Running the Tests

JUnit 5 tests cover password hashing, booking price math, and model behaviour
without touching the database:

```bash
mvn test
```

Expected output: **17 tests passing** across 3 test classes.

---

## 📁 Project Structure

```
MovieBookingSystem/
├── pom.xml                             ← Maven build config
├── .gitignore                          ← excludes target/, db.properties, IDE files
├── README.md                           ← this file
│
├── database/
│   └── schema.sql                      ← MySQL DDL + seed data
│
├── docs/
│   ├── class-diagram.svg               ← UML class diagram
│   ├── er-diagram.svg                  ← Entity-Relationship diagram
│   └── architecture.md                 ← design notes
│
└── src/
    ├── main/
    │   ├── java/com/movieticket/
    │   │   ├── MainApp.java            ← entry point
    │   │   ├── db/DBConnection.java    ← JDBC connection factory
    │   │   ├── model/                  ← POJOs
    │   │   │   ├── User.java
    │   │   │   ├── Movie.java
    │   │   │   ├── Theatre.java
    │   │   │   ├── Showtime.java
    │   │   │   └── Booking.java
    │   │   ├── dao/                    ← data-access objects
    │   │   │   ├── UserDAO.java
    │   │   │   ├── MovieDAO.java
    │   │   │   ├── TheatreDAO.java
    │   │   │   ├── ShowtimeDAO.java
    │   │   │   └── BookingDAO.java
    │   │   ├── service/                ← business logic
    │   │   │   ├── AuthService.java
    │   │   │   └── BookingService.java
    │   │   ├── util/
    │   │   │   ├── PasswordUtil.java   ← SHA-256 hashing
    │   │   │   ├── Session.java        ← current user holder
    │   │   │   ├── Theme.java          ← colour/font constants
    │   │   │   └── UI.java             ← reusable widget factory
    │   │   └── ui/                     ← Swing screens
    │   │       ├── LoginFrame.java
    │   │       ├── RegisterFrame.java
    │   │       ├── HomeFrame.java
    │   │       ├── MovieDetailFrame.java
    │   │       ├── SeatSelectionFrame.java
    │   │       ├── MyBookingsFrame.java
    │   │       ├── AdminDashboardFrame.java
    │   │       └── NavBar.java
    │   └── resources/
    │       ├── db.properties           ← (gitignored) your credentials
    │       └── db.properties.example   ← safe template
    │
    └── test/java/com/movieticket/
        ├── PasswordUtilTest.java
        ├── BookingServiceTest.java
        └── ModelTest.java
```

---

## 🌐 Git & GitHub Workflow

The project was developed with version control from day one:

```bash
# First-time setup
git init
git add .
git commit -m "Initial commit: project scaffold and UML diagrams"
git branch -M main
git remote add origin https://github.com/<you>/MovieBookingSystem.git
git push -u origin main

# Typical feature workflow
git checkout -b feature/seat-selection
# ...write code + tests...
git add .
git commit -m "Add seat-selection screen with VIP pricing"
git push -u origin feature/seat-selection
# Open a Pull Request on GitHub → review → merge to main
```

**Branching strategy used**

| Branch           | Purpose                                    |
|------------------|--------------------------------------------|
| `main`           | stable, demo-ready                         |
| `develop`        | integration of in-progress features        |
| `feature/*`      | individual features (auth, seats, admin …) |
| `bugfix/*`       | targeted bug fixes                         |

`db.properties` is excluded by `.gitignore` so credentials are never pushed.

---

## 📦 Build & Artifactory

The Maven **Shade** plugin bundles all dependencies into a single runnable fat JAR:

```bash
mvn clean package
# produces: target/MovieBookingSystem-1.0.0.jar  (with MySQL driver embedded)
```

This artifact can be uploaded to a JFrog Artifactory / Nexus repository for
distribution, or kept as a GitHub Release asset:

```bash
mvn deploy -DaltDeploymentRepository=artifactory::default::https://<repo-url>/
```

---

## 🎨 UI Theme

- **Dark cinema aesthetic** — deep navy + charcoal
- **Accent** — electric indigo `#6C63FF`
- **Status colours** — green (confirmed), red (cancelled), amber (warnings)
- **Hover effects** on every card and button for responsiveness

---

## 🧱 Design Artefacts

See the `docs/` directory for:

- **Class diagram** (`class-diagram.svg`) — all model + DAO + service + UI classes with relationships
- **ER diagram** (`er-diagram.svg`) — 5 entities with primary/foreign keys and cardinalities
- **Architecture notes** (`architecture.md`) — design decisions, trade-offs, threat model

---

## 🔒 Security Notes

- Passwords are stored only as **SHA-256 hex digests** (see `PasswordUtil`).
  In a production system you would upgrade to BCrypt/Argon2 with a per-user salt.
- All SQL is parameterised via `PreparedStatement`. No user input reaches raw SQL.
- `db.properties` is gitignored — credentials never leave the developer machine.
- Basic client-side validation: email regex, 6-char password minimum, 10-digit phone.

---

## 🧯 Troubleshooting

| Symptom                                       | Fix                                                                 |
|-----------------------------------------------|---------------------------------------------------------------------|
| "Database Connection Failed" popup on launch  | MySQL not running, or `db.properties` has wrong credentials         |
| `No suitable driver found`                    | Run via `mvn exec:java` — it puts the MySQL driver on the classpath |
| `Table 'cineplex_db.xxx' doesn't exist`       | You forgot to run `database/schema.sql`                             |
| Demo passwords don't work                     | Ran schema without seed data — re-run `schema.sql` fully            |
| Rupee symbol shows as `?`                     | Terminal encoding — the Swing UI itself renders it fine             |

---

## 📜 License

Academic project — free to use for learning. Not intended for production deployment.

---

## 👤 Author

Built as a Java full-stack capstone exercise covering:

> *Standalone Java project, UML & database design, GUI with Swing,
> JDBC database connection, Git/GitHub collaboration, JUnit unit testing,
> integration testing, Maven build & Artifactory management.*
