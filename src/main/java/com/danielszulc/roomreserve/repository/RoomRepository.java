package com.danielszulc.roomreserve.repository;

import com.danielszulc.roomreserve.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {

}
