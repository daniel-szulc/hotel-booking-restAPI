package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends PersonRepository<User> {
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
    void updateUserPassword(@Param("username") String username, @Param("newPassword") String newPassword);
}
