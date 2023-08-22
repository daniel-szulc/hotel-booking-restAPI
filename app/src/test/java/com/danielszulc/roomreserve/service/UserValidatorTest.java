package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.AccessDeniedException;
import com.danielszulc.roomreserve.exception.EmailTakenException;
import com.danielszulc.roomreserve.exception.InvalidPasswordException;
import com.danielszulc.roomreserve.exception.UsernameTakenException;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.GuestRepository;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.impl.UserValidatorImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserValidatorTest {

    @InjectMocks
    private UserValidatorImpl userValidator;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void validateUsernameAndEmailAvailability_ShouldThrowUsernameTakenException() {
        when(userRepository.existsByUsername("takenUsername")).thenReturn(true);

        assertThrows(UsernameTakenException.class, () -> userValidator.validateUsernameAndEmailAvailability("takenUsername", "email@example.com"));
    }

    @Test
    void validateEmailAvailability_ShouldThrowEmailTakenException_UserRepo() {
        when(userRepository.existsByEmail("taken@email.com")).thenReturn(true);

        assertThrows(EmailTakenException.class, () -> userValidator.validateEmailAvailability("taken@email.com"));
    }

    @Test
    void validateEmailAvailability_ShouldThrowEmailTakenException_GuestRepo() {
        when(guestRepository.existsByEmail("taken@email.com")).thenReturn(true);

        assertThrows(EmailTakenException.class, () -> userValidator.validateEmailAvailability("taken@email.com"));
    }

    @Test
    void validatePassword_ShouldThrowInvalidPasswordException() {
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(InvalidPasswordException.class, () -> userValidator.validatePassword("encodedPassword", "wrongPassword"));
    }

    @Test
    void validateAdminPermissions_ShouldThrowAccessDeniedException() {
        User user = new User();
        user.setRole(Role.ROLE_CLIENT);

        assertThrows(AccessDeniedException.class, () -> userValidator.validateAdminPermissions(user));
    }

    @Test
    void validateHotelPermissions_ShouldThrowAccessDeniedException() {
        User user = new User();
        user.setRole(Role.ROLE_CLIENT);

        assertThrows(AccessDeniedException.class, () -> userValidator.validateHotelPermissions(user));
    }

    @Test
    void validateAdminOrHotelPermissions_ShouldThrowAccessDeniedException() {
        User user = new User();
        user.setRole(Role.ROLE_CLIENT);

        assertThrows(AccessDeniedException.class, () -> userValidator.validateAdminOrHotelPermissions(user));
    }

    @Test
    void validateAdminOrHotelPermissions_ShouldNotThrowException_ForAdmin() {
        User user = new User();
        user.setRole(Role.ROLE_ADMIN);

        assertDoesNotThrow(() -> userValidator.validateAdminOrHotelPermissions(user));
    }

    @Test
    void validateAdminOrHotelPermissions_ShouldNotThrowException_ForHotel() {
        User user = new User();
        user.setRole(Role.ROLE_HOTEL);

        assertDoesNotThrow(() -> userValidator.validateAdminOrHotelPermissions(user));
    }

    @Test
    void validatePermissions_ShouldThrowAccessDeniedException_WhenRoleNotAllowed() {
        User user = new User();
        user.setRole(Role.ROLE_CLIENT);

        List<Role> allowedRoles = Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_HOTEL);

        assertThrows(AccessDeniedException.class, () -> userValidator.validatePermissions(user, allowedRoles));
    }

    @Test
    void validatePermissions_ShouldNotThrowException_WhenRoleIsAllowed() {
        User user = new User();
        user.setRole(Role.ROLE_ADMIN);

        List<Role> allowedRoles = Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_HOTEL);

        assertDoesNotThrow(() -> userValidator.validatePermissions(user, allowedRoles));
    }
}
