-- ============================================================
-- CineBook - Supabase PostgreSQL Database Schema
-- Production-Ready DDL with Foreign Keys & Constraints
-- ============================================================

-- Drop tables if existing (in reverse dependency order)
DROP TABLE IF EXISTS booking_seats CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS seats CASCADE;
DROP TABLE IF EXISTS shows CASCADE;
DROP TABLE IF EXISTS movies CASCADE;
DROP TABLE IF EXISTS coupons CASCADE;

-- 1. Movies Table
CREATE TABLE movies (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    genre VARCHAR(100) NOT NULL,
    duration INT NOT NULL, -- duration in minutes
    language VARCHAR(50) NOT NULL,
    rating VARCHAR(20) NOT NULL, -- e.g. "PG-13", "U/A", "R"
    poster_url TEXT,
    release_date DATE,
    status VARCHAR(50) NOT NULL DEFAULT 'NOW_SHOWING' -- 'NOW_SHOWING', 'UPCOMING'
);

-- 2. Shows Table (Screenings)
CREATE TABLE shows (
    id BIGSERIAL PRIMARY KEY,
    movie_id BIGINT NOT NULL REFERENCES movies(id) ON DELETE CASCADE,
    show_date DATE NOT NULL,
    show_time VARCHAR(20) NOT NULL, -- e.g. '04:00 PM', '07:30 PM', '09:00 PM'
    active BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT uq_movie_show_slot UNIQUE (movie_id, show_date, show_time)
);

-- 3. Seats Table (40 seats per show: Rows A-B Regular, C-D Premium, E VIP)
CREATE TABLE seats (
    id BIGSERIAL PRIMARY KEY,
    show_id BIGINT NOT NULL REFERENCES shows(id) ON DELETE CASCADE,
    seat_code VARCHAR(10) NOT NULL, -- 'A1' through 'E8'
    seat_type VARCHAR(20) NOT NULL, -- 'REGULAR', 'PREMIUM', 'VIP'
    is_booked BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uq_show_seat UNIQUE (show_id, seat_code)
);

-- 4. Bookings Table
CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_id VARCHAR(30) UNIQUE NOT NULL, -- 'BK1001', 'BK1002', etc.
    show_id BIGINT NOT NULL REFERENCES shows(id),
    total_amount NUMERIC(10, 2) NOT NULL,
    discount_amount NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
    booking_status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED', -- 'CONFIRMED', 'CANCELLED'
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT NOW(),
    cancelled_at TIMESTAMP WITHOUT TIME ZONE NULL
);

-- 5. Booking Seats Mapping Table
CREATE TABLE booking_seats (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL REFERENCES bookings(id) ON DELETE CASCADE,
    seat_id BIGINT NOT NULL REFERENCES seats(id),
    price NUMERIC(10, 2) NOT NULL,
    CONSTRAINT uq_booking_seat UNIQUE (booking_id, seat_id)
);

-- 6. Coupons Table
CREATE TABLE coupons (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) UNIQUE NOT NULL,
    discount_percentage NUMERIC(5, 2) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT true
);

-- Indexes for performance
CREATE INDEX idx_shows_movie_id ON shows(movie_id);
CREATE INDEX idx_shows_date ON shows(show_date);
CREATE INDEX idx_seats_show_id ON seats(show_id);
CREATE INDEX idx_seats_booked ON seats(show_id, is_booked);
CREATE INDEX idx_bookings_booking_id ON bookings(booking_id);
CREATE INDEX idx_booking_seats_booking ON booking_seats(booking_id);

