package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
}
