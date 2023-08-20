package com.danielszulc.roomreserve.model;

import com.danielszulc.roomreserve.enums.RoomType;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private Integer noOfPerson;
    @Enumerated(EnumType.STRING)
    private RoomType type;
    private double price;
    private boolean isAvailable;
    @OneToMany(mappedBy = "room", fetch = FetchType.EAGER)
    private List<Reservation> reservations;
}

