package com.danielszulc.roomreserve.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.dto.UpdatePasswordRequest;
import com.danielszulc.roomreserve.dto.UserRequest;
import com.danielszulc.roomreserve.service.UserService;
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
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();

    }

    @Test
    void updatePassword_ShouldReturnSuccessMessage_WhenPasswordIsUpdated() throws Exception {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("Test@12345");
        updatePasswordRequest.setNewPassword("New@Password");

        when(userService.updatePassword(any(UpdatePasswordRequest.class))).thenReturn("Password updated successfully!");

        mockMvc.perform(put("/api/user/update/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Password updated successfully!"));

        verify(userService).updatePassword(any(UpdatePasswordRequest.class));
    }

    @Test
    void updatePhone_ShouldReturnSuccessMessage_WhenPhoneIsUpdated() throws Exception {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("Test@12345");
        userRequest.setPhone("New Phone");

        when(userService.updatePersonalData(any(UserRequest.class))).thenReturn("Personal data updated successfully!");

        mockMvc.perform(put("/api/user/update/personal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Personal data updated successfully!"));

        verify(userService).updatePersonalData(any(UserRequest.class));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        List<PersonDTO> personDTOList = new ArrayList<>();
        PersonDTO personDTO = new PersonDTO();
        personDTOList.add(personDTO);

        when(userService.getAll()).thenReturn(personDTOList);

        mockMvc.perform(get("/api/user/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTOList)));

        verify(userService).getAll();
    }

    @Test
    void getUserData_ShouldReturnUserDetails() throws Exception {
        PersonDTO personDTO = new PersonDTO();

        when(userService.getUserData()).thenReturn(personDTO);

        mockMvc.perform(get("/api/user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(personDTO)));

        verify(userService).getUserData();
    }

    @Test
    void deleteUser_ShouldReturnSuccessMessage_WhenUserIsDeleted() throws Exception {
        mockMvc.perform(delete("/api/user/{username}", "testUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("User deleted successfully!"));

        verify(userService).deleteUserByUsername("testUser");
    }
}
