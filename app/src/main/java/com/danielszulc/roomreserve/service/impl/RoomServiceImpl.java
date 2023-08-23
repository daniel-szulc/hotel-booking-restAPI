package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.dto.RoomDTO;
import com.danielszulc.roomreserve.dto.RoomRequest;
import com.danielszulc.roomreserve.exception.AccessDeniedException;
import com.danielszulc.roomreserve.exception.RoomNotFoundException;
import com.danielszulc.roomreserve.mapper.RoomMapper;
import com.danielszulc.roomreserve.model.Room;
import com.danielszulc.roomreserve.repository.RoomRepository;
import com.danielszulc.roomreserve.service.RoomService;
import com.danielszulc.roomreserve.service.UserValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RoomServiceImpl implements RoomService {

    private RoomRepository roomRepository;

    @Autowired
    private UserValidator userValidator;

    @Autowired
    private RoomMapper roomMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getAll() {
        return roomRepository.findAll().stream().map(roomMapper::convertToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {

        Room room = roomRepository.findById(id).orElseThrow(() -> new RoomNotFoundException("Room not found"));

        try {
            userValidator.validateAdminOrHotelPermissions();
        } catch (AccessDeniedException e) {
            room.setReservations(null);
        }
        return room;
    }

    @Override
    public Room getRoomByNumber(String number) {
        Room room = roomRepository.findByNumber(number).orElseThrow(() -> new RoomNotFoundException("Room not found"));

        try {
            userValidator.validateAdminOrHotelPermissions();
        } catch (AccessDeniedException e) {
            room.setReservations(null);
        }
        return room;
    }

    @Override
    @Transactional
    public String deleteRoom(Long id) {
        Room room = getRoomById(id);
        roomRepository.delete(room);
        return "Room deleted successfully";
    }

    @Override
    public List<RoomDTO> getAvailableRooms(LocalDate startDate, LocalDate endDate) {

        List<Room> availableRooms = roomRepository.findAvailableRooms(startDate, endDate);

        return availableRooms.stream().map(roomMapper::convertToDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> getCurrentlyAvailableRooms() {
        LocalDateTime now = LocalDateTime.now();
        return getAvailableRooms(now.toLocalDate(), now.toLocalDate());
    }


    @Override
    @Transactional(readOnly = true)
    public List<Room> getCurrentlyOccupiedRooms() {
        return roomRepository.findCurrentlyOccupiedRooms();
    }



    @Override
    @Transactional
    public Room createRoom(RoomRequest roomRequest) {
        Room room =  roomMapper.convertToRoomEntity(roomRequest);
        return roomRepository.save(room);
    }

    @Override
    @Transactional
    public String updateRoom(RoomDTO room) {
        Long roomId = room.getId();
        boolean updateOccurred = false;

        if (room.getNumber() != null && !room.getNumber().isEmpty()) {
            roomRepository.updateNumber(roomId, room.getNumber());
            updateOccurred = true;
        }

        if (room.getNoOfPerson() != null) {
            roomRepository.updateNoOfPerson(roomId, room.getNoOfPerson());
            updateOccurred = true;
        }

        if (room.getType() != null) {
            roomRepository.updateType(roomId, room.getType());
            updateOccurred = true;
        }

        if (room.getPrice() > 0) {
            roomRepository.updatePrice(roomId, room.getPrice());
            updateOccurred = true;
        }

        if (updateOccurred) {
            log.info("Room update successful");
            return "Room updated successfully";
        } else {
            throw new IllegalArgumentException("No valid field specified for update.");
        }
    }

}
