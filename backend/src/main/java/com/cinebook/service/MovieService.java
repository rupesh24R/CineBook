package com.cinebook.service;

import com.cinebook.dto.MovieResponseDto;
import com.cinebook.entity.Movie;
import com.cinebook.exception.ResourceNotFoundException;
import com.cinebook.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieResponseDto> getAllMovies() {
        return movieRepository.findAllByOrderByTitleAsc()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public MovieResponseDto getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + id));
        return mapToDto(movie);
    }

    public Movie getMovieEntity(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + id));
    }

    public MovieResponseDto mapToDto(Movie movie) {
        return new MovieResponseDto(
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getLanguage(),
                movie.getRating(),
                movie.getPosterUrl(),
                movie.getReleaseDate(),
                movie.getStatus()
        );
    }
}

