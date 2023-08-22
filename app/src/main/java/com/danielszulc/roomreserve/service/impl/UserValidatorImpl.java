package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.*;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.GuestRepository;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class UserValidatorImpl implements UserValidator {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuestRepository guestRepository;

    @Override
    public void validateUsernameAndEmailAvailability(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException("Username is already taken!");
        }

        validateEmailAvailability(email);
    }

    @Override
    public void validateEmailAvailability(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new EmailTakenException("Email is already taken!");
        }

        if (guestRepository.existsByEmail(email)) {
            throw new EmailTakenException("Email is already taken!");
        }
    }

    @Override
    public void validatePassword(String encodedPassword, String providedPassword) {
        if (!passwordEncoder.matches(providedPassword, encodedPassword)) {
            throw new InvalidPasswordException("Incorrect password!");
        }
    }
    @Override
    public void validateAdminPermissions(User user) {
        if (!user.getRole().equals(Role.ROLE_ADMIN)) {
            throw new AccessDeniedException("Unauthorized access. This operation is restricted to administrators.");
        }
    }

    @Override
    public void validateHotelPermissions(User user) {
        if (!user.getRole().equals(Role.ROLE_HOTEL)) {
            throw new AccessDeniedException("\"Unauthorized access. This operation is restricted to hotel staff only.");
        }
    }

    @Override
    public void validateAdminOrHotelPermissions(User user) {
        Role userRole = user.getRole();

        if (!(userRole.equals(Role.ROLE_ADMIN) || userRole.equals(Role.ROLE_HOTEL))) {
            throw new AccessDeniedException("Unauthorized access. This operation is restricted to administrators and hotel staff.");
        }
    }

    @Override
    public void validatePermissions(User user, Role role) {
        validatePermissions(user, List.of(role));
    }

    @Override
    public void validatePermissions(User user, Collection<Role> allowedRoles) {
        Role userRole = user.getRole();

        if (!allowedRoles.contains(userRole)) {
            String allowedRolesString = allowedRoles.stream()
                    .map(Role::name)
                    .reduce((role1, role2) -> role1 + " or " + role2)
                    .orElse("");

            throw new AccessDeniedException("Unauthorized access. This operation is restricted to " + allowedRolesString + " only.");
        }
    }

}
