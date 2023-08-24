package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.service.HotelService;
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
class HotelControllerTest {

    @InjectMocks
    private HotelController hotelController;

    @Mock
    private HotelService hotelService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllGuests_ShouldReturnListOfAllGuestsAndUsers() throws Exception {
        List<PersonDTO> guestList = new ArrayList<>();

        when(hotelService.getAllGuests()).thenReturn(guestList);

        mockMvc.perform(get("/api/hotel/allGuests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(guestList)));

        verify(hotelService).getAllGuests();
    }

    @Test
    void searchAllGuests_ShouldReturnSearchedListOfGuestsAndUsers() throws Exception {
        PersonRequest searchRequest = new PersonRequest();
        List<PersonDTO> guestList = new ArrayList<>();

        when(hotelService.searchGuests(any(PersonRequest.class))).thenReturn(guestList);

        mockMvc.perform(get("/api/hotel/searchGuests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(guestList)));

        verify(hotelService).searchGuests(any(PersonRequest.class));
    }


    @Test
    void findGuest_ShouldReturnGuestDetails() throws Exception {
        PersonDTO personDTO = new PersonDTO();  // Populate this as needed

        when(hotelService.findGuestByIdOrEmailOrUsername(anyLong(), anyString(), anyString())).thenReturn(personDTO);

        mockMvc.perform(get("/api/hotel/findGuest")
                        .param("id", "1")
                        .param("email", "test@email.com")
                        .param("username", "username")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTO)));

        verify(hotelService).findGuestByIdOrEmailOrUsername(anyLong(), anyString(), anyString());
    }
}
