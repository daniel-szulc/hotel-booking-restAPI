package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.model.Reservation;
import com.danielszulc.roomreserve.repository.ReservationRepository;
import com.danielszulc.roomreserve.service.ReservationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    public Reservation bookRoom(Reservation reservation) {

        return reservationRepository.save(reservation);
    }

}
