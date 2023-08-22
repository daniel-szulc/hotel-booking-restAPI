package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.GuestRequest;
import com.danielszulc.roomreserve.dto.PersonRequest;
import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.dto.UserSearchRequest;
import com.danielszulc.roomreserve.service.GuestService;
import com.danielszulc.roomreserve.service.ReservationService;
import com.danielszulc.roomreserve.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/hotel")
@Tag(name = "Hotel")
@AllArgsConstructor
public class HotelController {

    private GuestService guestService;
    private UserService userService;
    private ReservationService reservationService;

    @GetMapping("/allGuests")
    public ResponseEntity<List<PersonDTO>> getAll()
    {
        List<PersonDTO> persons = new ArrayList<>();
        persons.addAll(userService.getAll());
        persons.addAll(guestService.getAll());
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/searchGuests")
    public ResponseEntity<List<PersonDTO>> searchAll(@RequestBody PersonRequest searchRequest) {
        List<PersonDTO> persons = new ArrayList<>();

        UserSearchRequest userSearchRequest = new UserSearchRequest();
        BeanUtils.copyProperties(searchRequest, userSearchRequest);

        GuestRequest guestRequest = new GuestRequest();
        BeanUtils.copyProperties(searchRequest, guestRequest);

        persons.addAll(userService.searchUsers(userSearchRequest));

        persons.addAll(guestService.searchGuests(guestRequest));
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping("/findGuest")
    public ResponseEntity<PersonDTO> getGuest(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username
    ) {
        PersonDTO personDTO = userService.findUserByIdOrEmailOrUsername(id, email, username);
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
