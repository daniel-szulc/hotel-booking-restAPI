package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.SignIn;
import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;


    public AuthController(PasswordEncoder passwordEncoder, UserRepository userRepository, AuthenticationManager authenticationManager) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignUp signUpDto){

        // check if username exists
        if(userRepository.existsByUsername(signUpDto.getUsername())){
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // check if email exists
        if(userRepository.existsByEmail(signUpDto.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // create user object
        User user = new User();
        user.setUsername(signUpDto.getUsername());
        user.setEmail(signUpDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        //set the lowest role level by default
        user.setRole(Role.ROLE_CLIENT);

        userRepository.save(user);

        return new ResponseEntity<>("User registered successfully", HttpStatus.OK);

    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody SignIn loginDto){

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        Optional<User> optionalUser = userRepository.findByUsername(loginDto.getUsername());

        if(optionalUser.isEmpty()) {
            return new ResponseEntity<>("Username not found!", HttpStatus.BAD_REQUEST);
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(loginDto.getPassword(), user.getPassword())) {
            return new ResponseEntity<>("Invalid password!", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("User logged in successfully", HttpStatus.OK);


    }


}
