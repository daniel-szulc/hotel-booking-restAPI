package com.danielszulc.roomreserve.controller;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User")
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<PersonDTO>> getAll()
    {
        List<PersonDTO> persons = userService.getAll();
        return new ResponseEntity<>(persons, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<PersonDTO> getUser() {
        PersonDTO res = userService.getUserData();
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
    public ResponseEntity<PersonDTO> createUser(@RequestBody @Valid SignUp signUpDto) {
        PersonDTO res = userService.createUserByAdmin(signUpDto);
        return new ResponseEntity<>(res, HttpStatus.CREATED);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PersonDTO>> searchUsers(@RequestBody UserSearchRequest searchRequest) {
        List<PersonDTO> users = userService.searchUsers(searchRequest);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/find")
    public ResponseEntity<PersonDTO> getUser(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "username", required = false) String username
    ) {
        PersonDTO personDTO = userService.findUserByIdOrEmailOrUsername(id, email, username);
        return new ResponseEntity<>(personDTO, HttpStatus.OK);
    }
}
