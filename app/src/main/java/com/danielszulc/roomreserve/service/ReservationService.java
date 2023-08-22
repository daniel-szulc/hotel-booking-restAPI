package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.model.Reservation;

import java.util.List;

public interface ReservationService {
    Reservation createReservation(Reservation reservation);
    String cancelReservation(Long reservationId);
    String updateReservation(Reservation reservation);
    String checkIn(Long reservationId);
    String checkOut(Long reservationId);
    String confirmReservation(Long reservationId);
    List<Reservation> getAllReservations();
    List<Reservation> getMyReservations();
    List<Reservation> getReservationsByGuestId(Long guestId);
    Reservation getReservationById(Long reservationId);

}
