package com.danielszulc.roomreserve.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequest {
    @NotBlank
    private String number;
    private Integer noOfPerson;
    @Enumerated(EnumType.STRING) @NotBlank
    private String type;
    @Min(value = 0, message = "Price must be greater than 0")
    private double price;
}
