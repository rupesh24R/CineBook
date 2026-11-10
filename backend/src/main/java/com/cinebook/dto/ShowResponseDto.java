package com.cinebook.dto;

import java.time.LocalDate;

public class ShowResponseDto {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private String movieRating;
    private Integer movieDuration;
    private LocalDate showDate;
    private String showTime;
    private Boolean active;
    private long availableSeats;
    private long totalSeats;

    public ShowResponseDto() {
    }

    public ShowResponseDto(Long id, Long movieId, String movieTitle, String moviePosterUrl,
                           String movieRating, Integer movieDuration, LocalDate showDate,
                           String showTime, Boolean active, long availableSeats, long totalSeats) {
        this.id = id;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.moviePosterUrl = moviePosterUrl;
        this.movieRating = movieRating;
        this.movieDuration = movieDuration;
        this.showDate = showDate;
        this.showTime = showTime;
        this.active = active;
        this.availableSeats = availableSeats;
        this.totalSeats = totalSeats;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public Integer getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(Integer movieDuration) {
        this.movieDuration = movieDuration;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public String getShowTime() {
        return showTime;
    }

    public void setShowTime(String showTime) {
        this.showTime = showTime;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public long getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(long availableSeats) {
        this.availableSeats = availableSeats;
    }

    public long getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(long totalSeats) {
        this.totalSeats = totalSeats;
    }
}

