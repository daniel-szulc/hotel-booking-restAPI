package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.dto.GuestRequest;
import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.model.Guest;

import java.util.List;

public interface GuestService extends PersonService {
    PersonDTO createGuest(Guest guest);
    List<PersonDTO> searchGuests(GuestRequest searchRequest);
    PersonDTO findGuestByIdOrEmail(Long id, String email);
    String updatePersonalData(GuestRequest guestRequest);
}
