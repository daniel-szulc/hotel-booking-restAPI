package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.RoomDTO;
import com.danielszulc.roomreserve.dto.RoomRequest;
import com.danielszulc.roomreserve.model.Room;
import com.danielszulc.roomreserve.service.RoomService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@Tag(name = "Room")
@AllArgsConstructor
public class RoomController {

    private RoomService roomService;

    @GetMapping("/available")
    public List<RoomDTO> getAvailableRooms(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return roomService.getAvailableRooms(startDate, endDate);
    }

    @GetMapping("/{id}")
    public RoomDTO getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @GetMapping("/room/{roomNumber}")
    public RoomDTO getRoomByNumber(@PathVariable String roomNumber) {
        return roomService.getRoomByNumber(roomNumber);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RoomDTO>> getAll() {
        List<RoomDTO> rooms = roomService.getAll();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/occupied")
    public ResponseEntity<List<RoomDTO>> getOccupied() {
        List<RoomDTO> rooms = roomService.getCurrentlyOccupiedRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/availableNow")
    public ResponseEntity<List<RoomDTO>> getAvailable() {
        List<RoomDTO> rooms = roomService.getCurrentlyAvailableRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody RoomRequest roomRequest) {
        Room res = roomService.createRoom(roomRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRoom(@Valid @RequestBody RoomDTO room) {
        String res = roomService.updateRoom(room);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String> deleteRoom(@PathVariable Long id) {
        String res = roomService.deleteRoom(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}

