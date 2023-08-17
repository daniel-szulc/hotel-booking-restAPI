package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.model.User;

public interface UserValidator {
    void validateUsernameAndEmailAvailability(String username, String email);
    void validatePassword(String encodedPassword, String providedPassword);
    void validateAdminPermissions(User currentUser);
}
