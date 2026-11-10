package com.cinebook.controller;

import com.cinebook.dto.SeatResponseDto;
import com.cinebook.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<List<SeatResponseDto>> getSeatsByShowId(@PathVariable Long showId) {
        return ResponseEntity.ok(seatService.getSeatsByShowId(showId));
    }
}

