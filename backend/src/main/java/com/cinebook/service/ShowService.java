package com.cinebook.service;

import com.cinebook.dto.ShowResponseDto;
import com.cinebook.entity.Show;
import com.cinebook.exception.ResourceNotFoundException;
import com.cinebook.repository.SeatRepository;
import com.cinebook.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShowService {

    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;

    public ShowService(ShowRepository showRepository, SeatRepository seatRepository) {
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
    }

    public List<ShowResponseDto> getAllShows() {
        return showRepository.findByActiveTrueOrderByShowDateAscShowTimeAsc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<ShowResponseDto> getShowsByMovieId(Long movieId) {
        return showRepository.findByMovieIdAndActiveTrueOrderByShowDateAscShowTimeAsc(movieId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ShowResponseDto getShowById(Long id) {
        Show show = showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + id));
        return mapToDto(show);
    }

    public Show getShowEntity(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + id));
    }

    public ShowResponseDto mapToDto(Show show) {
        long availableSeats = seatRepository.countByShowIdAndIsBookedFalse(show.getId());
        long totalSeats = 40; // Standard 5x8 auditorium
        return new ShowResponseDto(
                show.getId(),
                show.getMovie().getId(),
                show.getMovie().getTitle(),
                show.getMovie().getPosterUrl(),
                show.getMovie().getRating(),
                show.getMovie().getDuration(),
                show.getShowDate(),
                show.getShowTime(),
                show.getActive(),
                availableSeats,
                totalSeats
        );
    }
}

