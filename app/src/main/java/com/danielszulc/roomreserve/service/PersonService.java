package com.danielszulc.roomreserve.service;


import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.dto.PersonRequest;

import java.util.List;

public interface PersonService {
    String updatePersonalData(PersonRequest personRequest);

    List<PersonDTO> getAll();
}
