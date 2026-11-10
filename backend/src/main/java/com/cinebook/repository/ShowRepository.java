package com.cinebook.repository;

import com.cinebook.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findByMovieIdAndActiveTrueOrderByShowDateAscShowTimeAsc(Long movieId);
    List<Show> findByActiveTrueOrderByShowDateAscShowTimeAsc();
    List<Show> findByShowDateAndActiveTrue(LocalDate showDate);
}

