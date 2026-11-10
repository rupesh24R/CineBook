package com.cinebook.repository;

import com.cinebook.entity.Seat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByShowIdOrderBySeatCodeAsc(Long showId);

    Optional<Seat> findByShowIdAndSeatCode(Long showId, String seatCode);

    List<Seat> findByShowIdAndSeatCodeIn(Long showId, List<String> seatCodes);

    /**
     * Pessimistic write lock to prevent race conditions during concurrent bookings.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.show.id = :showId AND s.seatCode IN :seatCodes")
    List<Seat> findByShowIdAndSeatCodeInWithLock(@Param("showId") Long showId,
                                                @Param("seatCodes") List<String> seatCodes);

    long countByShowIdAndIsBookedFalse(Long showId);
}

