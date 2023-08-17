package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.dto.*;

import java.util.List;

public interface UserService {
    UserDTO registerUser(SignUp signUpDto);
    AuthenticationResponse authenticateUser(SignIn loginDto);
    String updatePassword(UpdatePasswordRequest updatePasswordRequest);
    String updatePersonalData(UserRequest userRequest);
    UserDTO createUserByAdmin(SignUp signUpDto);
    UserDTO getUserData();
    String deleteUserByUsername(String username);
    List<UserDTO> searchUsers(UserSearchRequest searchRequest);
    UserDTO findUserByIdOrEmailOrUsername(Long id, String email, String username);

}
