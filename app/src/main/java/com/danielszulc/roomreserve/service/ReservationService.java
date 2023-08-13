package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.model.Reservation;

public interface ReservationService {
    Reservation bookRoom(Reservation reservation);
}
