package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.TestDataProvider;
import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.EmailTakenException;
import com.danielszulc.roomreserve.exception.InvalidPasswordException;
import com.danielszulc.roomreserve.exception.UsernameTakenException;
import com.danielszulc.roomreserve.mapper.UserMapper;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DataJpaTest
@ActiveProfiles("test")
class UserServiceTest{

    private final TestDataProvider testDataProvider = new TestDataProvider();
    @InjectMocks
    private UserServiceImpl userService;
    @MockBean
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtUtil;

    @Spy
    private UserValidator userValidator;
    @Mock
    private UserMapper<User> userMapper;
    @Mock
    private AuthenticationService authenticationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder, jwtUtil, userValidator, authenticationService);
        userService.setUserMapper(userMapper);
        SecurityContext securityContext = SecurityContextHolder.getContext();
        User user = testDataProvider.getSampleUser();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user,
                null,
                Collections.emptyList()
        );
        securityContext.setAuthentication(authentication);
        when(authenticationService.authenticateWithCredentials(testDataProvider.getSampleSignIn().getUsername(), testDataProvider.getSampleSignIn().getPassword())).thenReturn(authentication);
        when(passwordEncoder.encode(anyString())).thenReturn(testDataProvider.getEncodedPassword());
        when(userRepository.save(any(User.class)))
                .thenAnswer(i -> i.getArguments()[0]);

    }

    @Test
    void registerUser_ShouldReturnSavedUser_WhenProvidedValidInput() {
        SignUp signUpDto = testDataProvider.getSampleSignUp();

        User user = testDataProvider.getSampleUser();

        PersonDTO personDTO = testDataProvider.getSampleUserAsPersonDTO();
        doNothing()
                .when(userValidator)
                .validateUsernameAndEmailAvailability(anyString(), anyString());

        when(userMapper.convertToEntity(any(SignUp.class), any(Role.class))).thenReturn(user);
        when(userMapper.convertToDTO(any(User.class))).thenReturn(personDTO);

        PersonDTO result = userService.registerUser(signUpDto);

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getEmail(), result.getEmail());
        verify(userValidator, times(1)).validateUsernameAndEmailAvailability(anyString(), anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_ShouldThrowUsernameTakenException_WhenUsernameExists() {
        SignUp signUpDto = testDataProvider.getSampleSignUp();

        doThrow(UsernameTakenException.class)
                .when(userValidator)
                .validateUsernameAndEmailAvailability(anyString(), anyString());

        assertThrows(UsernameTakenException.class, () -> userService.registerUser(signUpDto));
        verify(userValidator, times(1)).validateUsernameAndEmailAvailability(anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_ShouldThrowEmailTakenException_WhenEmailExists() {
        SignUp signUpDto = testDataProvider.getSampleSignUp();

        doThrow(EmailTakenException.class)
                .when(userValidator)
                .validateUsernameAndEmailAvailability(anyString(), anyString());

        assertThrows(EmailTakenException.class, () -> userService.registerUser(signUpDto));
        verify(userValidator, times(1)).validateUsernameAndEmailAvailability(anyString(), anyString());
        verify(userRepository, never()).save(any());
    }

    @Test
    void authenticateUser_ShouldReturnToken_WhenCredentialsAreValid() {
        SignIn signInDto = testDataProvider.getSampleSignIn();
        User user = testDataProvider.getSampleUser();

        Mockito.when(jwtUtil.generateAccessToken(user)).thenReturn("some_token");

        AuthenticationResponse response = userService.authenticateUser(signInDto);

        assertNotNull(response);
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals("some_token", response.getToken());
    }

    @Test
    void updateLastName_ShouldUpdateName_WhenPasswordAndFieldAreValid() {
        UserRequest userRequest = testDataProvider.getSampleUserAsUserRequest();
        userRequest.setLastName("New Last Name");

        User currentUser = testDataProvider.getSampleUser();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String result = userService.updatePersonalData(userRequest);

        assertEquals("Personal data updated successfully!", result);
        verify(userRepository).updateLastName(currentUser.getId(), userRequest.getLastName());
    }

    @Test
    void updateLastName_ShouldThrowInvalidPasswordException_WhenPasswordIsInvalid() {
        UserRequest userRequest = testDataProvider.getSampleUserAsUserRequest();
        userRequest.setPassword("InvalidPassword");
        userRequest.setLastName("New Last Name");

        User currentUser = testDataProvider.getSampleUser();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        doThrow(InvalidPasswordException.class)
                .when(userValidator)
                .validatePassword(anyString(), anyString());

        assertThrows(InvalidPasswordException.class, () -> userService.updatePersonalData(userRequest));
        verify(userRepository, never()).updateLastName(anyLong(), anyString());
    }

    @Test
    void updatePassword_ShouldThrowInvalidPasswordException_WhenCurrentPasswordIsInvalid() {
        UpdatePasswordRequest updatePasswordRequest = new UpdatePasswordRequest();
        updatePasswordRequest.setCurrentPassword("InvalidPassword");
        updatePasswordRequest.setNewPassword("New@Password");

        User currentUser = testDataProvider.getSampleUser();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        doThrow(InvalidPasswordException.class)
                .when(userValidator)
                .validatePassword(anyString(), anyString());

        assertThrows(InvalidPasswordException.class, () -> userService.updatePassword(updatePasswordRequest));
        verify(userRepository, never()).updateUserPassword(anyString(), anyString());
    }

    @Test
    void updatePhone_ShouldUpdatePhone_WhenPasswordAndFieldAreValid() {
        UserRequest userRequest = testDataProvider.getSampleUserAsUserRequest();
        userRequest.setPhone("New Phone");

        User currentUser = testDataProvider.getSampleUser();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(currentUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        String result = userService.updatePersonalData(userRequest);

        assertEquals("Personal data updated successfully!", result);
        verify(userRepository).updatePhone(currentUser.getId(), userRequest.getPhone());
    }

    @Test
    void updatePhone_ShouldThrowInvalidPasswordException_WhenPasswordIsInvalid() {
        UserRequest userRequest = testDataProvider.getSampleUserAsUserRequest();
        userRequest.setPassword("Invalid Password");
        userRequest.setPhone("New Phone");

        User currentUser = testDataProvider.getSampleUser();

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        doThrow(InvalidPasswordException.class)
                .when(userValidator)
                .validatePassword(anyString(), anyString());

        assertThrows(InvalidPasswordException.class, () -> userService.updatePersonalData(userRequest));
        verify(userRepository, never()).updatePhone(anyLong(), anyString());
    }

}
