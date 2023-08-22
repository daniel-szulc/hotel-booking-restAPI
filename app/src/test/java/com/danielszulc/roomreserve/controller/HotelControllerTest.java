package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.service.GuestService;
import com.danielszulc.roomreserve.service.UserService;
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
    private GuestService guestService;

    @Mock
    private UserService userService;

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
        List<PersonDTO> userList = new ArrayList<>();
        List<PersonDTO> allPersons = new ArrayList<>();

        allPersons.addAll(guestList);
        allPersons.addAll(userList);

        when(guestService.getAll()).thenReturn(guestList);
        when(userService.getAll()).thenReturn(userList);

        mockMvc.perform(get("/api/hotel/allGuests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(allPersons)));

        verify(guestService).getAll();
        verify(userService).getAll();
    }

    @Test
    void searchAllGuests_ShouldReturnSearchedListOfGuestsAndUsers() throws Exception {
        PersonRequest searchRequest = new GuestRequest();
        List<PersonDTO> guestList = new ArrayList<>();
        List<PersonDTO> userList = new ArrayList<>();
        List<PersonDTO> allPersons = new ArrayList<>();

        allPersons.addAll(guestList);
        allPersons.addAll(userList);

        when(guestService.searchGuests(any(GuestRequest.class))).thenReturn(guestList);
        when(userService.searchUsers(any(UserSearchRequest.class))).thenReturn(userList);

        mockMvc.perform(get("/api/hotel/searchGuests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(searchRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(allPersons)));

        verify(guestService).searchGuests(any(GuestRequest.class));
        verify(userService).searchUsers(any(UserSearchRequest.class));
    }


    @Test
    void findGuest_ShouldReturnGuestDetails() throws Exception {
        PersonDTO personDTO = new PersonDTO();  // Populate this as needed

        when(userService.findUserByIdOrEmailOrUsername(anyLong(), anyString(), anyString())).thenReturn(personDTO);

        mockMvc.perform(get("/api/hotel/findGuest")
                        .param("id", "1")
                        .param("email", "test@email.com")
                        .param("username", "username")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTO)));

        verify(userService).findUserByIdOrEmailOrUsername(anyLong(), anyString(), anyString());
    }
}
