package com.accounting.repository;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ReservationRepository using real database operations.
 * Tests the critical bug fix: deleted reservations should not appear in search results.
 */
@DataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient;
    private Provider testProvider;
    private Offer testOffer;
    private Reservation activeReservation1;
    private Reservation activeReservation2;
    private Reservation deletedReservation;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        reservationRepository.deleteAll();
        offerRepository.deleteAll();
        clientRepository.deleteAll();
        providerRepository.deleteAll();

        // Create test client
        testClient = new Client();
        testClient.setName("John");
        testClient.setSurname("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setTelephone("1234567890");
        testClient.setIsDeleted(false);
        testClient = clientRepository.save(testClient);

        // Create test provider
        testProvider = new Provider();
        testProvider.setProviderName("Travel Agency Inc");
        testProvider.setTelephone("0987654321");
        testProvider.setEmail("contact@travelagency.com");
        testProvider.setWebLink("www.travelagency.com");
        testProvider.setIsDeleted(false);
        testProvider = providerRepository.save(testProvider);

        // Create test offer
        testOffer = new Offer();
        testOffer.setClient(testClient);
        testOffer.setOfferDate(LocalDate.now());
        testOffer.setDestination("Paris");
        testOffer.setPeriod("Summer 2025");
        testOffer.setCurrency("EUR");
        testOffer.setBudget(1000.0);
        testOffer.setGrossPrice(1200.0);
        testOffer.setAdvance(300.0);
        testOffer.setCommission(100.0);
        testOffer.setAcquisitionPrice(1100.0);
        testOffer.setAdultsNo(2);
        testOffer.setChildrenNo(1);
        testOffer.setStatus(OfferStatusEnum.OFERTAT);
        testOffer.setIsDeleted(null);
        testOffer = offerRepository.save(testOffer);

        // Create active reservation 1
        activeReservation1 = new Reservation();
        activeReservation1.setClient(testClient);
        activeReservation1.setProvider(testProvider);
        activeReservation1.setOffer(testOffer);
        activeReservation1.setBookingRef("BK001");
        activeReservation1.setBookedDate(LocalDate.now());
        activeReservation1.setDepartureDate(LocalDate.now().plusDays(30));
        activeReservation1.setReturnDate(LocalDate.now().plusDays(37));
        activeReservation1.setDestination("Paris");
        activeReservation1.setHotel("Hotel Paris");
        activeReservation1.setRoomNo(2);
        activeReservation1.setTransport("Flight");
        activeReservation1.setPrice(1200.0);
        activeReservation1.setReceipted(300.0);
        activeReservation1.setBalance(900.0);
        activeReservation1.setPaymentDueDate(LocalDate.now().plusDays(15));
        activeReservation1.setCurrency("EUR");
        activeReservation1.setIsDeleted(null);

        // Create active reservation 2
        activeReservation2 = new Reservation();
        activeReservation2.setClient(testClient);
        activeReservation2.setProvider(testProvider);
        activeReservation2.setOffer(null);
        activeReservation2.setBookingRef("BK002");
        activeReservation2.setBookedDate(LocalDate.now().minusDays(5));
        activeReservation2.setDepartureDate(LocalDate.now().plusDays(60));
        activeReservation2.setReturnDate(LocalDate.now().plusDays(67));
        activeReservation2.setDestination("Rome");
        activeReservation2.setHotel("Hotel Rome");
        activeReservation2.setRoomNo(1);
        activeReservation2.setTransport("Train");
        activeReservation2.setPrice(900.0);
        activeReservation2.setReceipted(450.0);
        activeReservation2.setBalance(450.0);
        activeReservation2.setPaymentDueDate(LocalDate.now().plusDays(45));
        activeReservation2.setCurrency("EUR");
        activeReservation2.setIsDeleted(null);

        // Create deleted reservation
        deletedReservation = new Reservation();
        deletedReservation.setClient(testClient);
        deletedReservation.setProvider(testProvider);
        deletedReservation.setOffer(null);
        deletedReservation.setBookingRef("BK003");
        deletedReservation.setBookedDate(LocalDate.now().minusDays(30));
        deletedReservation.setDepartureDate(LocalDate.now().minusDays(10));
        deletedReservation.setReturnDate(LocalDate.now().minusDays(3));
        deletedReservation.setDestination("London");
        deletedReservation.setHotel("Hotel London");
        deletedReservation.setRoomNo(1);
        deletedReservation.setTransport("Flight");
        deletedReservation.setPrice(1500.0);
        deletedReservation.setReceipted(1500.0);
        deletedReservation.setBalance(0.0);
        deletedReservation.setPaymentDueDate(LocalDate.now().minusDays(20));
        deletedReservation.setCurrency("GBP");
        deletedReservation.setIsDeleted(true);
        deletedReservation.setDeletionDate(new Date());

        // Persist test data
        entityManager.persist(activeReservation1);
        entityManager.persist(activeReservation2);
        entityManager.persist(deletedReservation);
        entityManager.flush();
    }

    @Test
    void testFindAllActiveReservations_ReturnsOnlyActiveReservations() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Reservation> result = reservationRepository.findAllActiveReservations(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(reservation -> reservation.getIsDeleted() == null));
    }

    @Test
    void testFindAllActiveReservations_AsList_ReturnsOnlyActiveReservations() {
        // Act
        List<Reservation> result = reservationRepository.findAllActiveReservations();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(reservation -> reservation.getIsDeleted() == null));
    }

    @Test
    void testFindByReservationsByClient_ReturnsClientReservations() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Reservation> result = reservationRepository.findByReservationsByClient(
            testClient.getClientId().intValue(), pageable);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getTotalElements()); // Only active reservations
        assertTrue(result.getContent().stream()
            .allMatch(r -> r.getClient().getClientId().equals(testClient.getClientId())));
        assertTrue(result.getContent().stream().allMatch(r -> r.getIsDeleted() == null));
    }

    @Test
    void testFindByReservationsByClient_NonExistentClient_ReturnsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Reservation> result = reservationRepository.findByReservationsByClient(999, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindById_ReturnsReservation() {
        // Act
        Reservation found = reservationRepository.findById(activeReservation1.getReservationId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(activeReservation1.getReservationId(), found.getReservationId());
        assertEquals("BK001", found.getBookingRef());
        assertEquals("Paris", found.getDestination());
    }

    @Test
    void testSaveReservation_Success() {
        // Arrange
        Reservation newReservation = new Reservation();
        newReservation.setClient(testClient);
        newReservation.setProvider(testProvider);
        newReservation.setBookingRef("BK004");
        newReservation.setBookedDate(LocalDate.now());
        newReservation.setDepartureDate(LocalDate.now().plusDays(20));
        newReservation.setReturnDate(LocalDate.now().plusDays(27));
        newReservation.setDestination("Berlin");
        newReservation.setHotel("Hotel Berlin");
        newReservation.setRoomNo(1);
        newReservation.setTransport("Flight");
        newReservation.setPrice(800.0);
        newReservation.setReceipted(200.0);
        newReservation.setBalance(600.0);
        newReservation.setPaymentDueDate(LocalDate.now().plusDays(10));
        newReservation.setCurrency("EUR");
        newReservation.setIsDeleted(null);

        // Act
        Reservation saved = reservationRepository.save(newReservation);

        // Assert
        assertNotNull(saved.getReservationId());
        assertEquals("BK004", saved.getBookingRef());
        assertEquals("Berlin", saved.getDestination());

        // Verify it can be found
        Reservation found = reservationRepository.findById(saved.getReservationId()).orElse(null);
        assertNotNull(found);
        assertEquals("Berlin", found.getDestination());
    }

    @Test
    void testUpdateReservation_Success() {
        // Arrange
        Reservation reservation = reservationRepository.findById(activeReservation1.getReservationId()).orElseThrow();
        String originalDestination = reservation.getDestination();

        // Act
        reservation.setDestination("Updated Paris");
        reservation.setPrice(1300.0);
        reservationRepository.save(reservation);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Reservation updated = reservationRepository.findById(activeReservation1.getReservationId()).orElseThrow();
        assertEquals("Updated Paris", updated.getDestination());
        assertEquals(1300.0, updated.getPrice());
        assertNotEquals(originalDestination, updated.getDestination());
    }

    @Test
    void testSoftDelete_ReservationNotReturnedInActiveSearch() {
        // Arrange
        Reservation reservation = reservationRepository.findById(activeReservation1.getReservationId()).orElseThrow();
        Pageable pageable = PageRequest.of(0, 10);

        // Verify reservation is returned before deletion
        Page<Reservation> beforeDelete = reservationRepository.findAllActiveReservations(pageable);
        assertEquals(2, beforeDelete.getTotalElements());

        // Act - Soft delete
        reservation.setIsDeleted(true);
        reservation.setDeletionDate(new Date());
        reservationRepository.save(reservation);
        entityManager.flush();
        entityManager.clear();

        // Assert - Reservation should not appear in active reservations
        Page<Reservation> afterDelete = reservationRepository.findAllActiveReservations(pageable);
        assertEquals(1, afterDelete.getTotalElements());
        assertFalse(afterDelete.getContent().stream()
            .anyMatch(r -> r.getReservationId().equals(activeReservation1.getReservationId())));
    }

    @Test
    void testFindReservationsWithReturnDate_ReturnsMatchingReservations() {
        // Arrange
        LocalDate targetDate = LocalDate.now().plusDays(37);

        // Act
        List<Reservation> result = reservationRepository.findReservationsWithReturnDate(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BK001", result.get(0).getBookingRef());
        assertEquals(targetDate, result.get(0).getReturnDate());
    }

    @Test
    void testFindReservationsWithReturnDate_ExcludesDeletedReservations() {
        // Arrange
        LocalDate targetDate = LocalDate.now().minusDays(3); // Deleted reservation's return date

        // Act
        List<Reservation> result = reservationRepository.findReservationsWithReturnDate(targetDate);

        // Assert - Should not include deleted reservation
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindReservationsWithDepartureDate_ReturnsMatchingReservations() {
        // Arrange
        LocalDate targetDate = LocalDate.now().plusDays(30);

        // Act
        List<Reservation> result = reservationRepository.findReservationsWithDepartureDate(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BK001", result.get(0).getBookingRef());
        assertEquals(targetDate, result.get(0).getDepartureDate());
    }

    @Test
    void testFindReservationsWithPaymentDueDate_ReturnsMatchingReservations() {
        // Arrange
        LocalDate targetDate = LocalDate.now().plusDays(15);

        // Act
        List<Reservation> result = reservationRepository.findReservationsWithPaymentDueDate(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("BK001", result.get(0).getBookingRef());
        assertEquals(targetDate, result.get(0).getPaymentDueDate());
    }

    @Test
    void testFindReservationsWithPaymentDueDate_NoMatch_ReturnsEmpty() {
        // Arrange
        LocalDate targetDate = LocalDate.now().minusDays(100); // Date in the past with no matches

        // Act
        List<Reservation> result = reservationRepository.findReservationsWithPaymentDueDate(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }
}
