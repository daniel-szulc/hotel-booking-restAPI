package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.model.Room;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    List<Room> getAll();
    Room getRoomById(Long id);
    String deleteRoom(Long id);
    List<Room> getAvailableRooms(LocalDate startDate, LocalDate endDate);
    List<Room> getCurrentlyOccupiedRooms();
    List<Room> getCurrentlyAvailableRooms();
    Room createRoom(Room room);
    String updateRoom(Room room);

}
