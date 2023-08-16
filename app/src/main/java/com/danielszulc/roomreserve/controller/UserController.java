package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.SignUp;
import com.danielszulc.roomreserve.dto.UpdatePasswordRequest;
import com.danielszulc.roomreserve.dto.UserRequest;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping
    public ResponseEntity<User> getUser() {
        User res =  userService.getUserData();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        String res = userService.updatePassword(updatePasswordRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update/personal")
    public ResponseEntity<String> updatePersonalData(@RequestBody UserRequest userRequest) {
        String res = userService.updatePersonalData(userRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody @Valid SignUp signUpDto){
        User res = userService.createUserByAdmin(signUpDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }
}
