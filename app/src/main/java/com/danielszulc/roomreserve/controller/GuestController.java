package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.model.Guest;
import com.danielszulc.roomreserve.service.GuestService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
@Tag(name = "Guest")
@AllArgsConstructor
public class GuestController {

    private GuestService guestService;

    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> getAll()
    {
        List<PersonDTO> persons = guestService.getAll();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @PutMapping("/update/personal")
    public ResponseEntity<String> updatePersonalData(@RequestBody GuestRequest guestRequest) {
        String res = guestService.updatePersonalData(guestRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<PersonDTO> createGuest(@RequestBody Guest guest) {
        PersonDTO res = guestService.createGuest(guest);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDTO>> searchUsers(@RequestBody GuestRequest guestRequest) {
        List<PersonDTO> guests = guestService.searchGuests(guestRequest);
        return new ResponseEntity<>(guests, HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<PersonDTO> getUser(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "email", required = false) String email
    ) {
        PersonDTO guest = guestService.findGuestByIdOrEmail(id, email);
        return new ResponseEntity<>(guest, HttpStatus.OK);
    }
}
