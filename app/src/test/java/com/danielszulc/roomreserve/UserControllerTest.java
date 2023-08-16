package com.danielszulc.roomreserve;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.danielszulc.roomreserve.controller.UserController;
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
}
