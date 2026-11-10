package com.cinebook.service;

import com.cinebook.dto.SeatResponseDto;
import com.cinebook.entity.Seat;
import com.cinebook.entity.Show;
import com.cinebook.exception.ResourceNotFoundException;
import com.cinebook.model.SeatType;
import com.cinebook.repository.SeatRepository;
import com.cinebook.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {

    public static final int NUM_ROWS = 5;
    public static final int NUM_COLS = 8;

    private final SeatRepository seatRepository;
    private final ShowRepository showRepository;

    public SeatService(SeatRepository seatRepository, ShowRepository showRepository) {
        this.seatRepository = seatRepository;
        this.showRepository = showRepository;
    }

    @Transactional
    public List<SeatResponseDto> getSeatsByShowId(Long showId) {
        Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + showId));

        List<Seat> seats = seatRepository.findByShowIdOrderBySeatCodeAsc(showId);
        if (seats.isEmpty()) {
            // Lazy-initialize the 40 seats if not pre-populated
            seats = initializeSeatsForShow(show);
        }

        return seats.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Transactional
    public List<Seat> initializeSeatsForShow(Show show) {
        List<Seat> newSeats = new ArrayList<>();
        for (int r = 0; r < NUM_ROWS; r++) {
            char rowChar = (char) ('A' + r);
            SeatType type = SeatType.fromRow(rowChar);

            for (int c = 1; c <= NUM_COLS; c++) {
                String seatCode = "" + rowChar + c;
                newSeats.add(new Seat(show, seatCode, type, false));
            }
        }
        return seatRepository.saveAll(newSeats);
    }

    public SeatResponseDto mapToDto(Seat seat) {
        return new SeatResponseDto(
                seat.getId(),
                seat.getShow().getId(),
                seat.getSeatCode(),
                seat.getSeatType().name(),
                seat.getSeatType().getPrice(),
                seat.getIsBooked()
        );
    }
}

