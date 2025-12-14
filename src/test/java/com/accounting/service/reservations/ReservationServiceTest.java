package com.accounting.service.reservations;

import com.accounting.dto.CommentDTO;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.dto.reservation.ReservationPatchDTO;
import com.accounting.dto.reservation.ReservationParticipantDTO;
import com.accounting.dto.reservation.ReservationShortDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Offer;
import com.accounting.entity.Provider;
import com.accounting.entity.Reservation;
import com.accounting.entity.ReservationParticipant;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.mapper.ReservationMapper;
import com.accounting.repository.ReservationRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.comment.CommentService;
import com.accounting.service.providers.ProviderService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationMapper reservationMapper;

    @Mock
    private ReservationServiceHelper reservationServiceHelper;

    @Mock
    private ClientService clientService;

    @Mock
    private ProviderService providerService;

    @Mock
    private CommentService commentService;

    @InjectMocks
    private ReservationServiceImp reservationService;

    private Reservation testReservation;
    private ReservationDTO testReservationDTO;
    private ReservationShortDTO testReservationShortDTO;
    private ReservationForm testReservationForm;
    private Client testClient;
    private Provider testProvider;
    private Offer testOffer;
    private ClientShortDTO testClientShortDTO;
    private ProviderDTO testProviderDTO;
    private List<ReservationParticipant> testParticipants;
    private List<ReservationParticipantDTO> testParticipantDTOs;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");

        testProvider = new Provider();
        testProvider.setProviderId(1L);
        testProvider.setProviderName("Test Provider");

        testOffer = new Offer();
        testOffer.setOfferId(1L);

        testClientShortDTO = new ClientShortDTO();
        testClientShortDTO.setClientId(1L);
        testClientShortDTO.setFullName("John Doe");

        testProviderDTO = new ProviderDTO();
        testProviderDTO.setProviderId(1L);
        testProviderDTO.setName("Test Provider");

        testReservation = new Reservation();
        testReservation.setReservationId(1L);
        testReservation.setClient(testClient);
        testReservation.setProvider(testProvider);
        testReservation.setOffer(testOffer);
        testReservation.setDestination("Paris");
        testReservation.setDepartureDate(LocalDate.now().plusDays(10));
        testReservation.setReturnDate(LocalDate.now().plusDays(17));
        testReservation.setBookedDate(LocalDate.now());
        testReservation.setBookingRef("TEST-123");
        testReservation.setPrice(2000.0);
        testReservation.setReceipted(500.0);
        testReservation.setBalance(1500.0);
        testReservation.setIsDeleted(false);

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
        testReservationDTO.setPrice(2000.0);

        testReservationShortDTO = new ReservationShortDTO();
        testReservationShortDTO.setReservationId(1L);

        testReservationForm = new ReservationForm();
        testReservationForm.setClientId(1L);
        testReservationForm.setProviderId(1L);
        testReservationForm.setDestination("Paris");
        testReservationForm.setDepartureDate(LocalDate.now().plusDays(10));
        testReservationForm.setReturnDate(LocalDate.now().plusDays(17));
        testReservationForm.setTotalPrice(2000.0);
        testReservationForm.setAdvance(500.0);
        testReservationForm.setRemainingCost(1500.0);
        testReservationForm.setParticipants(testParticipantDTOs);
    }

    @Test
    void testCreateReservation_WithOffer_Success() throws ClientNotFoundException {
        // Arrange
        when(reservationMapper.toEntity(testReservationForm)).thenReturn(testReservation);
        when(clientService.findById(1L)).thenReturn(testClient);
        when(providerService.findById(1L)).thenReturn(testProvider);
        when(reservationServiceHelper.mapToReservationParticipant(any(), any())).thenReturn(testParticipants);
        when(reservationRepository.save(testReservation)).thenReturn(testReservation);
        when(reservationServiceHelper.mapEntityToReservationDTO(testReservation)).thenReturn(testReservationDTO);
        doNothing().when(reservationServiceHelper).validateReservation(testReservation);

        // Act
        ReservationDTO result = reservationService.createReservation(testReservationForm, testOffer);

        // Assert
        assertNotNull(result);
        assertEquals(testReservationDTO.getReservationId(), result.getReservationId());
        assertEquals(testOffer, testReservation.getOffer());
        assertNotNull(testReservation.getBookingRef());
        verify(reservationMapper, times(1)).toEntity(testReservationForm);
        verify(clientService, times(1)).findById(1L);
        verify(providerService, times(1)).findById(1L);
        verify(reservationServiceHelper, times(1)).validateReservation(testReservation);
        verify(reservationRepository, times(1)).save(testReservation);
        verify(commentService, never()).addComment(any());
    }

    @Test
    void testCreateReservation_WithoutOffer_Success() throws ClientNotFoundException {
        // Arrange
        when(reservationMapper.toEntity(testReservationForm)).thenReturn(testReservation);
        when(clientService.findById(1L)).thenReturn(testClient);
        when(providerService.findById(1L)).thenReturn(testProvider);
        when(reservationServiceHelper.mapToReservationParticipant(any(), any())).thenReturn(testParticipants);
        when(reservationRepository.save(testReservation)).thenReturn(testReservation);
        when(reservationServiceHelper.mapEntityToReservationDTO(testReservation)).thenReturn(testReservationDTO);
        doNothing().when(reservationServiceHelper).validateReservation(testReservation);
        doNothing().when(commentService).addComment(any(CommentDTO.class));

        // Act
        ReservationDTO result = reservationService.createReservation(testReservationForm, null);

        // Assert
        assertNotNull(result);
        assertEquals(testReservationDTO.getReservationId(), result.getReservationId());
        assertNotNull(testReservation.getBookingRef());

        // Verify comment was added
        ArgumentCaptor<CommentDTO> commentCaptor = ArgumentCaptor.forClass(CommentDTO.class);
        verify(commentService, times(1)).addComment(commentCaptor.capture());
        CommentDTO capturedComment = commentCaptor.getValue();
        assertEquals("Created Reservation without base offer!", capturedComment.getMessage());
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void testCreateReservation_ValidationFails_ThrowsException() throws ClientNotFoundException {
        // Arrange
        when(reservationMapper.toEntity(testReservationForm)).thenReturn(testReservation);
        when(clientService.findById(1L)).thenReturn(testClient);
        when(providerService.findById(1L)).thenReturn(testProvider);
        when(reservationServiceHelper.mapToReservationParticipant(any(), any())).thenReturn(testParticipants);
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(reservationServiceHelper).validateReservation(testReservation);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationService.createReservation(testReservationForm, null)
        );
        assertEquals("Validation failed", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testCreateReservation_ClientNotFound_ThrowsException() throws ClientNotFoundException {
        // Arrange
        when(reservationMapper.toEntity(testReservationForm)).thenReturn(testReservation);
        when(clientService.findById(999L))
            .thenThrow(new ClientNotFoundException("Client not found with ID: 999"));
        testReservationForm.setClientId(999L);

        // Act & Assert
        ClientNotFoundException exception = assertThrows(
            ClientNotFoundException.class,
            () -> reservationService.createReservation(testReservationForm, null)
        );
        assertEquals("Client not found with ID: 999", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));

        // Act
        Reservation result = reservationService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testReservation.getReservationId(), result.getReservationId());
        assertEquals(testReservation.getDestination(), result.getDestination());
        verify(reservationRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound_ThrowsException() {
        // Arrange
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(
            EntityNotFoundException.class,
            () -> reservationService.findById(999L)
        );
        assertEquals("No reservations found for reservationId: 999", exception.getMessage());
        verify(reservationRepository, times(1)).findById(999L);
    }

    @Test
    void testGetReservation_Success() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationServiceHelper.mapEntityToReservationDTO(testReservation)).thenReturn(testReservationDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);
        when(providerService.mapToDto(testProvider)).thenReturn(testProviderDTO);

        // Act
        ReservationDTO result = reservationService.getReservation(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testReservationDTO.getReservationId(), result.getReservationId());
        assertNotNull(result.getClient());
        assertNotNull(result.getProvider());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationServiceHelper, times(1)).mapEntityToReservationDTO(testReservation);
    }

    @Test
    void testGetReservations_WithoutFilter_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservationPage = new PageImpl<>(List.of(testReservation), pageable, 1);

        when(reservationRepository.findAllActiveReservations(any(Pageable.class))).thenReturn(reservationPage);
        when(reservationServiceHelper.mapEntityToReservationShortDTO(testReservation)).thenReturn(testReservationShortDTO);

        // Act
        PageDTO result = reservationService.getReservations(null, 0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, ((List<?>) result.getItems()).size());
        verify(reservationRepository, times(1)).findAllActiveReservations(any(Pageable.class));
        verify(reservationRepository, never()).findByReservationsByClient(anyInt(), any(Pageable.class));
    }

    @Test
    void testGetReservations_WithClientFilter_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Reservation> reservationPage = new PageImpl<>(List.of(testReservation), pageable, 1);

        when(reservationRepository.findByReservationsByClient(eq(1), any(Pageable.class))).thenReturn(reservationPage);
        when(reservationServiceHelper.mapEntityToReservationShortDTO(testReservation)).thenReturn(testReservationShortDTO);

        // Act
        PageDTO result = reservationService.getReservations(1, 0, 10);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, ((List<?>) result.getItems()).size());
        verify(reservationRepository, times(1)).findByReservationsByClient(eq(1), any(Pageable.class));
        verify(reservationRepository, never()).findAllActiveReservations(any(Pageable.class));
    }

    @Test
    void testPatchReservation_Success() {
        // Arrange
        ReservationPatchDTO patchDTO = new ReservationPatchDTO();
        patchDTO.setDestination("Rome");

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        doNothing().when(reservationMapper).updateEntityFromPatch(patchDTO, testReservation);
        doNothing().when(reservationServiceHelper).validateReservation(testReservation);
        when(reservationRepository.save(testReservation)).thenReturn(testReservation);
        when(reservationServiceHelper.mapEntityToReservationDTO(testReservation)).thenReturn(testReservationDTO);

        // Act
        ReservationDTO result = reservationService.patchReservation(1L, patchDTO);

        // Assert
        assertNotNull(result);
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationMapper, times(1)).updateEntityFromPatch(patchDTO, testReservation);
        verify(reservationServiceHelper, times(1)).validateReservation(testReservation);
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void testPatchReservation_WithProviderUpdate_Success() {
        // Arrange
        ReservationPatchDTO patchDTO = new ReservationPatchDTO();
        patchDTO.setProviderId(2L);

        Provider newProvider = new Provider();
        newProvider.setProviderId(2L);

        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(providerService.findById(2L)).thenReturn(newProvider);
        doNothing().when(reservationMapper).updateEntityFromPatch(patchDTO, testReservation);
        doNothing().when(reservationServiceHelper).validateReservation(testReservation);
        when(reservationRepository.save(testReservation)).thenReturn(testReservation);
        when(reservationServiceHelper.mapEntityToReservationDTO(testReservation)).thenReturn(testReservationDTO);

        // Act
        ReservationDTO result = reservationService.patchReservation(1L, patchDTO);

        // Assert
        assertNotNull(result);
        assertEquals(newProvider, testReservation.getProvider());
        verify(providerService, times(1)).findById(2L);
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void testPatchReservation_NotFound_ThrowsException() {
        // Arrange
        ReservationPatchDTO patchDTO = new ReservationPatchDTO();
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            EntityNotFoundException.class,
            () -> reservationService.patchReservation(999L, patchDTO)
        );
        verify(reservationRepository, times(1)).findById(999L);
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testPatchReservation_ValidationFails_ThrowsException() {
        // Arrange
        ReservationPatchDTO patchDTO = new ReservationPatchDTO();
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        doNothing().when(reservationMapper).updateEntityFromPatch(patchDTO, testReservation);
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(reservationServiceHelper).validateReservation(testReservation);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> reservationService.patchReservation(1L, patchDTO)
        );
        assertEquals("Validation failed", exception.getMessage());
        verify(reservationRepository, never()).save(any());
    }

    @Test
    void testDeleteReservationById_Success() {
        // Arrange
        when(reservationRepository.findById(1L)).thenReturn(Optional.of(testReservation));
        when(reservationRepository.save(testReservation)).thenReturn(testReservation);

        // Act
        reservationService.deleteReservationById(1L);

        // Assert
        assertTrue(testReservation.getIsDeleted());
        assertNotNull(testReservation.getDeletionDate());
        verify(reservationRepository, times(1)).findById(1L);
        verify(reservationRepository, times(1)).save(testReservation);
    }

    @Test
    void testDeleteReservationById_NotFound_ThrowsException() {
        // Arrange
        when(reservationRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            EntityNotFoundException.class,
            () -> reservationService.deleteReservationById(999L)
        );
        verify(reservationRepository, times(1)).findById(999L);
        verify(reservationRepository, never()).save(any());
    }
}
