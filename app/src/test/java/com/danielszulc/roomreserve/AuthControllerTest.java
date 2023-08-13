package com.danielszulc.roomreserve;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.danielszulc.roomreserve.controller.AuthController;
import com.danielszulc.roomreserve.dto.AuthenticationResponse;
import com.danielszulc.roomreserve.dto.SignIn;
import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.model.User;
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
public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserService userService;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void registerUser_ShouldReturnUser_WhenUserIsSuccessfullyRegistered() throws Exception {
        SignUp signUpDto = new SignUp();
        signUpDto.setUsername("testUser");
        signUpDto.setEmail("test@email.com");
        signUpDto.setPassword("Test@12345");

        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword("encodedPassword");

        when(userService.registerUser(any(SignUp.class))).thenReturn(user);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpDto)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));

        verify(userService).registerUser(any(SignUp.class));
    }

    @Test
    public void login_ShouldReturnAuthenticationResponse_WhenCredentialsAreValid() throws Exception {
        SignIn signInDto = new SignIn();
        signInDto.setUsername("testUsername");
        signInDto.setPassword("testPassword");
        AuthenticationResponse response = new AuthenticationResponse("test@test.com", "token");

        when(userService.authenticateUser(any(SignIn.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signInDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));

        verify(userService).authenticateUser(any(SignIn.class));
    }

}
