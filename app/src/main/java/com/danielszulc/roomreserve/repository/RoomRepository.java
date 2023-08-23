package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.enums.RoomType;
import com.danielszulc.roomreserve.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByNumber(String number);

    @Query("SELECT r FROM Room r WHERE r.id NOT IN (" +
            "SELECT res.room.id FROM Reservation res WHERE (" +
            "res.startDate <= :endDate AND res.endDate >= :startDate" +
            ")" +
            ")"
    )
    List<Room> findAvailableRooms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT r FROM Room r WHERE r.isOccupied = true")
    List<Room> findCurrentlyOccupiedRooms();

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} r SET r.number = :newNumber WHERE r.id = :id")
    void updateNumber(@Param("id") Long id, @Param("newNumber") String newNumber);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} r SET r.noOfPerson = :newNoOfPerson WHERE r.id = :id")
    void updateNoOfPerson(@Param("id") Long id, @Param("newNoOfPerson") Integer newNoOfPerson);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} r SET r.type = :newType WHERE r.id = :id")
    void updateType(@Param("id") Long id, @Param("newType") RoomType newType);

    @Modifying
    @Transactional
    @Query("UPDATE #{#entityName} r SET r.price = :newPrice WHERE r.id = :id")
    void updatePrice(@Param("id") Long id, @Param("newPrice") double newPrice);


}
