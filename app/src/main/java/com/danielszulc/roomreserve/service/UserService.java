package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.config.JwtTokenUtil;
import com.danielszulc.roomreserve.dto.AuthenticationResponse;
import com.danielszulc.roomreserve.dto.SignIn;
import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.exception.EmailTakenException;
import com.danielszulc.roomreserve.exception.InvalidLoginException;
import com.danielszulc.roomreserve.exception.UsernameTakenException;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private UserRepository userRepository;

    private AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenUtil jwtUtil;

    public User registerUser(SignUp signUpDto) {

        // check if username exists
        if (userRepository.existsByUsername(signUpDto.getUsername())) {
            throw new UsernameTakenException("Username is already taken!");
        }

        // check if email exists
        if (userRepository.existsByEmail(signUpDto.getEmail())) {
            throw new EmailTakenException("Email is already taken!");
        }

        // create user object
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));

        //set the lowest role level by default
        user.setRole(Role.ROLE_CLIENT);

        return userRepository.save(user);
    }

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

}
