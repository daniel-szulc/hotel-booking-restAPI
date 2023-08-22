package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.model.Reservation;
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

    @PostMapping("/book}")
    public ResponseEntity<Reservation> createReservation(@Valid @RequestBody Reservation reservation) {
        Reservation res = reservationService.createReservation(reservation);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reservation> getReservationById(@PathVariable Long id) {
        Reservation res = reservationService.getReservationById(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/guest/{id}")
    public ResponseEntity<List<Reservation>>  getReservationsByGuestId(@PathVariable Long id) {
        List<Reservation> res = reservationService.getReservationsByGuestId(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> cancelReservation(@PathVariable Long reservationId) {
        String res = reservationService.cancelReservation(reservationId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> res = reservationService.getAllReservations();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Reservation>> getMyReservations() {
        List<Reservation> res =  reservationService.getMyReservations();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateReservation(@Valid @RequestBody Reservation reservation) {
        String res = reservationService.updateReservation(reservation);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
