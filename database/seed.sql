-- ============================================================
-- CineBook - Supabase Initial Seed Data
-- ============================================================

-- 1. Insert Movies
INSERT INTO movies (title, description, genre, duration, language, rating, poster_url, release_date, status) VALUES
(
    'Interstellar',
    'When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft, along with a team of researchers, to find a new planet for humans.',
    'Sci-Fi, Adventure, Drama',
    169,
    'English',
    'PG-13',
    'https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=800&q=80',
    '2014-11-07',
    'NOW_SHOWING'
),
(
    'Inception',
    'A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his tragic past may doom the project and his team to disaster.',
    'Action, Sci-Fi, Thriller',
    148,
    'English',
    'PG-13',
    'https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=800&q=80',
    '2010-07-16',
    'NOW_SHOWING'
),
(
    'The Dark Knight',
    'When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice.',
    'Action, Crime, Drama',
    152,
    'English',
    'PG-13',
    'https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=800&q=80',
    '2008-07-18',
    'NOW_SHOWING'
),
(
    'Oppenheimer',
    'The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.',
    'Biography, Drama, History',
    180,
    'English',
    'R',
    'https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=800&q=80',
    '2023-07-21',
    'NOW_SHOWING'
),
(
    'Dune: Part Two',
    'Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.',
    'Action, Adventure, Sci-Fi',
    166,
    'English',
    'PG-13',
    'https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80',
    '2024-03-01',
    'NOW_SHOWING'
),
(
    'Gladiator II',
    'After his home is conquered by the tyrannical emperors who now lead Rome, Lucius must enter the Colosseum and look to his past to find strength and honor.',
    'Action, Adventure, Drama',
    148,
    'English',
    'R',
    'https://images.unsplash.com/photo-1533488765986-dfa2a9939acd?auto=format&fit=crop&w=800&q=80',
    '2024-11-22',
    'UPCOMING'
);

-- 2. Insert Shows (for today and next 2 days)
INSERT INTO shows (movie_id, show_date, show_time, active) VALUES
(1, CURRENT_DATE, '04:00 PM', true),
(1, CURRENT_DATE, '07:30 PM', true),
(1, CURRENT_DATE, '10:15 PM', true),
(2, CURRENT_DATE, '01:30 PM', true),
(2, CURRENT_DATE, '06:00 PM', true),
(3, CURRENT_DATE, '05:00 PM', true),
(3, CURRENT_DATE, '09:00 PM', true),
(4, CURRENT_DATE, '02:00 PM', true),
(4, CURRENT_DATE, '08:00 PM', true),
(5, CURRENT_DATE, '03:30 PM', true),
(5, CURRENT_DATE, '07:00 PM', true);

-- Helper procedure to populate 40 seats for every show currently in the table
DO $$
DECLARE
    show_rec RECORD;
    row_char CHAR;
    col_num INT;
    seat_cd VARCHAR(10);
    seat_tp VARCHAR(20);
BEGIN
    FOR show_rec IN SELECT id FROM shows LOOP
        -- Rows A-B: REGULAR (150)
        -- Rows C-D: PREMIUM (250)
        -- Row E: VIP (400)
        FOR r IN 0..4 LOOP
            row_char := CHR(65 + r); -- 'A', 'B', 'C', 'D', 'E'
            IF r < 2 THEN
                seat_tp := 'REGULAR';
            ELSIF r < 4 THEN
                seat_tp := 'PREMIUM';
            ELSE
                seat_tp := 'VIP';
            END IF;

            FOR c IN 1..8 LOOP
                seat_cd := row_char || c::text;
                INSERT INTO seats (show_id, seat_code, seat_type, is_booked)
                VALUES (show_rec.id, seat_cd, seat_tp, false)
                ON CONFLICT (show_id, seat_code) DO NOTHING;
            END LOOP;
        END LOOP;
    END LOOP;
END $$;

-- 3. Insert Coupons
INSERT INTO coupons (code, discount_percentage, active) VALUES
('CINE10', 10.00, true),
('VIP20', 20.00, true),
('WELCOME50', 50.00, true);

