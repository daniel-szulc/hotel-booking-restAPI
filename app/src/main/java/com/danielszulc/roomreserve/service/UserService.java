package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.model.User;

public interface UserService {
    User registerUser(SignUp signUpDto);
    AuthenticationResponse authenticateUser(SignIn loginDto);
    String updatePassword(UpdatePasswordRequest updatePasswordRequest);
    String updatePersonalData(UserRequest userRequest);
    User createUserByAdmin(SignUp signUpDto);
    User getUserData();
}
