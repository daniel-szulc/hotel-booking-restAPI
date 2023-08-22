package com.danielszulc.roomreserve.service;

import com.danielszulc.roomreserve.TestDataProvider;
import com.danielszulc.roomreserve.dto.GuestRequest;
import com.danielszulc.roomreserve.dto.PersonDTO;
import com.danielszulc.roomreserve.exception.EmailTakenException;
import com.danielszulc.roomreserve.exception.UserNotFoundException;
import com.danielszulc.roomreserve.mapper.UserMapper;
import com.danielszulc.roomreserve.model.Guest;
import com.danielszulc.roomreserve.repository.GuestRepository;
import com.danielszulc.roomreserve.service.impl.GuestServiceImpl;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;


@DataJpaTest
@ActiveProfiles("test")
class GuestServiceTest {

    private final TestDataProvider testDataProvider = new TestDataProvider();

    @InjectMocks
    private GuestServiceImpl guestService;

    @MockBean
    private GuestRepository guestRepository;

    @Mock
    private UserValidator userValidator;

    @Mock
    private UserMapper<Guest> userMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        guestService = new GuestServiceImpl(guestRepository, userValidator);
        guestService.setUserMapper(userMapper);
    }

    @Test
    void createGuest_ShouldReturnSavedGuest_WhenProvidedValidInput() {
        Guest guest = testDataProvider.getSampleGuest();
        PersonDTO personDTO = testDataProvider.getSampleGuestAsPersonDTO();

        when(userMapper.convertToDTO(any(Guest.class))).thenReturn(personDTO);
        when(guestRepository.save(any(Guest.class))).thenReturn(guest);

        PersonDTO result = guestService.createGuest(guest);

        assertNotNull(result);
        assertEquals(guest.getEmail(), result.getEmail());
        verify(userValidator, times(1)).validateEmailAvailability(anyString());
        verify(guestRepository, times(1)).save(any(Guest.class));
    }

    @Test
    void createGuest_ShouldThrowEmailTakenException_WhenEmailExists() {
        Guest guest = testDataProvider.getSampleGuest();

        doThrow(EmailTakenException.class)
                .when(userValidator)
                .validateEmailAvailability(anyString());

        assertThrows(EmailTakenException.class, () -> guestService.createGuest(guest));
        verify(userValidator, times(1)).validateEmailAvailability(anyString());
        verify(guestRepository, never()).save(any());
    }

    @Test
    void findGuestByIdOrEmail_ShouldReturnGuest_WhenGuestExists() {
        Guest guest = testDataProvider.getSampleGuest();
        PersonDTO personDTO = testDataProvider.getSampleGuestAsPersonDTO();

        when(userMapper.convertToDTO(any(Guest.class))).thenReturn(personDTO);
        when(guestRepository.findById(anyLong())).thenReturn(Optional.of(guest));

        PersonDTO result = guestService.findGuestByIdOrEmail(1L, null);

        assertNotNull(result);
        assertEquals(guest.getEmail(), result.getEmail());
    }

    @Test
    void findGuestByIdOrEmail_ShouldThrowUserNotFoundException_WhenGuestDoesNotExist() {
        when(guestRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(guestRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> guestService.findGuestByIdOrEmail(1L, null));
    }

    @Test
    void updatePersonalData_ShouldReturnSuccessMessage_WhenDataIsUpdated() {
        Guest guest = testDataProvider.getSampleGuest();
        GuestRequest guestRequest = testDataProvider.getSampleGuestAsGuestRequest();
        guestRequest.setPhone("New phone");
        when(guestRepository.findById(anyLong())).thenReturn(Optional.of(guest));

        String result = guestService.updatePersonalData(guestRequest);

        assertEquals("Personal data updated successfully!", result);
    }

}
