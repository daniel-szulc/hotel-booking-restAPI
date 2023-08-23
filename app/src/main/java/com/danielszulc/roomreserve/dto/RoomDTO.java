package com.danielszulc.roomreserve.dto;

import com.danielszulc.roomreserve.enums.RoomType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomDTO {
    private Long id;
    private String number;
    private Integer noOfPerson;
    private RoomType type;
    private double price;
}
