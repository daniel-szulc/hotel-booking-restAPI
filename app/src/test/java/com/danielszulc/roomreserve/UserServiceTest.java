package com.danielszulc.roomreserve;

import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.AuthenticationResponse;
import com.danielszulc.roomreserve.dto.SignIn;
import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.exception.EmailTakenException;
import com.danielszulc.roomreserve.exception.InvalidLoginException;
import com.danielszulc.roomreserve.exception.UsernameTakenException;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DataJpaTest
public class UserServiceTest{

    private final String ENCODED_PASSWORD = "encodedPassword";

    @InjectMocks
    private UserService userService;
    @MockBean
    private UserRepository userRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
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

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        User result = userService.registerUser(signUpDto);

        assertNotNull(result);
        assertNotNull(result.getRole());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@email.com", result.getEmail());
    }

    @Test
    public void registerUser_ShouldThrowUsernameTakenException_WhenUsernameExists() {
        SignUp signUpDto = new SignUp();
        signUpDto.setUsername("testUser");
        signUpDto.setEmail("test@email.com");
        signUpDto.setPassword("Test@12345");

        when(userRepository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> userService.registerUser(signUpDto));
    }

    @Test
    public void registerUser_ShouldThrowEmailTakenException_WhenEmailExists() {
        SignUp signUpDto = new SignUp();
        signUpDto.setUsername("testUser");
        signUpDto.setEmail("test@email.com");
        signUpDto.setPassword("Test@12345");

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

}
