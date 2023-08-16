package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Modifying
    @Query("UPDATE User u SET u.username = :newUsername WHERE u.username = :username")
    void updateUserName(@Param("username") String username, @Param("newUsername") String newUsername);

    @Modifying
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
    void updateUserPassword(@Param("username") String username, @Param("newPassword") String newPassword);

    @Modifying
    @Query("UPDATE User u SET u.phone = :newPhone WHERE u.username = :username")
    void updateUserPhone(@Param("username") String username, @Param("newPhone") String newPhone);

}
