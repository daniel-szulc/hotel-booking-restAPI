package com.danielszulc.roomreserve;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.danielszulc.roomreserve.controller.UserController;
import com.danielszulc.roomreserve.dto.UpdatePasswordRequest;
import com.danielszulc.roomreserve.dto.UpdateRequest;
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

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

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
    public void updateName_ShouldReturnSuccessMessage_WhenNameIsUpdated() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setPassword("Test@12345");
        updateRequest.setField("New Name");

        when(userService.updateName(any(UpdateRequest.class))).thenReturn("Name updated successfully!");

        mockMvc.perform(put("/api/user/update/name")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Name updated successfully!"));

        verify(userService).updateName(any(UpdateRequest.class));
    }

    @Test
    public void updatePassword_ShouldReturnSuccessMessage_WhenPasswordIsUpdated() throws Exception {
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
    public void updatePhone_ShouldReturnSuccessMessage_WhenPhoneIsUpdated() throws Exception {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setPassword("Test@12345");
        updateRequest.setField("New Phone");

        when(userService.updatePhone(any(UpdateRequest.class))).thenReturn("Phone updated successfully!");

        mockMvc.perform(put("/api/user/update/phone")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Phone updated successfully!"));

        verify(userService).updatePhone(any(UpdateRequest.class));
    }
}
