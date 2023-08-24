package com.danielszulc.roomreserve.controller;


import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.service.ReservationService;
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

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void createReservation_ShouldReturnCreatedReservation() throws Exception {
        ReservationDTO reservationDTO = new ReservationDTO();
        ReservationRequest reservationRequest = new ReservationRequest();

        when(reservationService.createReservation(any(ReservationRequest.class))).thenReturn(reservationDTO);

        mockMvc.perform(post("/api/reservation/createReservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reservationDTO)));

        verify(reservationService).createReservation(reservationRequest);
    }

    @Test
    void getReservationById_ShouldReturnReservation() throws Exception {
        ReservationDTO reservationDTO = new ReservationDTO();

        when(reservationService.getReservationById(anyLong())).thenReturn(reservationDTO);

        mockMvc.perform(get("/api/reservation/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reservationDTO)));

        verify(reservationService).getReservationById(1L);
    }

    @Test
    void getReservationsByGuestId_ShouldReturnList() throws Exception {
        List<ReservationDTO> reservations = new ArrayList<>();

        when(reservationService.getReservationsByGuestId(anyLong())).thenReturn(reservations);

        mockMvc.perform(get("/api/reservation/guest/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reservations)));

        verify(reservationService).getReservationsByGuestId(1L);
    }

    // Similarly for other methods
    @Test
    void cancelReservation_ShouldReturnSuccessMessage() throws Exception {
        String successMessage = "Reservation cancelled successfully";

        when(reservationService.cancelReservation(anyLong())).thenReturn(successMessage);

        mockMvc.perform(delete("/api/reservation/{reservationId}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));

        verify(reservationService).cancelReservation(1L);
    }

    @Test
    void getAllReservations_ShouldReturnAllReservations() throws Exception {
        List<ReservationDTO> reservations = new ArrayList<>();

        when(reservationService.getAllReservations()).thenReturn(reservations);

        mockMvc.perform(get("/api/reservation/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(reservations)));

        verify(reservationService).getAllReservations();
    }

}

