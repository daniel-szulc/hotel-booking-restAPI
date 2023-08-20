package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.dto.GuestRequest;
import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.exception.UserNotFoundException;
import com.danielszulc.roomreserve.model.Guest;
import com.danielszulc.roomreserve.repository.GuestRepository;
import com.danielszulc.roomreserve.repository.PersonRepository;
import com.danielszulc.roomreserve.service.GuestService;
import com.danielszulc.roomreserve.utils.SpecificationUtils;
import jakarta.persistence.criteria.Predicate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class GuestServiceImpl extends PersonServiceImpl<Guest> implements GuestService{

    private final GuestRepository guestRepository;

    @Override
    public PersonRepository<Guest> getRepository() {
        return guestRepository;
    }

    @Override
    public PersonDTO createGuest(Guest guest) {
        Guest savedGuest = guestRepository.save(guest);
        return userMapper.convertToDTO(savedGuest);
    }

    @Override
    public List<PersonDTO> searchGuests(GuestRequest searchRequest) {

        Specification<Guest> spec = (root, query, cb) -> {
            List<Predicate> predicates = SpecificationUtils.createPersonPredicates(root, cb, searchRequest.getEmail(), searchRequest.getFirstName(), searchRequest.getLastName(), searchRequest.getPhone(), searchRequest.getAddress());
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Guest> guests = guestRepository.findAll(spec);
        return guests.stream().map(this.userMapper::convertToDTO).toList();
    }

    public PersonDTO findGuestByIdOrEmail(Long id, String email) {
        Guest guest = null;

        if (id != null) {
            guest = guestRepository.findById(id).orElse(null);
        }

        if (guest == null && email != null) {
            guest = guestRepository.findByEmail(email).orElse(null);
        }

        if (guest == null) {
            throw new UserNotFoundException("Guest not found");
        }

        return userMapper.convertToDTO(guest);
    }

    @Override
    public String updatePersonalData(GuestRequest guestRequest) {

       if(guestRequest.getId() == null) {
           Guest guest = guestRepository.findByEmail(guestRequest.getEmail()).orElseThrow(
                   () -> new UserNotFoundException("Guest not found"));

          guestRequest.setId(guest.getId());
       }

        return super.updatePersonalData(guestRequest);
    }

}
