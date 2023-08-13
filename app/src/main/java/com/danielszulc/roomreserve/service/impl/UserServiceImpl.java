package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.*;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    @Autowired JwtTokenUtil jwtUtil;

    @Override
    public User registerUser(SignUp signUpDto) {
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new UsernameTakenException("Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new EmailTakenException("Email is already taken!");
        }

        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setRole(Role.ROLE_CLIENT);

        return userRepository.save(user);
    }

    @Override
    public AuthenticationResponse authenticateUser(SignIn loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);

            return new AuthenticationResponse(user.getEmail(), accessToken);
        } catch (BadCredentialsException e) {
            throw new InvalidLoginException("Invalid username or password!");
        }
    }

    private User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found!");
        }
        return user.get();
    }

    @Override
    public String updateName(UpdateRequest updateRequest) {
        User currentUser = getCurrentLoggedInUser();

        String password = updateRequest.getPassword();
        if (!passwordEncoder.matches(password, currentUser.getPassword())) {
            throw new InvalidPasswordException("Incorrect password!");
        }
        userRepository.updateUserName(currentUser.getUsername(), updateRequest.getField());

        log.info("Update successfull");
        return "Name updated successfully!";
    }

    @Override
    public String updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User currentUser = getCurrentLoggedInUser();

        String oldPassword = updatePasswordRequest.getCurrentPassword();
        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new InvalidPasswordException("Incorrect password!");
        }
        String newPassword = updatePasswordRequest.getNewPassword();

        if (!passwordEncoder.matches(oldPassword, currentUser.getPassword())) {
            throw new InvalidPasswordException("Incorrect old password!");
        }

        userRepository.updateUserPassword(currentUser.getUsername(), passwordEncoder.encode(newPassword));

        log.info("Update successful");
        return "Password updated successfully!";
    }

    @Override
    public String updatePhone(UpdateRequest updateRequest) {
        User currentUser = getCurrentLoggedInUser();

        String password = updateRequest.getPassword();
        if (!passwordEncoder.matches(password, currentUser.getPassword())) {
            throw new InvalidPasswordException("Incorrect password!");
        }
        userRepository.updateUserPhone(currentUser.getUsername(), updateRequest.getField());

        log.info("Update successful");
        return "Phone updated successfully!";
    }

}
