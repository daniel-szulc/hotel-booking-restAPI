package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.model.User;

public interface UserService {
    User registerUser(SignUp signUpDto);
    AuthenticationResponse authenticateUser(SignIn loginDto);
    String updateName(UpdateRequest updateRequest);
    String updatePassword(UpdatePasswordRequest updatePasswordRequest);
    String updatePhone(UpdateRequest updateRequest);
    User createUserByAdmin(SignUp signUpDto);
}
