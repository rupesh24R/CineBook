package com.cinebook.config;

import com.cinebook.entity.Coupon;
import com.cinebook.entity.Movie;
import com.cinebook.entity.Show;
import com.cinebook.repository.CouponRepository;
import com.cinebook.repository.MovieRepository;
import com.cinebook.repository.ShowRepository;
import com.cinebook.service.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatically seeds the database with initial movies, shows, 40 seats per show,
 * and promotional coupons if the database is newly initialized.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;
    private final CouponRepository couponRepository;
    private final SeatService seatService;

    public DataInitializer(MovieRepository movieRepository,
                           ShowRepository showRepository,
                           CouponRepository couponRepository,
                           SeatService seatService) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
        this.couponRepository = couponRepository;
        this.seatService = seatService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (movieRepository.count() == 0) {
            log.info("Populating CineBook database with initial seed data...");

            // 1. Movies
            Movie m1 = new Movie(
                    "Interstellar",
                    "When Earth becomes uninhabitable in the future, a farmer and ex-NASA pilot, Joseph Cooper, is tasked to pilot a spacecraft along with a team of researchers to find a new planet for humans.",
                    "Sci-Fi, Adventure, Drama",
                    169,
                    "English",
                    "PG-13",
                    "https://images.unsplash.com/photo-1534447677768-be436bb09401?auto=format&fit=crop&w=800&q=80",
                    LocalDate.of(2014, 11, 7),
                    "NOW_SHOWING"
            );

            Movie m2 = new Movie(
                    "Inception",
                    "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O., but his past may doom his team to disaster.",
                    "Action, Sci-Fi, Thriller",
                    148,
                    "English",
                    "PG-13",
                    "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?auto=format&fit=crop&w=800&q=80",
                    LocalDate.of(2010, 7, 16),
                    "NOW_SHOWING"
            );

            Movie m3 = new Movie(
                    "The Dark Knight",
                    "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological tests of his ability to fight injustice.",
                    "Action, Crime, Drama",
                    152,
                    "English",
                    "PG-13",
                    "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?auto=format&fit=crop&w=800&q=80",
                    LocalDate.of(2008, 7, 18),
                    "NOW_SHOWING"
            );

            Movie m4 = new Movie(
                    "Oppenheimer",
                    "The story of American scientist J. Robert Oppenheimer and his role in the development of the atomic bomb during World War II.",
                    "Biography, Drama, History",
                    180,
                    "English",
                    "R",
                    "https://images.unsplash.com/photo-1440404653325-ab127d49abc1?auto=format&fit=crop&w=800&q=80",
                    LocalDate.of(2023, 7, 21),
                    "NOW_SHOWING"
            );

            Movie m5 = new Movie(
                    "Dune: Part Two",
                    "Paul Atreides unites with Chani and the Fremen while seeking revenge against the conspirators who destroyed his family.",
                    "Action, Adventure, Sci-Fi",
                    166,
                    "English",
                    "PG-13",
                    "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?auto=format&fit=crop&w=800&q=80",
                    LocalDate.of(2024, 3, 1),
                    "NOW_SHOWING"
            );

            Movie m6 = new Movie(
                    "Gladiator II",
                    "After his home is conquered by the tyrannical emperors who lead Rome, Lucius enters the Colosseum looking to his past to find strength and honor.",
                    "Action, Adventure, Drama",
                    148,
                    "English",
                    "R",
                    "https://images.unsplash.com/photo-1533488765986-dfa2a9939acd?auto=format&fit=crop&w=800&q=80",
                    LocalDate.of(2024, 11, 22),
                    "UPCOMING"
            );

            List<Movie> savedMovies = movieRepository.saveAll(List.of(m1, m2, m3, m4, m5, m6));

            // 2. Shows for Today and Tomorrow
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);

            List<Show> shows = new ArrayList<>();
            // Interstellar
            shows.add(new Show(savedMovies.get(0), today, "04:00 PM", true));
            shows.add(new Show(savedMovies.get(0), today, "07:30 PM", true));
            shows.add(new Show(savedMovies.get(0), today, "10:15 PM", true));
            shows.add(new Show(savedMovies.get(0), tomorrow, "07:30 PM", true));

            // Inception
            shows.add(new Show(savedMovies.get(1), today, "01:30 PM", true));
            shows.add(new Show(savedMovies.get(1), today, "06:00 PM", true));
            shows.add(new Show(savedMovies.get(1), tomorrow, "04:00 PM", true));

            // The Dark Knight
            shows.add(new Show(savedMovies.get(2), today, "05:00 PM", true));
            shows.add(new Show(savedMovies.get(2), today, "09:00 PM", true));
            shows.add(new Show(savedMovies.get(2), tomorrow, "08:30 PM", true));

            // Oppenheimer
            shows.add(new Show(savedMovies.get(3), today, "02:00 PM", true));
            shows.add(new Show(savedMovies.get(3), today, "08:00 PM", true));

            // Dune: Part Two
            shows.add(new Show(savedMovies.get(4), today, "03:30 PM", true));
            shows.add(new Show(savedMovies.get(4), today, "07:00 PM", true));

            List<Show> savedShows = showRepository.saveAll(shows);

            // 3. Populate 40 seats for every show
            for (Show show : savedShows) {
                seatService.initializeSeatsForShow(show);
            }
            log.info("Initialized {} shows with 40 seats each.", savedShows.size());

            // 4. Coupons
            couponRepository.save(new Coupon("CINE10", new BigDecimal("10.00"), true));
            couponRepository.save(new Coupon("VIP20", new BigDecimal("20.00"), true));
            couponRepository.save(new Coupon("WELCOME50", new BigDecimal("50.00"), true));

            log.info("Seed initialization completed successfully.");
        }
    }
}

