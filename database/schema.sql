
-- ================================================================
--  CinePlex - Movie Ticket Booking System
--  Database Schema (MySQL 8.0+)
--  Run this once before starting the application.
-- ================================================================

DROP DATABASE IF EXISTS cineplex_db;
CREATE DATABASE cineplex_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE cineplex_db;

-- ----------------------------------------------------------------
-- users
-- ----------------------------------------------------------------
CREATE TABLE users (
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    full_name    VARCHAR(100) NOT NULL,
    email        VARCHAR(120) NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,           -- stored as SHA-256 hex
    phone        VARCHAR(20),
    role         ENUM('USER','ADMIN') NOT NULL DEFAULT 'USER',
    created_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------
-- theatres
-- ----------------------------------------------------------------
CREATE TABLE theatres (
    theatre_id   INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    city         VARCHAR(60)  NOT NULL,
    address      VARCHAR(255),
    total_seats  INT NOT NULL DEFAULT 60
);

-- ----------------------------------------------------------------
-- movies
-- ----------------------------------------------------------------
CREATE TABLE movies (
    movie_id     INT AUTO_INCREMENT PRIMARY KEY,
    title        VARCHAR(150) NOT NULL,
    genre        VARCHAR(60),
    language     VARCHAR(40),
    duration_min INT,
    rating       VARCHAR(10),              -- U / UA / A
    description  TEXT,
    poster_path  VARCHAR(255),
    now_showing  BOOLEAN NOT NULL DEFAULT TRUE
);

-- ----------------------------------------------------------------
-- showtimes
-- ----------------------------------------------------------------
CREATE TABLE showtimes (
    showtime_id  INT AUTO_INCREMENT PRIMARY KEY,
    movie_id     INT NOT NULL,
    theatre_id   INT NOT NULL,
    show_date    DATE NOT NULL,
    show_time    TIME NOT NULL,
    price        DECIMAL(8,2) NOT NULL,
    CONSTRAINT fk_show_movie   FOREIGN KEY (movie_id)   REFERENCES movies(movie_id)   ON DELETE CASCADE,
    CONSTRAINT fk_show_theatre FOREIGN KEY (theatre_id) REFERENCES theatres(theatre_id) ON DELETE CASCADE
);

-- ----------------------------------------------------------------
-- bookings
-- ----------------------------------------------------------------
CREATE TABLE bookings (
    booking_id   INT AUTO_INCREMENT PRIMARY KEY,
    user_id      INT NOT NULL,
    showtime_id  INT NOT NULL,
    seats        VARCHAR(255) NOT NULL,    -- e.g. "A1,A2,B5"
    total_amount DECIMAL(10,2) NOT NULL,
    status       ENUM('CONFIRMED','CANCELLED') NOT NULL DEFAULT 'CONFIRMED',
    booked_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_book_user FOREIGN KEY (user_id)     REFERENCES users(user_id)         ON DELETE CASCADE,
    CONSTRAINT fk_book_show FOREIGN KEY (showtime_id) REFERENCES showtimes(showtime_id) ON DELETE CASCADE
);

-- ================================================================
--  SEED DATA
-- ================================================================

-- Default users  (passwords are SHA-256 hex of: admin123 / user123)
INSERT INTO users (full_name, email, password, phone, role) VALUES
('Administrator','admin@cineplex.com',
 '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9','9999999999','ADMIN'),
('Rahul Sharma','rahul@gmail.com',
 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446','9876543210','USER'),
('Priya Singh','priya@gmail.com',
 'e606e38b0d8c19b24cf0ee3808183162ea7cd63ff7912dbb22b5e803286b4446','9876500000','USER');

-- Theatres
INSERT INTO theatres (name, city, address, total_seats) VALUES
('PVR Phoenix',   'Mumbai','Lower Parel',   60),
('INOX Nehru Place','Delhi','South Delhi',  60),
('Cinepolis Forum','Bangalore','Koramangala', 60);

-- Movies
INSERT INTO movies (title, genre, language, duration_min, rating, description, now_showing) VALUES
('Pathaan',     'Action',  'Hindi',   146, 'UA', 'A spy thriller with high-octane action.',               TRUE),
('Oppenheimer', 'Drama',   'English', 180, 'UA', 'The story of the father of the atomic bomb.',           TRUE),
('3 Idiots',    'Comedy',  'Hindi',   170, 'U',  'Three friends search for their lost buddy.',            TRUE),
('Interstellar','Sci-Fi',  'English', 169, 'UA', 'A team travels through a wormhole to save humanity.',   TRUE),
('RRR',         'Action',  'Telugu',  182, 'UA', 'A fictional tale about two legendary revolutionaries.', TRUE),
('Inception',   'Sci-Fi',  'English', 148, 'UA', 'A thief enters dreams to steal secrets.',               FALSE);

-- Showtimes (next 3 days, morning/afternoon/evening)
INSERT INTO showtimes (movie_id, theatre_id, show_date, show_time, price) VALUES
(1,1,CURDATE(),     '10:00:00', 250.00),
(1,1,CURDATE(),     '18:30:00', 300.00),
(1,2,CURDATE(),     '21:00:00', 320.00),
(2,1,CURDATE(),     '14:00:00', 280.00),
(2,3,CURDATE(),     '20:00:00', 350.00),
(3,2,DATE_ADD(CURDATE(),INTERVAL 1 DAY),'11:00:00', 200.00),
(3,3,DATE_ADD(CURDATE(),INTERVAL 1 DAY),'19:00:00', 250.00),
(4,1,DATE_ADD(CURDATE(),INTERVAL 1 DAY),'16:00:00', 300.00),
(4,2,DATE_ADD(CURDATE(),INTERVAL 2 DAY),'18:00:00', 300.00),
(5,3,DATE_ADD(CURDATE(),INTERVAL 2 DAY),'20:30:00', 280.00);

-- Sample booking
INSERT INTO bookings (user_id, showtime_id, seats, total_amount, status) VALUES
(2, 1, 'A1,A2', 700.00, 'CONFIRMED');

-- Verify
SELECT 'Users:' AS '', COUNT(*) FROM users
UNION ALL SELECT 'Movies:', COUNT(*) FROM movies
UNION ALL SELECT 'Theatres:', COUNT(*) FROM theatres
UNION ALL SELECT 'Showtimes:', COUNT(*) FROM showtimes
UNION ALL SELECT 'Bookings:', COUNT(*) FROM bookings;
