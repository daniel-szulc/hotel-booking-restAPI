package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.TestDataProvider;
import com.danielszulc.roomreserve.dto.ReservationDTO;
import com.danielszulc.roomreserve.dto.ReservationRequest;
import com.danielszulc.roomreserve.dto.RoomDTO;
import com.danielszulc.roomreserve.exception.RoomNotAvailableException;
import com.danielszulc.roomreserve.mapper.ReservationMapper;
import com.danielszulc.roomreserve.model.Reservation;
import com.danielszulc.roomreserve.model.Room;
import com.danielszulc.roomreserve.model.User;
import com.danielszulc.roomreserve.repository.GuestRepository;
import com.danielszulc.roomreserve.repository.ReservationRepository;
import com.danielszulc.roomreserve.repository.RoomRepository;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.impl.ReservationServiceImpl;
import com.danielszulc.roomreserve.service.impl.UserValidatorImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@DataJpaTest
@ActiveProfiles("test")
class ReservationServiceTest {

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @MockBean
    private ReservationRepository reservationRepository;

    @Mock
    private UserValidatorImpl userValidator;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GuestRepository guestRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomService roomService;

    private final TestDataProvider testDataProvider = new TestDataProvider();

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        reservationService = new ReservationServiceImpl(reservationRepository, userValidator, reservationMapper, userRepository, guestRepository, roomRepository, roomService);
    }

    @Test
    void createReservation_ShouldThrowIllegalArgumentException_WhenDatesAreInThePast() {
        ReservationRequest reservationRequest = testDataProvider.getSampleReservationRequest();
        reservationRequest.setStartDate(LocalDate.now().minusDays(1));

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(reservationRequest));
    }

    @Test
    void createReservation_ShouldThrowIllegalArgumentException_WhenEndDateIsBeforeStartDate() {
        ReservationRequest reservationRequest = testDataProvider.getSampleReservationRequest();
        reservationRequest.setStartDate(LocalDate.now().plusDays(2));
        reservationRequest.setEndDate(LocalDate.now().plusDays(1));

        assertThrows(IllegalArgumentException.class, () -> reservationService.createReservation(reservationRequest));
    }

    @Test
    void createReservation_ShouldReturnSavedReservation_WhenValidRequest() {
        ReservationRequest reservationRequest = testDataProvider.getSampleReservationRequest();
        Reservation savedReservation = testDataProvider.getSampleReservation();
        ReservationDTO reservationDTO = testDataProvider.getSampleReservationDTO();
        Room room = testDataProvider.getSampleRoom();
        RoomDTO roomDTO = testDataProvider.getSampleRoomDTO();
        User user = testDataProvider.getSampleUser();
        when(roomService.getAvailableRooms(any(), any())).thenReturn(Collections.singletonList(roomDTO));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(reservationRepository.save(any())).thenReturn(savedReservation);
        when(reservationMapper.convertToDTO(any())).thenReturn(reservationDTO);

        ReservationDTO result = reservationService.createReservation(reservationRequest);

        assertNotNull(result);
        assertEquals(savedReservation.getId(), result.getId());
    }

    @Test
    void createReservation_ShouldThrowRoomNotAvailableException_WhenNoRoomsAvailable() {
        ReservationRequest reservationRequest = testDataProvider.getSampleReservationRequest();
        when(roomService.getAvailableRooms(any(), any())).thenReturn(Collections.emptyList());

        assertThrows(RoomNotAvailableException.class, () -> reservationService.createReservation(reservationRequest));
    }

    @Test
    void cancelReservation_ShouldReturnSuccessMessage_WhenValidRequest() {
        Long reservationId = 1L; // Sample reservation ID
        String expectedMessage = "Reservation canceled successfully";

        doNothing().when(reservationRepository).deleteById(anyLong());

        String result = reservationService.cancelReservation(reservationId);

        assertEquals(expectedMessage, result);
    }
}
