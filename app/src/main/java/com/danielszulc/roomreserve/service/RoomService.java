package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.dto.RoomDTO;
import com.danielszulc.roomreserve.dto.RoomRequest;
import com.danielszulc.roomreserve.model.Room;

import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    List<RoomDTO> getAll();
    Room getRoomById(Long id);
    Room getRoomByNumber(String number);
    String deleteRoom(Long id);
    List<RoomDTO> getAvailableRooms(LocalDate startDate, LocalDate endDate);
    List<Room> getCurrentlyOccupiedRooms();
    List<RoomDTO> getCurrentlyAvailableRooms();
    Room createRoom(RoomRequest roomRequest);
    String updateRoom(RoomDTO room);

}
