package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.enums.Gender;
import com.danielszulc.roomreserve.model.Address;
import com.danielszulc.roomreserve.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@NoRepositoryBean
public interface PersonRepository<T extends Person> extends JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    Optional<T> findById(Long id);
    Optional<T> findByEmail(String email);
    Boolean existsById(long id);
    Boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.firstName = :newFirstName WHERE e.id = :id")
    void updateFirstName(@Param("id") Long id, @Param("newFirstName") String newFirstName);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.lastName = :newLastName WHERE e.id = :id")
    void updateLastName(@Param("id") Long id, @Param("newLastName") String newLastName);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.phone = :newPhone WHERE e.id = :id")
    void updatePhone(@Param("id") Long id, @Param("newPhone") String newPhone);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.gender = :newGender WHERE e.id = :id")
    void updateGender(@Param("id") Long id, @Param("newGender") Gender newGender);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} e SET e.address = :newAddress WHERE e.id = :id")
    void updateAddress(@Param("id") Long id, @Param("newAddress") Address newAddress);
}
