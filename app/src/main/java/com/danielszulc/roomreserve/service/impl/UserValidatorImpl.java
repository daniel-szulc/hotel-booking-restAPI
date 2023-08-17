package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.*;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserValidatorImpl implements UserValidator {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Override
    public void validateUsernameAndEmailAvailability(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException("Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
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
            throw new AccessDeniedException("Only administrators can create users.");
        }
    }

}
