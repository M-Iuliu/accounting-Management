package com.accounting.service.offers;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferPatchDTO;
import com.accounting.dto.offer.OfferUpdateStatusDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.entity.Client;
import com.accounting.entity.Offer;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.OfferStatusConflictException;
import com.accounting.mapper.OfferMapper;
import com.accounting.repository.OfferRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.reservations.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferMapper offerMapper;

    @Mock
    private OfferServiceHelper offerServiceHelper;

    @Mock
    private ClientService clientService;

    @Mock
    private ReservationService reservationService;

    @InjectMocks
    private OfferServiceImp offerService;

    private Offer testOffer;
    private OfferDTO testOfferDTO;
    private OfferForm testOfferForm;
    private Client testClient;
    private ClientShortDTO testClientShortDTO;

    @BeforeEach
    void setUp() {
        testClient = new Client();
        testClient.setClientId(1L);
        testClient.setName("John");
        testClient.setSurname("Doe");

        testClientShortDTO = new ClientShortDTO();
        testClientShortDTO.setClientId(1L);
        testClientShortDTO.setFullName("John Doe");

        testOffer = new Offer();
        testOffer.setOfferId(1L);
        testOffer.setClient(testClient);
        testOffer.setDestination("Paris");
        testOffer.setAdultsNo(2);
        testOffer.setChildrenNo(1);
        testOffer.setGrossPrice(1500.0);
        testOffer.setAcquisitionPrice(1200.0);
        testOffer.setAdvance(500.0);
        testOffer.setCommission(300.0);
        testOffer.setStatus(OfferStatusEnum.OFERTAT);
        testOffer.setOfferDate(LocalDate.now());
        testOffer.setIsDeleted(false);

        testOfferDTO = new OfferDTO();
        testOfferDTO.setOfferId(1L);
        testOfferDTO.setDestination("Paris");
        testOfferDTO.setAdultsNo(2);
        testOfferDTO.setChildrenNo(1);
        testOfferDTO.setGrossPrice(1500.0);
        testOfferDTO.setStatus(OfferStatusEnum.OFERTAT);

        testOfferForm = new OfferForm();
        testOfferForm.setClientId(1L);
        testOfferForm.setDestination("Paris");
        testOfferForm.setAdultsNo(2);
        testOfferForm.setChildrenNo(1);
        testOfferForm.setGrossPrice(1500.0);
        testOfferForm.setAcquisitionPrice(1200.0);
    }

    @Test
    void testCreateOffer_Success() throws ClientNotFoundException {
        // Arrange
        when(offerMapper.toEntity(testOfferForm)).thenReturn(testOffer);
        when(clientService.findById(1L)).thenReturn(testClient);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);
        when(offerServiceHelper.mapOfferToOfferDTO(testOffer)).thenReturn(testOfferDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);
        doNothing().when(offerServiceHelper).validateOffer(testOffer);

        // Act
        OfferDTO result = offerService.createOffer(testOfferForm);

        // Assert
        assertNotNull(result);
        assertEquals(testOfferDTO.getOfferId(), result.getOfferId());
        assertEquals(testOfferDTO.getDestination(), result.getDestination());
        assertEquals(OfferStatusEnum.OFERTAT, testOffer.getStatus());
        assertNotNull(testOffer.getOfferDate());
        verify(offerMapper, times(1)).toEntity(testOfferForm);
        verify(clientService, times(1)).findById(1L);
        verify(offerServiceHelper, times(1)).validateOffer(testOffer);
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    void testCreateOffer_ValidationFails_ThrowsException() throws ClientNotFoundException {
        // Arrange
        when(offerMapper.toEntity(testOfferForm)).thenReturn(testOffer);
        when(clientService.findById(1L)).thenReturn(testClient);
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(offerServiceHelper).validateOffer(testOffer);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> offerService.createOffer(testOfferForm)
        );
        assertEquals("Validation failed", exception.getMessage());
        verify(offerRepository, never()).save(any());
    }

    @Test
    void testCreateOffer_ClientNotFound_ThrowsException() throws ClientNotFoundException {
        // Arrange
        when(offerMapper.toEntity(testOfferForm)).thenReturn(testOffer);
        when(clientService.findById(999L))
            .thenThrow(new ClientNotFoundException("Client not found with ID: 999"));
        testOfferForm.setClientId(999L);

        // Act & Assert
        ClientNotFoundException exception = assertThrows(
            ClientNotFoundException.class,
            () -> offerService.createOffer(testOfferForm)
        );
        assertEquals("Client not found with ID: 999", exception.getMessage());
        verify(offerRepository, never()).save(any());
    }

    @Test
    void testFindOfferById_Success() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));

        // Act
        Offer result = offerService.findOfferById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOffer.getOfferId(), result.getOfferId());
        assertEquals(testOffer.getDestination(), result.getDestination());
        verify(offerRepository, times(1)).findById(1L);
    }

    @Test
    void testFindOfferById_NotFound_ThrowsException() {
        // Arrange
        when(offerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> offerService.findOfferById(999L)
        );
        assertEquals("Offer not found with ID: 999", exception.getMessage());
        verify(offerRepository, times(1)).findById(999L);
    }

    @Test
    void testGetOfferDTOById_Success() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(offerServiceHelper.mapOfferToOfferDTO(testOffer)).thenReturn(testOfferDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);

        // Act
        OfferDTO result = offerService.getOfferDTOById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOfferDTO.getOfferId(), result.getOfferId());
        assertNotNull(result.getClient());
        verify(offerRepository, times(1)).findById(1L);
        verify(offerServiceHelper, times(1)).mapOfferToOfferDTO(testOffer);
        verify(clientService, times(1)).mapToShortDto(testClient);
    }

    @Test
    void testGetOffers_WithoutFilter_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> offerPage = new PageImpl<>(List.of(testOffer), pageable, 1);

        when(offerRepository.findAllActiveOffers(any(Pageable.class), Boolean.TRUE)).thenReturn(offerPage);
        when(offerServiceHelper.mapOfferToOfferDTO(testOffer)).thenReturn(testOfferDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);

        // Act
        PageDTO result = offerService.getOffers(null, 0, 10, Boolean.TRUE);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, ((List<?>) result.getItems()).size());
        verify(offerRepository, times(1)).findAllActiveOffers(any(Pageable.class), Boolean.TRUE);
        verify(offerRepository, never()).findByFilter(anyString(), any(Pageable.class), Boolean.TRUE);
    }

    @Test
    void testGetOffers_WithFilter_Success() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> offerPage = new PageImpl<>(List.of(testOffer), pageable, 1);

        when(offerRepository.findByFilter(eq("John"), any(Pageable.class), Boolean.TRUE)).thenReturn(offerPage);
        when(offerServiceHelper.mapOfferToOfferDTO(testOffer)).thenReturn(testOfferDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);

        // Act
        PageDTO result = offerService.getOffers("John", 0, 10, Boolean.TRUE);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getItems());
        assertEquals(1, ((List<?>) result.getItems()).size());
        verify(offerRepository, times(1)).findByFilter(eq("John"), any(Pageable.class), Boolean.TRUE);
        verify(offerRepository, never()).findAllActiveOffers(any(Pageable.class), Boolean.TRUE);
    }

    @Test
    void testGetOffers_EmptyFilter_UsesAllActiveOffers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Offer> offerPage = new PageImpl<>(List.of(), pageable, 0);

        when(offerRepository.findAllActiveOffers(any(Pageable.class), Boolean.TRUE)).thenReturn(offerPage);

        // Act
        PageDTO result = offerService.getOffers("   ", 0, 10, Boolean.TRUE);

        // Assert
        assertNotNull(result);
        verify(offerRepository, times(1)).findAllActiveOffers(any(Pageable.class), Boolean.TRUE);
        verify(offerRepository, never()).findByFilter(anyString(), any(Pageable.class), Boolean.TRUE);
    }

    @Test
    void testPatchOffer_Success() {
        // Arrange
        OfferPatchDTO patchDTO = new OfferPatchDTO();
        patchDTO.setDestination("Rome");
        patchDTO.setGrossPrice(2000.0);

        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        doNothing().when(offerMapper).updateEntityFromPatch(patchDTO, testOffer);
        doNothing().when(offerServiceHelper).validateOffer(testOffer);
        when(offerRepository.save(testOffer)).thenReturn(testOffer);
        when(offerServiceHelper.mapOfferToOfferDTO(testOffer)).thenReturn(testOfferDTO);
        when(clientService.mapToShortDto(testClient)).thenReturn(testClientShortDTO);

        // Act
        OfferDTO result = offerService.patchOffer(1L, patchDTO);

        // Assert
        assertNotNull(result);
        verify(offerRepository, times(1)).findById(1L);
        verify(offerMapper, times(1)).updateEntityFromPatch(patchDTO, testOffer);
        verify(offerServiceHelper, times(1)).validateOffer(testOffer);
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    void testPatchOffer_NotFound_ThrowsException() {
        // Arrange
        OfferPatchDTO patchDTO = new OfferPatchDTO();
        when(offerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> offerService.patchOffer(999L, patchDTO)
        );
        verify(offerRepository, times(1)).findById(999L);
        verify(offerRepository, never()).save(any());
    }

    @Test
    void testPatchOffer_ValidationFails_ThrowsException() {
        // Arrange
        OfferPatchDTO patchDTO = new OfferPatchDTO();
        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        doNothing().when(offerMapper).updateEntityFromPatch(patchDTO, testOffer);
        doThrow(new IllegalArgumentException("Validation failed"))
            .when(offerServiceHelper).validateOffer(testOffer);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> offerService.patchOffer(1L, patchDTO)
        );
        assertEquals("Validation failed", exception.getMessage());
        verify(offerRepository, never()).save(any());
    }

    @Test
    void testUpdateOfferStatus_OfertatToCastigat_Success() throws ClientNotFoundException {
        // Arrange
        ReservationForm reservationForm = new ReservationForm();
        reservationForm.setAdvance(500.0);

        OfferUpdateStatusDTO updateDto = new OfferUpdateStatusDTO();
        updateDto.setStatus("CASTIGAT");
        updateDto.setReservationDetails(reservationForm);

        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(testOffer)).thenReturn(testOffer);
        doNothing().when(reservationService).createReservation(reservationForm, testOffer);

        // Act
        offerService.updateOfferStatus(1L, updateDto);

        // Assert
        assertEquals(OfferStatusEnum.CASTIGAT, testOffer.getStatus());
        assertEquals(500.0, testOffer.getAdvance());
        verify(reservationService, times(1)).createReservation(reservationForm, testOffer);
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    void testUpdateOfferStatus_OfertatToPierdut_Success() throws ClientNotFoundException {
        // Arrange
        OfferUpdateStatusDTO updateDto = new OfferUpdateStatusDTO();
        updateDto.setStatus("PIERDUT");

        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // Act
        offerService.updateOfferStatus(1L, updateDto);

        // Assert
        assertEquals(OfferStatusEnum.PIERDUT, testOffer.getStatus());
        verify(reservationService, never()).createReservation(any(), any());
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    void testUpdateOfferStatus_PierdutToRevenit_Success() throws ClientNotFoundException {
        // Arrange
        testOffer.setStatus(OfferStatusEnum.PIERDUT);
        OfferUpdateStatusDTO updateDto = new OfferUpdateStatusDTO();
        updateDto.setStatus("REVENIT");

        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // Act
        offerService.updateOfferStatus(1L, updateDto);

        // Assert
        assertEquals(OfferStatusEnum.REVENIT, testOffer.getStatus());
        verify(reservationService, never()).createReservation(any(), any());
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    void testUpdateOfferStatus_InvalidTransition_ThrowsException() {
        // Arrange
        testOffer.setStatus(OfferStatusEnum.CASTIGAT);
        OfferUpdateStatusDTO updateDto = new OfferUpdateStatusDTO();
        updateDto.setStatus("PIERDUT");

        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));

        // Act & Assert
        OfferStatusConflictException exception = assertThrows(
            OfferStatusConflictException.class,
            () -> offerService.updateOfferStatus(1L, updateDto)
        );
        assertTrue(exception.getMessage().contains("can't be updated"));
        verify(offerRepository, never()).save(any());
    }

    @Test
    void testUpdateOfferStatus_CastigatToRevenit_InvalidTransition() {
        // Arrange
        testOffer.setStatus(OfferStatusEnum.CASTIGAT);
        OfferUpdateStatusDTO updateDto = new OfferUpdateStatusDTO();
        updateDto.setStatus("REVENIT");

        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));

        // Act & Assert
        assertThrows(
            OfferStatusConflictException.class,
            () -> offerService.updateOfferStatus(1L, updateDto)
        );
        verify(offerRepository, never()).save(any());
    }

    @Test
    void testDeleteOfferById_Success() {
        // Arrange
        when(offerRepository.findById(1L)).thenReturn(Optional.of(testOffer));
        when(offerRepository.save(testOffer)).thenReturn(testOffer);

        // Act
        offerService.deleteOfferById(1L);

        // Assert
        assertTrue(testOffer.getIsDeleted());
        assertEquals(OfferStatusEnum.PIERDUT, testOffer.getStatus());
        assertNotNull(testOffer.getDeletionDate());
        verify(offerRepository, times(1)).findById(1L);
        verify(offerRepository, times(1)).save(testOffer);
    }

    @Test
    void testDeleteOfferById_NotFound_ThrowsException() {
        // Arrange
        when(offerRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
            IllegalArgumentException.class,
            () -> offerService.deleteOfferById(999L)
        );
        verify(offerRepository, times(1)).findById(999L);
        verify(offerRepository, never()).save(any());
    }
}
