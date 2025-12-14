package com.accounting.service.reservations;

import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationParticipantDTO;
import com.accounting.dto.reservation.ReservationShortDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Reservation;
import com.accounting.entity.ReservationParticipant;
import com.accounting.mapper.ReservationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceHelperTest {

    @Mock
    private ReservationMapper reservationMapper;

    @InjectMocks
    private ReservationServiceHelper reservationServiceHelper;

    private Reservation testReservation;
    private ReservationDTO testReservationDTO;
    private ReservationShortDTO testReservationShortDTO;
    private Client testClient;
    private List<ReservationParticipant> testParticipants;
    private List<ReservationParticipantDTO> testParticipantDTOs;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");
        testClient.setTitle("Mr.");
        testClient.setTelephone("1234567890");

        testReservation = new Reservation();
        testReservation.setReservationId(1L);
        testReservation.setClient(testClient);
        testReservation.setDestination("Paris");
        testReservation.setDepartureDate(LocalDate.now().plusDays(10));
        testReservation.setReturnDate(LocalDate.now().plusDays(17));
        testReservation.setPrice(2000.0);
        testReservation.setReceipted(500.0);
        testReservation.setBalance(1500.0);

        testParticipants = new ArrayList<>();
        ReservationParticipant participant1 = new ReservationParticipant();
        participant1.setParticipantName("John Doe");
        participant1.setParticipantAge(30L);
        participant1.setReservation(testReservation);
        testParticipants.add(participant1);

        ReservationParticipant participant2 = new ReservationParticipant();
        participant2.setParticipantName("Jane Doe");
        participant2.setParticipantAge(10L);
        participant2.setReservation(testReservation);
        testParticipants.add(participant2);

        testReservation.setParticipants(testParticipants);

        testParticipantDTOs = new ArrayList<>();
        ReservationParticipantDTO participantDTO1 = new ReservationParticipantDTO();
        participantDTO1.setParticipantName("John Doe");
        participantDTO1.setParticipantAge(30L);
        testParticipantDTOs.add(participantDTO1);

        ReservationParticipantDTO participantDTO2 = new ReservationParticipantDTO();
        participantDTO2.setParticipantName("Jane Doe");
        participantDTO2.setParticipantAge(10L);
        testParticipantDTOs.add(participantDTO2);

        testReservationDTO = new ReservationDTO();
        testReservationDTO.setReservationId(1L);
        testReservationDTO.setDestination("Paris");

        testReservationShortDTO = new ReservationShortDTO();
        testReservationShortDTO.setReservationId(1L);
    }

    @Test
    void testMapEntityToReservationShortDTO_Success() {
        // Arrange
        when(reservationMapper.toShortDTO(testReservation)).thenReturn(testReservationShortDTO);

        // Act
        ReservationShortDTO result = reservationServiceHelper.mapEntityToReservationShortDTO(testReservation);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getClient());
        assertEquals(testClient.getClientId(), result.getClient().getClientId());
        assertEquals(testClient.getTitle(), result.getClient().getTitle());
        assertEquals("John Doe", result.getClient().getFullName());
        assertEquals(testClient.getTelephone(), result.getClient().getTelephone());
        assertEquals(2, result.getPersonNo());
        verify(reservationMapper, times(1)).toShortDTO(testReservation);
    }

    @Test
    void testMapEntityToReservationDTO_Success() {
        // Arrange
        when(reservationMapper.toDTO(testReservation)).thenReturn(testReservationDTO);

        // Act
        ReservationDTO result = reservationServiceHelper.mapEntityToReservationDTO(testReservation);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getParticipants());
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getParticipants().contains("John Doe"));
        assertTrue(result.getParticipants().contains("Jane Doe"));
        assertNotNull(result.getChildrenAge());
        assertEquals(1, result.getChildrenAge().size());
        assertTrue(result.getChildrenAge().contains(10));
        verify(reservationMapper, times(1)).toDTO(testReservation);
    }

    @Test
    void testMapEntityToReservationDTO_OnlyAdults_NoChildren() {
        // Arrange
        testParticipants.clear();
        ReservationParticipant adult1 = new ReservationParticipant();
        adult1.setParticipantName("Adult One");
        adult1.setParticipantAge(25L);
        testParticipants.add(adult1);

        ReservationParticipant adult2 = new ReservationParticipant();
        adult2.setParticipantName("Adult Two");
        adult2.setParticipantAge(30L);
        testParticipants.add(adult2);

        testReservation.setParticipants(testParticipants);

        when(reservationMapper.toDTO(testReservation)).thenReturn(testReservationDTO);

        // Act
        ReservationDTO result = reservationServiceHelper.mapEntityToReservationDTO(testReservation);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getParticipants().size());
        assertTrue(result.getChildrenAge().isEmpty());
    }

    @Test
    void testMapToReservationParticipant_Success() {
        // Act
        List<ReservationParticipant> result = reservationServiceHelper.mapToReservationParticipant(
            testReservation, testParticipantDTOs);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getParticipantName());
        assertEquals(30L, result.get(0).getParticipantAge());
        assertEquals(testReservation, result.get(0).getReservation());
        assertEquals("Jane Doe", result.get(1).getParticipantName());
        assertEquals(10L, result.get(1).getParticipantAge());
        assertEquals(testReservation, result.get(1).getReservation());
    }

    @Test
    void testMapToReservationParticipantDTO_Success() {
        // Act
        List<ReservationParticipantDTO> result = reservationServiceHelper.mapToReservationParticipantDTO(testParticipants);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getParticipantName());
        assertEquals(30L, result.get(0).getParticipantAge());
        assertEquals(1L, result.get(0).getReservationId());
        assertEquals("Jane Doe", result.get(1).getParticipantName());
        assertEquals(10L, result.get(1).getParticipantAge());
        assertEquals(1L, result.get(1).getReservationId());
    }

    @Test
    void testValidateReservation_Success() {
        // Act & Assert
        assertDoesNotThrow(() -> reservationServiceHelper.validateReservation(testReservation));
    }

    @Test
    void testValidateReservation_NoParticipants_ThrowsException() {
        // Arrange
        testReservation.setParticipants(new ArrayList<>());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationServiceHelper.validateReservation(testReservation)
        );
        assertEquals("Reservation must have at least one participant", exception.getMessage());
    }

    @Test
    void testValidateReservation_NullParticipants_ThrowsException() {
        // Arrange
        testReservation.setParticipants(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationServiceHelper.validateReservation(testReservation)
        );
        assertEquals("Reservation must have at least one participant", exception.getMessage());
    }

    @Test
    void testValidateReservation_ReturnDateBeforeDepartureDate_ThrowsException() {
        // Arrange
        testReservation.setDepartureDate(LocalDate.now().plusDays(10));
        testReservation.setReturnDate(LocalDate.now().plusDays(5));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationServiceHelper.validateReservation(testReservation)
        );
        assertEquals("Return date cannot be before departure date", exception.getMessage());
    }

    @Test
    void testValidateReservation_NegativePrice_ThrowsException() {
        // Arrange
        testReservation.setPrice(-100.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationServiceHelper.validateReservation(testReservation)
        );
        assertEquals("Price cannot be negative", exception.getMessage());
    }

    @Test
    void testValidateReservation_NegativeReceipted_ThrowsException() {
        // Arrange
        testReservation.setReceipted(-50.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationServiceHelper.validateReservation(testReservation)
        );
        assertEquals("Receipted amount cannot be negative", exception.getMessage());
    }

    @Test
    void testValidateReservation_NegativeBalance_ThrowsException() {
        // Arrange
        testReservation.setBalance(-100.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationServiceHelper.validateReservation(testReservation)
        );
        assertEquals("Balance cannot be negative", exception.getMessage());
    }

    @Test
    void testValidateReservation_ReceiptedExceedsPrice_ThrowsException() {
        // Arrange
        testReservation.setPrice(1000.0);
        testReservation.setReceipted(1500.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationServiceHelper.validateReservation(testReservation)
        );
        assertEquals("Receipted amount cannot exceed total price", exception.getMessage());
    }

    @Test
    void testValidateReservation_ReceiptedEqualToPrice_Success() {
        // Arrange
        testReservation.setPrice(1000.0);
        testReservation.setReceipted(1000.0);
        testReservation.setBalance(0.0);

        // Act & Assert
        assertDoesNotThrow(() -> reservationServiceHelper.validateReservation(testReservation));
    }

    @Test
    void testValidateReservation_SameDepartureAndReturnDate_Success() {
        // Arrange
        LocalDate sameDate = LocalDate.now().plusDays(10);
        testReservation.setDepartureDate(sameDate);
        testReservation.setReturnDate(sameDate);

        // Act & Assert
        assertDoesNotThrow(() -> reservationServiceHelper.validateReservation(testReservation));
    }

    @Test
    void testValidateReservation_ZeroPrice_Success() {
        // Arrange
        testReservation.setPrice(0.0);
        testReservation.setReceipted(0.0);
        testReservation.setBalance(0.0);

        // Act & Assert
        assertDoesNotThrow(() -> reservationServiceHelper.validateReservation(testReservation));
    }
}
