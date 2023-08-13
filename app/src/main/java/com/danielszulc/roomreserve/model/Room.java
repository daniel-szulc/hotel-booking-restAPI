package com.danielszulc.roomreserve.model;

import com.danielszulc.roomreserve.enums.RoomType;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private double price;
    private boolean isAvailable;
}

