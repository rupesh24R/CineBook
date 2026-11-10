package com.cinebook.repository;

import com.cinebook.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByBookingId(String bookingId);
    List<Booking> findAllByOrderByCreatedAtDesc();
    boolean existsByBookingId(String bookingId);
}

