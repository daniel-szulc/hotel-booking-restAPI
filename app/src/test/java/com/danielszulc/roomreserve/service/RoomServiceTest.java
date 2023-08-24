package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.TestDataProvider;
import com.danielszulc.roomreserve.dto.RoomDTO;
import com.danielszulc.roomreserve.dto.RoomRequest;
import com.danielszulc.roomreserve.exception.RoomNotFoundException;
import com.danielszulc.roomreserve.mapper.RoomMapper;
import com.danielszulc.roomreserve.model.Room;
import com.danielszulc.roomreserve.repository.RoomRepository;
import com.danielszulc.roomreserve.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@DataJpaTest
@ActiveProfiles("test")
class RoomServiceTest {

    @InjectMocks
    private RoomServiceImpl roomService;

    @MockBean
    private RoomRepository roomRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private RoomMapper roomMapper;

    private final TestDataProvider testDataProvider = new TestDataProvider();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        roomService = new RoomServiceImpl(roomRepository, roomMapper);
    }

    @Test
    void getAll_ShouldReturnListOfRooms() {
        RoomDTO roomDTO = testDataProvider.getSampleRoomDTO();
        Room room = testDataProvider.getSampleRoom();
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(roomMapper.convertToDTO(room)).thenReturn(roomDTO);

        List<RoomDTO> result = roomService.getAll();

        assertEquals(1, result.size());
        assertEquals(roomDTO, result.get(0));
    }

    @Test
    void getRoomById_ShouldReturnRoom_WhenRoomExists() {
        RoomDTO roomDTO = testDataProvider.getSampleRoomDTO();
        Room room = testDataProvider.getSampleRoom();
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomMapper.convertToDTO(room)).thenReturn(roomDTO);

        RoomDTO result = roomService.getRoomById(1L);

        assertEquals(roomDTO, result);
    }

    @Test
    void getRoomById_ShouldThrowRoomNotFoundException_WhenRoomDoesNotExist() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.getRoomById(1L));
    }

    @Test
    void deleteRoom_ShouldReturnMessage_WhenRoomIsDeleted() {
        Room room = testDataProvider.getSampleRoom();
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));

        String result = roomService.deleteRoom(1L);

        assertEquals("Room deleted successfully", result);
        verify(roomRepository).delete(room);
    }

    @Test
    void createRoom_ShouldReturnCreatedRoom() {
        RoomRequest roomRequest = new RoomRequest();
        Room room = testDataProvider.getSampleRoom();
        when(roomMapper.convertToRoomEntity(any(RoomRequest.class))).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room result = roomService.createRoom(roomRequest);

        assertEquals(room, result);
    }
}
