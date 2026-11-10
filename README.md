# CineBook – Full Stack Cinema Booking Platform

A production-oriented, full-stack movie ticket booking web application for a single-screen cinema. Built with **React 18 + TypeScript + Vite + Tailwind CSS** on the frontend, **Java 17+ + Spring Boot 3 + Spring Data JPA** on the backend, and **Supabase PostgreSQL** as the transactional relational database.

CineBook migrates and enhances the core Object-Oriented principles, business logic, seat categories, tiered pricing, and atomic transactions from the original Java console architecture into an enterprise three-layer system.

---

## Table of Contents
1. [Architecture Overview](#1-architecture-overview)
2. [Technology Stack](#2-technology-stack)
3. [Project Structure](#3-project-structure)
4. [Database Schema (Supabase PostgreSQL)](#4-database-schema-supabase-postgresql)
5. [Supabase Setup Guide](#5-supabase-setup-guide)
6. [Business Logic & Concurrency Safety](#6-business-logic--concurrency-safety)
7. [REST API Documentation](#7-rest-api-documentation)
8. [Frontend Design & Pages](#8-frontend-design--pages)
9. [Local Development Setup](#9-local-development-setup)
10. [Deployment Guide (Vercel & Render)](#10-deployment-guide-vercel--render)

---

## 1. Architecture Overview

CineBook strictly adheres to a decoupled **Three-Layer Architecture**:

```
┌─────────────────────────────────┐
│     React 18 + TypeScript       │
│  (Tailwind CSS, Vite, Axios)    │  <--- Frontend Layer (Client)
└───────────────┬─────────────────┘
                │ REST API Calls (/api/*)
                ▼
┌─────────────────────────────────┐
│    Spring Boot 3 REST API       │
│  (Controllers, Services, DTOs)  │  <--- Backend Application Layer
│     Pessimistic Concurrency     │
└───────────────┬─────────────────┘
                │ JDBC / JPA (Hibernate)
                ▼
┌─────────────────────────────────┐
│      Supabase PostgreSQL        │
│  (ACID Tables, Foreign Keys,    │  <--- Persistence Layer (Database)
│     Unique Constraints)         │
└─────────────────────────────────┘
```

### Key Architectural Tenets:
- **Java is the Source of Truth:** All seat validation, pricing computations, coupon discounting, and cancellation refund percentages are calculated strictly on the Java backend using `BigDecimal`. Client-submitted totals are never trusted.
- **Credential Isolation:** Supabase connection credentials and database passwords reside exclusively on the Spring Boot backend via environment variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`). No database secrets or Supabase service-role keys are ever exposed to the client.
- **Pessimistic Locking & Atomic Transactions:** Seat selections are validated and locked atomically with Spring `@Transactional(isolation = Isolation.REPEATABLE_READ)` and JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)` to eliminate race conditions and double-booking.

---

## 2. Technology Stack

### Frontend
- **Framework:** React 18 (TypeScript)
- **Build Tool:** Vite 5
- **Styling:** Tailwind CSS (Dark navy `#070a13`, purple `#8b5cf6`, and gold `#f59e0b` cinema palette)
- **Routing:** React Router DOM v6
- **HTTP Client:** Axios
- **Icons:** Lucide React
- **Animations:** Canvas Confetti

### Backend
- **Language:** Java 17+ (Eclipse Temurin OpenJDK)
- **Framework:** Spring Boot 3.2.5
- **Web:** Spring Web (REST Controllers, CORS filters)
- **ORM / Persistence:** Spring Data JPA, Hibernate 6
- **Validation:** Spring Boot Starter Validation (Bean Validation / Jakarta)
- **Financial Precision:** `java.math.BigDecimal`
- **Database Drivers:** PostgreSQL JDBC Driver (`org.postgresql:postgresql`), H2 In-Memory Driver (`com.h2database:h2` for local fallback)

### Database
- **Provider:** Supabase Managed PostgreSQL
- **Features:** Relational schema, foreign keys, unique constraints, performance indexes

### Deployment
- **Frontend:** Vercel (Edge CDN, SPA rewrites)
- **Backend:** Render (Dockerized Spring Boot container / Web Service)

---

## 3. Project Structure

```text
d:/Booking/
├── .env.example                               # Environment template for cloud deployments
├── render.yaml                                # Render Blueprint deployment descriptor
├── README.md                                  # Comprehensive documentation
├── start-frontend.bat                         # 1-Click script to run Vite dev server
├── start-backend.bat                          # 1-Click script to run Spring Boot API
│
├── database/                                  # Database migrations and seed data
│   ├── schema.sql                             # DDL for Supabase PostgreSQL tables & indexes
│   └── seed.sql                               # DML initial seed data (movies, shows, 40 seats)
│
├── backend/                                   # Spring Boot 3 Java Application
│   ├── Dockerfile                             # Multi-stage production Docker build
│   ├── pom.xml                                # Maven dependencies descriptor
│   ├── build.gradle                           # Gradle build script
│   └── src/main/
│       ├── java/com/cinebook/
│       │   ├── CineBookApplication.java       # Spring Boot main entry point
│       │   ├── config/
│       │   │   ├── CorsConfig.java            # Cross-Origin resource sharing policy
│       │   │   └── DataInitializer.java       # Auto-seeds database on startup if empty
│       │   ├── controller/
│       │   │   ├── MovieController.java       # /api/movies endpoints
│       │   │   ├── ShowController.java        # /api/shows & /api/movies/{id}/shows
│       │   │   ├── SeatController.java        # /api/shows/{id}/seats
│       │   │   ├── BookingController.java     # /api/bookings & cancellation
│       │   │   └── CouponController.java      # /api/coupons/validate
│       │   ├── dto/
│       │   │   ├── MovieResponseDto.java
│       │   │   ├── ShowResponseDto.java
│       │   │   ├── SeatResponseDto.java
│       │   │   ├── BookingRequestDto.java
│       │   │   ├── BookingResponseDto.java
│       │   │   ├── CancellationResponseDto.java
│       │   │   ├── CouponValidationRequestDto.java
│       │   │   └── CouponValidationResponseDto.java
│       │   ├── entity/
│       │   │   ├── Movie.java
│       │   │   ├── Show.java
│       │   │   ├── Seat.java
│       │   │   ├── Booking.java
│       │   │   ├── BookingSeat.java
│       │   │   └── Coupon.java
│       │   ├── exception/
│       │   │   ├── BookingException.java
│       │   │   ├── ResourceNotFoundException.java
│       │   │   └── GlobalExceptionHandler.java# Uniform HTTP error responses
│       │   ├── model/
│       │   │   └── SeatType.java              # REGULAR(150), PREMIUM(250), VIP(400)
│       │   ├── repository/
│       │   │   ├── MovieRepository.java
│       │   │   ├── ShowRepository.java
│       │   │   ├── SeatRepository.java        # Pessimistic locking queries
│       │   │   ├── BookingRepository.java
│       │   │   └── CouponRepository.java
│       │   └── service/
│       │       ├── MovieService.java
│       │       ├── ShowService.java
│       │       ├── SeatService.java           # 40-seat grid generator
│       │       ├── BookingService.java        # Atomic booking & 80% refund logic
│       │       └── CouponService.java         # Coupon percentage discounting
│       └── resources/
│           ├── application.yml                # Spring Boot configuration
│           └── application-local.yml
│
└── frontend/                                  # React 18 + Vite TypeScript SPA
    ├── package.json                           # Dependencies & scripts
    ├── tsconfig.json                          # TypeScript configuration
    ├── vite.config.ts                         # Vite configuration with proxy
    ├── tailwind.config.js                     # Cinema dark theme & colors
    ├── vercel.json                            # Vercel SPA rewrites
    └── src/
        ├── App.tsx                            # Root router layout
        ├── main.tsx                           # React entry point
        ├── index.css                          # Tailwind directives & glow effects
        ├── types/                             # TypeScript interfaces (Movie, Show, Seat...)
        ├── api/client.ts                      # Axios API functions
        ├── context/BookingContext.tsx         # Live booking cart & seat selection state
        ├── components/
        │   ├── Navbar.tsx                     # Header navigation with branding
        │   ├── Footer.tsx                     # Cinema info & tiers
        │   ├── MovieCard.tsx                  # Poster card with ratings & specs
        │   ├── SeatGrid.tsx                   # Interactive 5x8 auditorium seat map
        │   ├── SeatLegend.tsx                 # Categories and status indicators
        │   └── BookingSummaryCard.tsx         # Live price calculation & coupon engine
        └── pages/
            ├── HomePage.tsx                   # Hero showcase, Now Showing, Upcoming
            ├── MovieDetailsPage.tsx           # Synopsis, specs & showtime slots
            ├── ShowtimeSelectionPage.tsx      # Filter by date & search showtimes
            ├── SeatSelectionPage.tsx          # Real-time 5x8 seat selection
            ├── CheckoutPage.tsx               # Review, simulated payment & confirmation
            ├── ConfirmationPage.tsx           # Digital ticket boarding pass & confetti
            └── MyBookingsPage.tsx             # Booking lookup & 80% refund cancellation
```

---

## 4. Database Schema (Supabase PostgreSQL)

The database schema is fully defined in [`database/schema.sql`](file:///d:/Booking/database/schema.sql) and seeded via [`database/seed.sql`](file:///d:/Booking/database/seed.sql).

### Tables Summary

| Table | Description | Key Constraints |
|---|---|---|
| `movies` | Catalog of screened and upcoming films | Primary key `id` |
| `shows` | Individual screening slots | `movie_id` FK, `UNIQUE(movie_id, show_date, show_time)` |
| `seats` | 40 seats per show (Rows A–E, Cols 1–8) | `show_id` FK, `UNIQUE(show_id, seat_code)` |
| `bookings` | Customer reservations | Unique `booking_id` (`BK1001`...), status |
| `booking_seats` | Join table associating booked seats with price | `booking_id` FK, `seat_id` FK, `UNIQUE(booking_id, seat_id)` |
| `coupons` | Promotional discount codes (`CINE10`, `VIP20`...) | Unique `code`, `discount_percentage` |

### Seating Tiers & Fixed Pricing (Enforced via `SeatType`)

| Category | Auditorium Rows | Seat Codes | Price (INR) |
|---|---|---|:---:|
| **Regular** | Rows A & B | `A1`–`A8`, `B1`–`B8` | **Rs. 150.00** |
| **Premium** | Rows C & D | `C1`–`C8`, `D1`–`D8` | **Rs. 250.00** |
| **VIP Recliner** | Row E | `E1`–`E8` | **Rs. 400.00** |

---

## 5. Supabase Setup Guide

1. Log in to [Supabase](https://supabase.com) and create a new project (e.g., `cinebook`).
2. Go to **SQL Editor** in the left sidebar.
3. Copy the contents of [`database/schema.sql`](file:///d:/Booking/database/schema.sql) and click **Run**.
4. Copy the contents of [`database/seed.sql`](file:///d:/Booking/database/seed.sql) and click **Run** to seed initial movies, showtimes, 40 seats for every screening, and promotional coupons.
5. Retrieve your PostgreSQL connection string:
   - Go to **Project Settings** -> **Database**.
   - Under **Connection String**, select **URI** or **JDBC**.
   - Copy your connection string, e.g.:
     `jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres?sslmode=require`
6. Set the environment variables in your backend `.env` or deployment dashboard:
   ```bash
   DB_URL=jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres?sslmode=require
   DB_USERNAME=postgres
   DB_PASSWORD=YOUR_DATABASE_PASSWORD
   ```
7. *(Note: Even without configuring Supabase immediately, the backend includes an embedded H2 in-memory PostgreSQL-mode fallback that auto-initializes all seed data so you can test the full stack locally out of the box).*

---

## 6. Business Logic & Concurrency Safety

### 1. Atomic Multi-Seat Reservation
- When a customer reserves multiple seats (e.g., `C3, C4, E2`), the backend acquires an exclusive write lock on those rows in the database:
  ```java
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT s FROM Seat s WHERE s.show.id = :showId AND s.seatCode IN :seatCodes")
  List<Seat> findByShowIdAndSeatCodeInWithLock(Long showId, List<String> seatCodes);
  ```
- If even **one** seat is already booked (`isBooked == true`), the entire transaction aborts with a descriptive `BookingException`. No partial bookings can occur.

### 2. Pricing & Coupon Validation
- All pricing is derived strictly on the backend from `SeatType.getPrice()` using `BigDecimal`.
- Method overloading (`calculateTotal` with and without coupon) validates discounts against the `coupons` table. For example, coupon `CINE10` provides an exact 10% discount. Unknown coupons apply zero discount.

### 3. Booking Cancellation & 80% Refund Policy
- A booking is cancelled by supplying its unique ID (`BK1001`).
- The backend checks if the booking is already cancelled to prohibit double-cancellations.
- All reserved seats are released back to `isBooked = false`.
- An **80% refund** is calculated on the actual amount paid (`booking.getTotalAmount().multiply(new BigDecimal("0.80"))`).
- Status is updated to `CANCELLED` with a `cancelledAt` timestamp.

---

## 7. REST API Documentation

### Movies

#### `GET /api/movies`
Returns a list of all movies.
```json
[
  {
    "id": 1,
    "title": "Interstellar",
    "genre": "Sci-Fi, Adventure, Drama",
    "duration": 169,
    "language": "English",
    "rating": "PG-13",
    "posterUrl": "https://images.unsplash.com/...",
    "status": "NOW_SHOWING"
  }
]
```

#### `GET /api/movies/{id}`
Returns details for a specific movie.

---

### Shows

#### `GET /api/shows`
Returns all active movie shows.

#### `GET /api/movies/{movieId}/shows`
Returns all scheduled showtimes for a specific movie.
```json
[
  {
    "id": 2,
    "movieId": 1,
    "movieTitle": "Interstellar",
    "showDate": "2026-11-07",
    "showTime": "07:30 PM",
    "availableSeats": 38,
    "totalSeats": 40
  }
]
```

---

### Seats

#### `GET /api/shows/{showId}/seats`
Returns the real-time status of all 40 seats in the 5 × 8 auditorium grid.
```json
[
  {
    "id": 1,
    "showId": 2,
    "seatCode": "A1",
    "seatType": "REGULAR",
    "price": 150.00,
    "isBooked": false
  },
  {
    "id": 19,
    "showId": 2,
    "seatCode": "C3",
    "seatType": "PREMIUM",
    "price": 250.00,
    "isBooked": true
  }
]
```

---

### Bookings

#### `POST /api/bookings`
Atomically creates a new booking.
**Request Body:**
```json
{
  "showId": 2,
  "seatCodes": ["C3", "C4"],
  "couponCode": "CINE10"
}
```
**Response Body (201 Created):**
```json
{
  "id": 1,
  "bookingId": "BK1001",
  "showId": 2,
  "movieTitle": "Interstellar",
  "showDate": "2026-11-07",
  "showTime": "07:30 PM",
  "seatCodes": ["C3", "C4"],
  "seatCategories": "PREMIUM",
  "originalAmount": 500.00,
  "discountAmount": 50.00,
  "totalAmount": 450.00,
  "bookingStatus": "CONFIRMED",
  "createdAt": "2026-11-07T16:45:00"
}
```

#### `GET /api/bookings/{bookingId}`
Fetches booking details by unique ID (e.g., `BK1001`).

#### `GET /api/bookings`
Returns all bookings ordered by latest first.

#### `POST /api/bookings/{bookingId}/cancel`
Cancels the booking, releases all seats, and computes an 80% refund.
**Response Body (200 OK):**
```json
{
  "bookingId": "BK1001",
  "originalAmount": 450.00,
  "refundAmount": 360.00,
  "bookingStatus": "CANCELLED",
  "cancelledAt": "2026-11-07T16:50:00",
  "releasedSeats": ["C3", "C4"],
  "message": "Booking BK1001 cancelled successfully. 80% refund of Rs. 360.00 has been initiated."
}
```

---

### Coupons

#### `POST /api/coupons/validate`
Validates a promotional coupon against an order subtotal.
**Request Body:**
```json
{
  "code": "CINE10",
  "totalAmount": 500.00
}
```
**Response Body (200 OK):**
```json
{
  "code": "CINE10",
  "valid": true,
  "discountPercentage": 10.00,
  "discountAmount": 50.00,
  "finalAmount": 450.00,
  "message": "Coupon 'CINE10' applied successfully! (10% off)"
}
```

---

## 8. Frontend Design & Pages

The user interface uses a **Dark Cinema Luxury aesthetic** (`#070a13` background with purple `#8b5cf6` and amber gold `#f59e0b` accents).

1. **Home Page (`/`)**: Hero showcase with backdrop banner, "Now Showing" catalog, upcoming releases, promotional coupon cards, and cinema quality pillars.
2. **Movie Details (`/movies/:id`)**: Comprehensive film synopsis, duration, language, genre, rating, and showtimes schedule grouped by date.
3. **Showtimes Schedule (`/showtimes`)**: Searchable list of all screenings with date filters and seat count indicators.
4. **Interactive Seat Map (`/seats/:showId`)**: 
   - Realistic curved screen graphic (`All Eyes This Way`).
   - 5 × 8 interactive seating grid with distinct color codes for Regular, Premium, and VIP Recliners.
   - Interactive hover tooltips and selection markers.
   - Dynamic right-side live checkout summary.
5. **Checkout Page (`/checkout`)**:
   - Order breakdown, customer information, payment method selection (Simulated UPI, Card, Net Banking).
   - Server-side atomic booking submission with error handling.
6. **Booking Confirmation (`/confirmation/:bookingId`)**:
   - Perforated digital ticket boarding pass.
   - Booking ID copy button, print pass action (`window.print()`).
   - Celebratory confetti animation.
7. **My Bookings (`/my-bookings`)**:
   - Search booking by ID.
   - Booking history with status pills (`CONFIRMED` / `CANCELLED`).
   - Interactive cancellation modal showing exact 80% refund calculations before confirmation.

---

## 9. Local Development Setup

### Prerequisites
- **Node.js** v18+ and **npm**
- **Java JDK 17+** (Ensure `java` and `javac` are on PATH)

### Step 1: Run the Backend
```bash
cd backend
mvn spring-boot:run
```
*(Or in Windows: run `start-backend.bat`)*

The Spring Boot REST API will start at: `http://localhost:8080`.
The `DataInitializer` will automatically seed the database on the first run.

### Step 2: Run the Frontend
```bash
cd frontend
npm install
npm run dev
```
*(Or in Windows: run `start-frontend.bat`)*

The React application will be available at: `http://localhost:5173`.
Vite is pre-configured to proxy `/api/*` requests directly to `http://localhost:8080`.

---

## 10. Deployment Guide (Vercel & Render)

### A. Deploy Frontend to Vercel
1. Push your repository to GitHub.
2. Log in to [Vercel](https://vercel.com) and click **Add New** -> **Project**.
3. Import your repository and set the **Root Directory** to `frontend`.
4. In **Environment Variables**, add:
   ```
   VITE_API_URL=https://your-backend.onrender.com
   ```
5. Click **Deploy**. Vercel will build the frontend using `npm run build` and route SPA requests using [`frontend/vercel.json`](file:///d:/Booking/frontend/vercel.json).

### B. Deploy Backend to Render
1. Log in to [Render](https://render.com) and select **New** -> **Web Service**.
2. Connect your repository.
3. Set **Runtime** to **Docker** and configure:
   - **Docker Command Context:** `backend`
   - **Dockerfile Path:** `backend/Dockerfile`
4. In **Environment Variables**, configure:
   ```
   PORT=8080
   DB_URL=jdbc:postgresql://db.YOUR_PROJECT_REF.supabase.co:5432/postgres?sslmode=require
   DB_USERNAME=postgres
   DB_PASSWORD=YOUR_SUPABASE_PASSWORD
   DB_DRIVER=org.postgresql.Driver
   ```
5. Click **Create Web Service**. Render will automatically build the multi-stage Docker container and expose your Spring Boot API securely.
# CineBook
