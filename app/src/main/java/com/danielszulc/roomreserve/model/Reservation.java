package com.danielszulc.roomreserve.model;

import com.danielszulc.roomreserve.enums.PaymentType;
import com.danielszulc.roomreserve.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Person guest;
    @ManyToOne
    private Room room;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Integer noOfPerson;
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentType payment;
}
