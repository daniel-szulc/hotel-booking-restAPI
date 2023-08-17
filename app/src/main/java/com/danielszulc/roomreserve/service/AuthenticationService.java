package com.danielszulc.roomreserve.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationService {
    Authentication authenticateWithCredentials(String username, String password);
}
