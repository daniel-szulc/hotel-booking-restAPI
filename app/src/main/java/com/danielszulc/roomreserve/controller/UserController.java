package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping
    public ResponseEntity<UserDTO> getUser() {
        UserDTO res = userService.getUserData();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok("User deleted successfully!");
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
    public ResponseEntity<UserDTO> createUser(@RequestBody @Valid SignUp signUpDto) {
        UserDTO res = userService.createUserByAdmin(signUpDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestBody UserSearchRequest searchRequest) {
        List<UserDTO> users = userService.searchUsers(searchRequest);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<UserDTO> getUser(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username
    ) {
        UserDTO userDTO = userService.findUserByIdOrEmailOrUsername(id, email, username);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }
}
