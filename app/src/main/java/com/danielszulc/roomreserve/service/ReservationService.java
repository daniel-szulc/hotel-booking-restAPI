package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.model.Reservation;
import com.danielszulc.roomreserve.repository.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation bookRoom(Reservation reservation) {

        return reservationRepository.save(reservation);
    }

}
