package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.TestDataProvider;
import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.model.Room;
import com.danielszulc.roomreserve.service.RoomService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomService roomService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(roomController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAvailableRooms_ShouldReturnListOfAvailableRooms() throws Exception {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        List<RoomDTO> availableRooms = new ArrayList<>();

        when(roomService.getAvailableRooms(any(LocalDate.class), any(LocalDate.class))).thenReturn(availableRooms);

        mockMvc.perform(get("/api/rooms/available")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(availableRooms)));

        verify(roomService).getAvailableRooms(startDate, endDate);
    }

    @Test
    void getRoomById_ShouldReturnRoomDetails() throws Exception {
        RoomDTO roomDTO = new RoomDTO();

        when(roomService.getRoomById(anyLong())).thenReturn(roomDTO);

        mockMvc.perform(get("/api/rooms/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roomDTO)));

        verify(roomService).getRoomById(1L);
    }

    @Test
    void getRoomByNumber_ShouldReturnRoomDetails() throws Exception {
        RoomDTO roomDTO = new RoomDTO();
        String roomNumber = "101";

        when(roomService.getRoomByNumber(anyString())).thenReturn(roomDTO);

        mockMvc.perform(get("/api/rooms/room/{roomNumber}", roomNumber)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(roomDTO)));

        verify(roomService).getRoomByNumber(roomNumber);
    }

    @Test
    void createRoom_ShouldReturnCreatedRoom() throws Exception {
        Room room = new Room();
        RoomRequest roomRequest = new RoomRequest();

        when(roomService.createRoom(any(RoomRequest.class))).thenReturn(room);

        mockMvc.perform(post("/api/rooms/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roomRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(room)));

        verify(roomService).createRoom(roomRequest);
    }

    @Test
    void deleteRoom_ShouldReturnSuccessMessage() throws Exception {
        String successMessage = "Room deleted successfully";

        when(roomService.deleteRoom(anyLong())).thenReturn(successMessage);

        mockMvc.perform(delete("/api/rooms/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));

        verify(roomService).deleteRoom(1L);
    }
}

