package com.danielszulc.roomreserve;

import com.danielszulc.roomreserve.dto.*;
import com.danielszulc.roomreserve.enums.PaymentType;
import com.danielszulc.roomreserve.enums.ReservationStatus;
import com.danielszulc.roomreserve.enums.Role;
import com.danielszulc.roomreserve.enums.RoomType;
import com.danielszulc.roomreserve.model.Guest;
import com.danielszulc.roomreserve.model.Reservation;
import com.danielszulc.roomreserve.model.Room;
import com.danielszulc.roomreserve.model.User;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

public class TestDataProvider {

    public String getEncodedPassword() {
        return ENCODED_PASSWORD;
    }

    private final String ENCODED_PASSWORD = "encodedPassword";

    public User getSampleUser() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        user.setEmail("test@email.com");
        user.setPassword(ENCODED_PASSWORD);
        user.setRole(Role.ROLE_CLIENT);
        return user;
    }

    public Guest getSampleGuest() {
        Guest guest = new Guest();
        guest.setId(3L);
        guest.setEmail("test@guest.com");
        return guest;
    }

    public GuestRequest getSampleGuestAsGuestRequest() {
        GuestRequest guestRequest = new GuestRequest();
        BeanUtils.copyProperties(getSampleGuest(), guestRequest);
        return guestRequest;
    }

    public UserRequest getSampleUserAsUserRequest() {
        UserRequest userRequest = new UserRequest();
        BeanUtils.copyProperties(getSampleUser(), userRequest);
        return userRequest;
    }

    public SignUp getSampleSignUp() {
        SignUp signUp = new SignUp();
        signUp.setUsername("testNewUser");
        signUp.setEmail("testNewUser@email.com");
        signUp.setPassword("Test@12345");
        signUp.setFirstName("Joe");
        signUp.setLastName("Doe");
        return signUp;
    }

    public SignIn getSampleSignIn() {
        SignIn signIn = new SignIn();
        signIn.setUsername("testUser");
        signIn.setPassword("testPassword");
        return signIn;
    }

    public PersonDTO getSampleUserAsPersonDTO() {
        PersonDTO personDTO = new PersonDTO();
        BeanUtils.copyProperties(getSampleUser(), personDTO);
        return personDTO;
    }

    public PersonDTO getSampleGuestAsPersonDTO() {
        PersonDTO personDTO = new PersonDTO();
        BeanUtils.copyProperties(getSampleGuest(), personDTO);
        return personDTO;
    }

    public Room getSampleRoom() {
        Room room = new Room();
        room.setId(10L);
        room.setType(RoomType.DOUBLE);
        room.setOccupied(false);
        room.setNumber("101");
        room.setNoOfPerson(2);
        return room;
    }
    public RoomDTO getSampleRoomDTO() {
        RoomDTO room = new RoomDTO();
        BeanUtils.copyProperties(getSampleRoom(), room);
        return room;
    }


    public Reservation getSampleReservation() {
        Reservation reservation = new Reservation();
        reservation.setId(5L);
        reservation.setGuest(getSampleGuest());
        reservation.setRoom(getSampleRoom());
        reservation.setStartDate(LocalDate.of(2023, 9, 1));
        reservation.setEndDate(LocalDate.of(2023, 9, 5));
        reservation.setNoOfPerson(2);
        reservation.setPayment(PaymentType.CREDIT_CARD);
        reservation.setStatus(ReservationStatus.PENDING);
        return reservation;
    }

    public ReservationDTO getSampleReservationDTO() {
        ReservationDTO reservationDTO = new ReservationDTO();
        BeanUtils.copyProperties(getSampleReservation(), reservationDTO);
        reservationDTO.setGuest(getSampleGuestAsPersonDTO());
        reservationDTO.setRoom(getSampleRoomDTO());
        return reservationDTO;
    }

    public ReservationRequest getSampleReservationRequest() {
        ReservationRequest request = new ReservationRequest();
        BeanUtils.copyProperties(getSampleReservation(), request);
        request.setGuestId(3L);
        request.setRoomId(10L);
        request.setPayment("CREDIT_CARD");
        return request;
    }


}
