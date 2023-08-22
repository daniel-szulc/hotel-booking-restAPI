package com.danielszulc.roomreserve.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.danielszulc.roomreserve.dto.GuestRequest;
import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.model.Guest;
import com.danielszulc.roomreserve.service.GuestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class GuestControllerTest {

    @InjectMocks
    private GuestController guestController;

    @Mock
    private GuestService guestService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(guestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllGuests_ShouldReturnListOfGuests() throws Exception {
        List<PersonDTO> personDTOList = new ArrayList<>();
        PersonDTO personDTO = new PersonDTO();
        personDTOList.add(personDTO);

        when(guestService.getAll()).thenReturn(personDTOList);

        mockMvc.perform(get("/api/guest/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTOList)));

        verify(guestService).getAll();
    }

    @Test
    void updatePersonalData_ShouldReturnSuccessMessage_WhenDataIsUpdated() throws Exception {
        GuestRequest guestRequest = new GuestRequest(); // Populate this as needed

        when(guestService.updatePersonalData(any(GuestRequest.class))).thenReturn("Personal data updated successfully!");

        mockMvc.perform(put("/api/guest/update/personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Personal data updated successfully!"));

        verify(guestService).updatePersonalData(any(GuestRequest.class));
    }

    @Test
    void createGuest_ShouldReturnCreatedGuest() throws Exception {
        Guest guest = new Guest(); // Populate this as needed
        PersonDTO personDTO = new PersonDTO(); // Populate this as needed

        when(guestService.createGuest(any(Guest.class))).thenReturn(personDTO);

        mockMvc.perform(post("/api/guest/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guest)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTO)));

        verify(guestService).createGuest(any(Guest.class));
    }

    @Test
    void searchGuests_ShouldReturnListOfGuests() throws Exception {
        List<PersonDTO> personDTOList = new ArrayList<>();
        GuestRequest guestRequest = new GuestRequest(); // Populate this as needed

        when(guestService.searchGuests(any(GuestRequest.class))).thenReturn(personDTOList);

        mockMvc.perform(get("/api/guest/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(guestRequest)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTOList)));

        verify(guestService).searchGuests(any(GuestRequest.class));
    }

    @Test
    void findGuestByIdOrEmail_ShouldReturnGuestDetails() throws Exception {
        PersonDTO personDTO = new PersonDTO(); // Populate this as needed

        when(guestService.findGuestByIdOrEmail(anyLong(), anyString())).thenReturn(personDTO);

        mockMvc.perform(get("/api/guest/find")
                        .param("id", "1")
                        .param("email", "test@email.com")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTO)));

        verify(guestService).findGuestByIdOrEmail(anyLong(), anyString());
    }
}
