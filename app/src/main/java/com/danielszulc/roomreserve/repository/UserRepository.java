package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    void updateUserName(String username, String newPhone);
    void updateUserPassword(String username, String newPassword);
    void updateUserPhone(String username, String newPhone);

}
