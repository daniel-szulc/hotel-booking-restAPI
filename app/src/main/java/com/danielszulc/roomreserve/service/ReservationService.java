package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.dto.ReservationDTO;
import com.danielszulc.roomreserve.dto.ReservationRequest;

import java.util.List;

public interface ReservationService {
    ReservationDTO createReservation(ReservationRequest reservation);
    String cancelReservation(Long reservationId);
    String updateReservation(ReservationRequest reservation);
    String checkIn(Long reservationId);
    String checkOut(Long reservationId);
    String confirmReservation(Long reservationId);
    List<ReservationDTO> getAllReservations();
    List<ReservationDTO> getMyReservations();
    List<ReservationDTO> getReservationsByGuestId(Long guestId);
    ReservationDTO getReservationById(Long reservationId);

}
