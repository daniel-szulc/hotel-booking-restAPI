package com.danielszulc.roomreserve.controller;

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
    public List<Room> getAvailableRooms(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return roomService.getAvailableRooms(startDate, endDate);
    }

    @GetMapping("/{id}")
    public Room getRoomById(@PathVariable Long id) {
        return roomService.getRoomById(id);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Room>> getAll() {
        List<Room> rooms = roomService.getAll();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/occupied")
    public ResponseEntity<List<Room>> getOccupied() {
        List<Room> rooms = roomService.getCurrentlyOccupiedRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailable() {
        List<Room> rooms = roomService.getCurrentlyAvailableRooms();
        return new ResponseEntity<>(rooms, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Room> createRoom(@Valid @RequestBody Room room) {
        Room res = roomService.createRoom(room);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<String> updateRoom(@Valid @RequestBody Room room) {
        String res = roomService.updateRoom(room);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public  ResponseEntity<String>  deleteRoom(@PathVariable Long id) {
        String res = roomService.deleteRoom(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}

