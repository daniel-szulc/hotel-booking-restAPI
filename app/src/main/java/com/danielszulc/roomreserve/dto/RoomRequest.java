package com.danielszulc.roomreserve.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {
    private String number;
    private Integer noOfPerson;
    @Enumerated(EnumType.STRING)
    private String type;
    @Min(value = 0, message = "Price must be greater than 0")
    private double price;
}
