package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@Component
public class HotelUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User loadUserByUsername(String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return optionalUser.get();
    }
    @PostConstruct
    protected void initialize() {
        createDefaultAdminUser();
    }

    private void createDefaultAdminUser() {
        String defaultUsername = "admin";
        String defaultEmail = "admin@admin.com";
        String defaultPassword = "password";
        Role defaultRole = Role.ROLE_ADMIN;

        // Check if the user already exists by username or email
        boolean userExists = userRepository.existsByUsername(defaultUsername) || userRepository.existsByEmail(defaultEmail);

        if (!userExists) {
            // If user doesn't exist, create and save the user
            User user = new User();
            user.setUsername(defaultUsername);
            user.setEmail(defaultEmail);
            user.setPassword(passwordEncoder.encode(defaultPassword));
            user.setRole(defaultRole);
            userRepository.save(user);
        }
    }

    @Transactional
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

}

