package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.PersonRequest;
import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.service.HotelService;
import com.danielszulc.roomreserve.service.ReservationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotel")
@Tag(name = "Hotel")
@AllArgsConstructor
public class HotelController {

    private HotelService hotelService;
    private ReservationService reservationService;

    @GetMapping("/allGuests")
    public ResponseEntity<List<PersonDTO>> getAll()
    {
        List<PersonDTO> persons = hotelService.getAllGuests();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/searchGuests")
    public ResponseEntity<List<PersonDTO>> searchAll(@RequestBody PersonRequest searchRequest) {
        List<PersonDTO> persons = hotelService.searchGuests(searchRequest);
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/findGuest")
    public ResponseEntity<PersonDTO> getGuest(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username
    ) {
        PersonDTO personDTO = hotelService.findGuestByIdOrEmailOrUsername(id, email, username);
        return new ResponseEntity<>(personDTO, HttpStatus.OK);
    }

    @PostMapping("/checkin/{reservationId}")
    public ResponseEntity<String> checkIn(@PathVariable Long reservationId) {
        String res = reservationService.checkIn(reservationId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/checkout/{reservationId}")
    public ResponseEntity<String> checkOut(@PathVariable Long reservationId) {
        String res = reservationService.checkOut(reservationId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/confirmReservation/{reservationId}")
    public ResponseEntity<String> confirm(@PathVariable Long reservationId) {
        String res = reservationService.confirmReservation(reservationId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
