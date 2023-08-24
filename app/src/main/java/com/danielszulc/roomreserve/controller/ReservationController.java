package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.ReservationDTO;
import com.danielszulc.roomreserve.dto.ReservationRequest;
import com.danielszulc.roomreserve.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservation")
@Tag(name = "Reservation")
@AllArgsConstructor
public class ReservationController {

    private ReservationService reservationService;

    @PostMapping("/createReservation")
    public ResponseEntity<ReservationDTO> createReservation(@Valid @RequestBody ReservationRequest reservation) {
        ReservationDTO res = reservationService.createReservation(reservation);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getReservationById(@PathVariable Long id) {
        ReservationDTO res = reservationService.getReservationById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/guest/{id}")
    public ResponseEntity<List<ReservationDTO>>  getReservationsByGuestId(@PathVariable Long id) {
        List<ReservationDTO> res = reservationService.getReservationsByGuestId(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        String res = reservationService.cancelReservation(reservationId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ReservationDTO>> getAllReservations() {
        List<ReservationDTO> res = reservationService.getAllReservations();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReservationDTO>> getMyReservations() {
        List<ReservationDTO> res =  reservationService.getMyReservations();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateReservation(@Valid @RequestBody ReservationRequest reservation) {
        String res = reservationService.updateReservation(reservation);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
