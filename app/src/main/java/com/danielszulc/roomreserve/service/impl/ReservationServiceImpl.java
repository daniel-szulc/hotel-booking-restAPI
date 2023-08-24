package com.danielszulc.roomreserve.service.impl;

import com.danielszulc.roomreserve.dto.ReservationDTO;
import com.danielszulc.roomreserve.dto.ReservationRequest;
import com.danielszulc.roomreserve.dto.RoomDTO;
import com.danielszulc.roomreserve.enums.PaymentType;
import com.danielszulc.roomreserve.enums.ReservationStatus;
import com.danielszulc.roomreserve.exception.*;
import com.danielszulc.roomreserve.mapper.ReservationMapper;
import com.danielszulc.roomreserve.model.*;
import com.danielszulc.roomreserve.repository.GuestRepository;
import com.danielszulc.roomreserve.repository.ReservationRepository;
import com.danielszulc.roomreserve.repository.RoomRepository;
import com.danielszulc.roomreserve.repository.UserRepository;
import com.danielszulc.roomreserve.service.ReservationService;
import com.danielszulc.roomreserve.service.RoomService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    @Autowired
    private UserValidatorImpl userValidator;
    @Autowired
    private ReservationMapper reservationMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuestRepository guestRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomService roomService;

    private Person findPersonById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            return guestRepository.findById(id).orElse(null);
        }
    }

    private Person findPersonByRequestOrLoggedInUser(Long guestId) {
        Person person;
        try {
            userValidator.validateAdminOrHotelPermissions();
            person = findPersonById(guestId);
        } catch (AccessDeniedException e) {
            // Catch the AccessDeniedException to fallback to the currently logged-in user
            person = findPersonById(userValidator.getCurrentLoggedInUserId());
        }

        if (person == null) {
            throw new UserNotFoundException("User not found");
        }
        return person;
    }

    @Override
    public ReservationDTO createReservation(ReservationRequest reservationRequest) {

        Reservation reservation = new Reservation();

        // Check if the date is not in the past
        LocalDate now = LocalDate.now();
        if (reservationRequest.getStartDate().isBefore(now) || reservationRequest.getEndDate().isBefore(now)) {
            throw new IllegalArgumentException("Date must not be in the past");
        }

        // Check that the end date is not earlier than the start date
        if (reservationRequest.getEndDate().isBefore(reservationRequest.getStartDate())) {
            throw new IllegalArgumentException("End date must not be earlier than start date");
        }

        // Room availability verification
        List<RoomDTO> availableRooms = roomService.getAvailableRooms(reservationRequest.getStartDate(), reservationRequest.getEndDate());
        boolean isRoomAvailable = availableRooms.stream()
                .anyMatch(room -> room.getId().equals(reservationRequest.getRoomId()));
        if (!isRoomAvailable) {
            throw new RoomNotAvailableException("Room is not available for the selected dates");
        }

        Person person = findPersonByRequestOrLoggedInUser(reservationRequest.getGuestId());

        Room room = roomRepository.findById(reservationRequest.getRoomId())
                .orElseThrow(() -> new RoomNotFoundException("Room not found"));

        if (room.getNoOfPerson() < reservationRequest.getNoOfPerson()) {
            throw new IllegalArgumentException("The number of persons exceeds the room capacity");
        }

        reservation.setGuest(person);
        reservation.setRoom(room);
        reservation.setStartDate(reservationRequest.getStartDate());
        reservation.setEndDate(reservationRequest.getEndDate());
        reservation.setNoOfPerson(reservationRequest.getNoOfPerson());
        reservation.setPayment(PaymentType.valueOf(reservationRequest.getPayment().toUpperCase()));
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);
        return reservationMapper.convertToDTO(savedReservation);
    }

    @Override
    public String cancelReservation(Long reservationId) {
        userValidator.validateAdminOrHotelPermissions();
        reservationRepository.deleteById(reservationId);
        return "Reservation canceled successfully";
    }

    @Override
    public String updateReservation(ReservationRequest reservationRequest) {
        userValidator.validateAdminOrHotelPermissions();
        Reservation reservation = reservationRepository.findById(reservationRequest.getId())
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

        boolean updateOccurred = false;

        if (reservationRequest.getGuestId() != null) {
            Person person = findPersonByRequestOrLoggedInUser(reservationRequest.getGuestId());
            reservation.setGuest(person);
            updateOccurred = true;
        }

        if (reservationRequest.getRoomId() != null) {
            Room room = roomRepository.findById(reservationRequest.getRoomId())
                    .orElseThrow(() -> new RoomNotFoundException("Room not found"));
            reservation.setRoom(room);
            updateOccurred = true;
        }

        if (reservationRequest.getStartDate() != null) {
            reservation.setStartDate(reservationRequest.getStartDate());
            updateOccurred = true;
        }

        if (reservationRequest.getEndDate() != null) {
            reservation.setEndDate(reservationRequest.getEndDate());
            updateOccurred = true;
        }

        if (reservationRequest.getNoOfPerson() != null) {
            reservation.setNoOfPerson(reservationRequest.getNoOfPerson());
            updateOccurred = true;
        }

        if (reservationRequest.getPayment() != null && !reservationRequest.getPayment().isEmpty()) {
            PaymentType newPaymentType = PaymentType.valueOf(reservationRequest.getPayment().toUpperCase());
            reservation.setPayment(newPaymentType);
            updateOccurred = true;
        }

        if (updateOccurred) {
            reservationRepository.save(reservation);
            return "Reservation updated successfully";
        } else {
            throw new IllegalArgumentException("No valid field specified for update.");
        }
    }


    @Override
    public String checkIn(Long reservationId) {
        userValidator.validateAdminOrHotelPermissions();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CHECKED_IN);
        reservationRepository.save(reservation);
        return "Checked in successfully";
    }

    @Override
    public String checkOut(Long reservationId) {
        userValidator.validateAdminOrHotelPermissions();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CHECKED_OUT);
        reservationRepository.save(reservation);
        return "Checked out successfully";
    }

    @Override
    public String confirmReservation(Long reservationId) {
        userValidator.validateAdminOrHotelPermissions();
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
        return "Reservation confirmed";
    }

    @Override
    public List<ReservationDTO> getAllReservations() {
        userValidator.validateAdminOrHotelPermissions();
        List<Reservation> reservations = reservationRepository.findAll();
        return reservations.stream().map(reservationMapper::convertToDTO).toList();
    }

    @Override
    public List<ReservationDTO> getMyReservations() {
        List<Reservation> myReservations = reservationRepository.findByGuestId(userValidator.getCurrentLoggedInUserId());
        return myReservations.stream().map(reservationMapper::convertToDTO).toList();
    }

    @Override
    public List<ReservationDTO> getReservationsByGuestId(Long guestId) {
        try {
            userValidator.validateAdminOrHotelPermissions();
        } catch (AccessDeniedException e) {
            guestId = userValidator.getCurrentLoggedInUserId();
        }
        List<Reservation> reservations = reservationRepository.findByGuestId(guestId);
        return reservations.stream().map(reservationMapper::convertToDTO).toList();
    }

    @Override
    public ReservationDTO getReservationById(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ReservationNotFoundException("Reservation not found"));

            try {
                userValidator.validateAdminOrHotelPermissions();
            } catch (AccessDeniedException e) {
                Long guestId = userValidator.getCurrentLoggedInUserId();
                if(!reservation.getGuest().getId().equals(guestId))
                {
                    throw new AccessDeniedException("Access denied");
                }
            }
        return reservationMapper.convertToDTO(reservation);
    }
}
