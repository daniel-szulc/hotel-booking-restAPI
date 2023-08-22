package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.model.User;

import java.util.Collection;

public interface UserValidator {
    void validateUsernameAndEmailAvailability(String username, String email);
    void validateEmailAvailability(String email);
    void validatePassword(String encodedPassword, String providedPassword);
    void validateAdminPermissions(User user);
    void validateHotelPermissions(User user);
    void validateAdminOrHotelPermissions(User user);
    void validatePermissions(User user, Role role);
    void validatePermissions(User user, Collection<Role> allowedRoles);
}
