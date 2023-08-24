package com.danielszulc.roomreserve.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequest {
    private Long id;
    private Long guestId;
    private Long roomId;
    private String roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer noOfPerson;
    private String payment;
}
