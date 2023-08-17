package com.danielszulc.roomreserve;

import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.exception.EmailTakenException;
import com.danielszulc.roomreserve.exception.InvalidLoginException;
import com.danielszulc.roomreserve.exception.InvalidPasswordException;
import com.danielszulc.roomreserve.exception.UsernameTakenException;
import com.danielszulc.roomreserve.mapper.UserMapper;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.AuthenticationService;
import com.danielszulc.roomreserve.service.UserService;
import com.danielszulc.roomreserve.service.UserValidator;
import com.danielszulc.roomreserve.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DataJpaTest
public class UserServiceTest{

    private final String ENCODED_PASSWORD = "encodedPassword";

    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtUtil;
    @Mock
    private UserValidator userValidator;
    @Mock
    private UserMapper userMapper;
    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, authenticationManager, passwordEncoder, jwtUtil, userValidator, userMapper, authenticationService);

        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@email.com");
        user.setPassword(ENCODED_PASSWORD);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.emptyList()
        );
        securityContext.setAuthentication(authentication);

        when(passwordEncoder.encode(anyString())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);

    }

    @Test
    public void registerUser_ShouldReturnSavedUser_WhenProvidedValidInput() {
        SignUp signUpDto = new SignUp();
        signUpDto.setUsername("testUser");
        signUpDto.setEmail("test@email.com");
        signUpDto.setPassword("Test@12345");
        signUpDto.setFirstName("Joe");
        signUpDto.setLastName("Doe");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        UserDTO result = userService.registerUser(signUpDto);

        assertNotNull(result);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    public void registerUser_ShouldThrowUsernameTakenException_WhenUsernameExists() {
        SignUp signUpDto = new SignUp();
        signUpDto.setUsername("testUser");
        signUpDto.setEmail("test@email.com");
        signUpDto.setPassword("Test@12345");
        signUpDto.setFirstName("Joe");
        signUpDto.setLastName("Doe");

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> userService.registerUser(signUpDto));
    }

    @Test
    public void registerUser_ShouldThrowEmailTakenException_WhenEmailExists() {
        SignUp signUpDto = new SignUp();
        signUpDto.setUsername("testUser");
        signUpDto.setEmail("test@email.com");
        signUpDto.setPassword("Test@12345");
        signUpDto.setFirstName("Joe");
        signUpDto.setLastName("Doe");

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(EmailTakenException.class, () -> userService.registerUser(signUpDto));
    }

    @Test
    public void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() {
        SignIn signInDto = new SignIn();
        signInDto.setUsername("testUsername");
        signInDto.setPassword("testPassword");
        User user = new User();
        user.setEmail("test@test.com");
        Authentication auth = Mockito.mock(Authentication.class);

        Mockito.when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        Mockito.when(auth.getPrincipal()).thenReturn(user);
        Mockito.when(jwtUtil.generateAccessToken(user)).thenReturn("some_token");

        AuthenticationResponse response = userService.authenticateUser(signInDto);

        assertNotNull(response);
        assertEquals("test@test.com", response.getEmail());
        assertEquals("some_token", response.getToken());
    }

    @Test
    public void authenticateUser_ShouldThrowInvalidLoginException_WhenCredentialsAreInvalid() {
        SignIn signInDto = new SignIn();
        signInDto.setUsername("testUsername");
        signInDto.setPassword("testPassword");

        Mockito.when(authenticationManager.authenticate(
                        any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password!"));


        assertThrows(InvalidLoginException.class, () -> userService.authenticateUser(signInDto));
    }

    @Test
    public void updateLastName_ShouldUpdateName_WhenPasswordAndFieldAreValid() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("Test@12345");
        userRequest.setLastName("New Last Name");

        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword(passwordEncoder.encode("Test@12345"));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String result = userService.updatePersonalData(userRequest);

        assertEquals("Personal data updated successfully!", result);
        verify(userRepository).updateLastName(eq(currentUser.getUsername()), eq(userRequest.getLastName()));
    }

    @Test
    public void updateLastName_ShouldThrowInvalidPasswordException_WhenPasswordIsInvalid() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("InvalidPassword");
        userRequest.setLastName("New Last Name");

        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword(passwordEncoder.encode("Test@12345"));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.updatePersonalData(userRequest));
        verify(userRepository, never()).updateLastName(anyString(), anyString());
    }

    @Test
    public void updatePassword_ShouldThrowInvalidPasswordException_WhenCurrentPasswordIsInvalid() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("InvalidPassword");
        updatePasswordRequest.setNewPassword("New@Password");

        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword(passwordEncoder.encode("Test@12345"));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.updatePassword(updatePasswordRequest));
        verify(userRepository, never()).updateUserPassword(anyString(), anyString());
    }

    @Test
    public void updatePhone_ShouldUpdatePhone_WhenPasswordAndFieldAreValid() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("Test@12345");
        userRequest.setPhone("New Phone");

        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword(passwordEncoder.encode("Test@12345"));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String result = userService.updatePersonalData(userRequest);

        assertEquals("Personal data updated successfully!", result);
        verify(userRepository).updateUserPhone(eq(currentUser.getUsername()), eq(userRequest.getPhone()));
    }

    @Test
    public void updatePhone_ShouldThrowInvalidPasswordException_WhenPasswordIsInvalid() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("Invalid Password");
        userRequest.setPhone("New Phone");

        User currentUser = new User();
        currentUser.setUsername("testUser");
        currentUser.setPassword(passwordEncoder.encode("Test@12345"));

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userService.updatePersonalData(userRequest));
        verify(userRepository, never()).updateUserPhone(anyString(), anyString());
    }

}
