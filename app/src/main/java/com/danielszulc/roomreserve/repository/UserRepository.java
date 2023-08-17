package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.enums.Gender;
import com.danielszulc.roomreserve.model.Address;
import com.danielszulc.roomreserve.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Optional<User> findById(Long id);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.firstName = :newFirstName WHERE u.username = :username")
    void updateFirstName(@Param("username") String username, @Param("newFirstName") String newFirstName);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.lastName = :newLastName WHERE u.username = :username")
    void updateLastName(@Param("username") String username, @Param("newLastName") String newLastName);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :newPassword WHERE u.username = :username")
    void updateUserPassword(@Param("username") String username, @Param("newPassword") String newPassword);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.phone = :newPhone WHERE u.username = :username")
    void updateUserPhone(@Param("username") String username, @Param("newPhone") String newPhone);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.gender = :newGender WHERE u.username = :username")
    void updateUserGender(@Param("username") String username, @Param("newGender") Gender newGender);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.address = :newAddress WHERE u.username = :username")
    void updateUserAddress(@Param("username") String username, @Param("newAddress") Address newAddress);
}
