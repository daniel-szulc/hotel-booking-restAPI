package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.AuthenticationResponse;
import com.danielszulc.roomreserve.dto.SignIn;
import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

     private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<User> registerUser(@RequestBody SignUp signUpDto){

        User res;
        try {
            res = userService.registerUser(signUpDto);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody SignIn loginDto) {

        AuthenticationResponse res;
        try {
            res = userService.authenticateUser(loginDto);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
