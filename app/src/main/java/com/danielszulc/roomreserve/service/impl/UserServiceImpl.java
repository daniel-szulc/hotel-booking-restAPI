package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.enums.Gender;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.*;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtUtil;

    @Override
    public User registerUser(SignUp signUpDto) {
        validateUsernameAndEmailAvailability(signUpDto.getUsername(), signUpDto.getEmail());

        User user = createUserFromSignUp(signUpDto, Role.ROLE_CLIENT);
        return userRepository.save(user);
    }

    @Override
    public AuthenticationResponse authenticateUser(SignIn loginDto) {
        try {
            Authentication authentication = authenticateWithCredentials(loginDto.getUsername(), loginDto.getPassword());
            User user = (User) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(user);

            return new AuthenticationResponse(user.getEmail(), accessToken);
        } catch (BadCredentialsException e) {
            throw new InvalidLoginException("Invalid username or password!");
        }
    }

    @Override
    public String updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        User currentUser = getCurrentLoggedInUser();
        validatePassword(currentUser.getPassword(), updatePasswordRequest.getCurrentPassword());

        String newPassword = updatePasswordRequest.getNewPassword();
        userRepository.updateUserPassword(currentUser.getUsername(), passwordEncoder.encode(newPassword));

        log.info("Update successful");
        return "Password updated successfully!";
    }

    @Override
    public String updatePersonalData(UserRequest userRequest) {
        User currentUser = getCurrentLoggedInUser();
        validatePassword(currentUser.getPassword(), userRequest.getPassword());

        boolean updateOccurred = false;

        if (userRequest.getFirstName() != null && !userRequest.getFirstName().isEmpty()) {
            userRepository.updateFirstName(currentUser.getUsername(), userRequest.getFirstName());
            updateOccurred = true;
        }

        if (userRequest.getLastName() != null && !userRequest.getLastName().isEmpty()) {
            userRepository.updateLastName(currentUser.getUsername(), userRequest.getLastName());
            updateOccurred = true;
        }

        if (userRequest.getPhone() != null && !userRequest.getPhone().isEmpty()) {
            userRepository.updateUserPhone(currentUser.getUsername(), userRequest.getPhone());
            updateOccurred = true;
        }

        if (userRequest.getAddress() != null) {
            userRepository.updateUserAddress(currentUser.getUsername(), userRequest.getAddress());
            updateOccurred = true;
        }

        if (userRequest.getGender() != null) {
            Gender newGender = Gender.valueOf(userRequest.getGender().toUpperCase());
            userRepository.updateUserGender(currentUser.getUsername(), newGender);
            updateOccurred = true;
        }

        if (updateOccurred) {
            log.info("Update successful");
            return "Personal data updated successfully!";
        } else {
            throw new IllegalArgumentException("No valid field specified for update.");
        }
    }

    @Override
    public User createUserByAdmin(SignUp signUpDto) {
        Role role = determineUserRole(signUpDto.getRole());
        validateAdminPermissions();

        User user = createUserFromSignUp(signUpDto, role);
        return userRepository.save(user);
    }

    private Authentication authenticateWithCredentials(String username, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    private void validateUsernameAndEmailAvailability(String username, String email) {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameTakenException("Username is already taken!");
        }

        if (userRepository.existsByEmail(email)) {
            throw new EmailTakenException("Email is already taken!");
        }
    }

    private void validatePassword(String encodedPassword, String providedPassword) {
        if (!passwordEncoder.matches(providedPassword, encodedPassword)) {
            throw new InvalidPasswordException("Incorrect password!");
        }
    }

    @Override
    public User getUserData(){
            User user = getCurrentLoggedInUser();
            user.setPassword(null);

            return user;
    }

    private User getCurrentLoggedInUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(email)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    private void validateAdminPermissions() {
        User currentUser = getCurrentLoggedInUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            throw new UnauthorizedException("Only administrators can create users.");
        }
    }

    private Role determineUserRole(String requestedRole) {
        return (requestedRole != null) ? Role.valueOf(requestedRole) : Role.ROLE_CLIENT;
    }

    private User createUserFromSignUp(SignUp signUpDto, Role role) {
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        user.setRole(role);
        return user;
    }

}
