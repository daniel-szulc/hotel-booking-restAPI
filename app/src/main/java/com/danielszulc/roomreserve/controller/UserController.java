package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.UpdatePasswordRequest;
import com.danielszulc.roomreserve.dto.UpdateRequest;
import com.danielszulc.roomreserve.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PutMapping("/update/name")
    public ResponseEntity<String> updateName(@RequestBody UpdateRequest updateRequest) {
        String res = userService.updateName(updateRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update/password")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest) {
        String res = userService.updatePassword(updatePasswordRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PutMapping("/update/phone")
    public ResponseEntity<String> updatePhone(@RequestBody UpdateRequest updateRequest) {
        String res = userService.updatePhone(updateRequest);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
