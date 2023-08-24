package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.dto.PersonRequest;

import java.util.List;


public interface HotelService {
    PersonDTO findGuestById(Long id);
    PersonDTO findGuestByIdOrEmailOrUsername(Long id, String email, String username);
    List<PersonDTO> searchGuests(PersonRequest searchRequest);
    List<PersonDTO> getAllGuests();
}
