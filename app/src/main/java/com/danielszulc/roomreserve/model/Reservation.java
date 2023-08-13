package com.danielszulc.roomreserve.model;

import com.danielszulc.roomreserve.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Room room;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer noOfPerson;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Enumerated(EnumType.STRING)
    private PaymentType payment;
}
