package com.danielszulc.roomreserve.mapper;

import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.dto.ReservationDTO;
import com.danielszulc.roomreserve.dto.RoomDTO;
import com.danielszulc.roomreserve.model.Reservation;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ReservationMapper {
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoomMapper roomMapper;

    public ReservationDTO convertToDTO(Reservation reservation) {
        ReservationDTO reservationDTO = new ReservationDTO();
        BeanUtils.copyProperties(reservation, reservationDTO);
        if (reservation.getGuest() != null) {
            PersonDTO personDTO = userMapper.convertToDTO(reservation.getGuest());
            reservationDTO.setGuest(personDTO);
        }
        if (reservation.getRoom() != null) {
            RoomDTO roomDTO = roomMapper.convertToDTO(reservation.getRoom());
            reservationDTO.setRoom(roomDTO);
        }
        return reservationDTO;
    }
}

