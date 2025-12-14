package com.accounting.repository;

import com.accounting.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ReservationFileRepository using real database operations.
 */
@DataJpaTest
class ReservationFileRepositoryTest {

    @Autowired
    private ReservationFileRepository reservationFileRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient;
    private Provider testProvider;
    private Reservation testReservation1;
    private Reservation testReservation2;
    private ReservationFile file1;
    private ReservationFile file2;
    private ReservationFile file3;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        reservationFileRepository.deleteAll();
        reservationRepository.deleteAll();
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

        // Create test reservation 1
        testReservation1 = new Reservation();
        testReservation1.setClient(testClient);
        testReservation1.setProvider(testProvider);
        testReservation1.setBookingRef("BK001");
        testReservation1.setBookedDate(LocalDate.now());
        testReservation1.setDepartureDate(LocalDate.now().plusDays(30));
        testReservation1.setReturnDate(LocalDate.now().plusDays(37));
        testReservation1.setDestination("Paris");
        testReservation1.setHotel("Hotel Paris");
        testReservation1.setRoomNo(2);
        testReservation1.setTransport("Flight");
        testReservation1.setPrice(1200.0);
        testReservation1.setReceipted(300.0);
        testReservation1.setBalance(900.0);
        testReservation1.setPaymentDueDate(LocalDate.now().plusDays(15));
        testReservation1.setCurrency("EUR");
        testReservation1.setIsDeleted(null);
        testReservation1 = reservationRepository.save(testReservation1);

        // Create test reservation 2
        testReservation2 = new Reservation();
        testReservation2.setClient(testClient);
        testReservation2.setProvider(testProvider);
        testReservation2.setBookingRef("BK002");
        testReservation2.setBookedDate(LocalDate.now());
        testReservation2.setDepartureDate(LocalDate.now().plusDays(60));
        testReservation2.setReturnDate(LocalDate.now().plusDays(67));
        testReservation2.setDestination("Rome");
        testReservation2.setHotel("Hotel Rome");
        testReservation2.setRoomNo(1);
        testReservation2.setTransport("Train");
        testReservation2.setPrice(900.0);
        testReservation2.setReceipted(450.0);
        testReservation2.setBalance(450.0);
        testReservation2.setPaymentDueDate(LocalDate.now().plusDays(45));
        testReservation2.setCurrency("EUR");
        testReservation2.setIsDeleted(null);
        testReservation2 = reservationRepository.save(testReservation2);

        // Create reservation files for reservation 1
        file1 = new ReservationFile();
        file1.setReservation(testReservation1);
        file1.setFileName("ticket_001.pdf");

        file2 = new ReservationFile();
        file2.setReservation(testReservation1);
        file2.setFileName("confirmation_001.pdf");

        // Create reservation file for reservation 2
        file3 = new ReservationFile();
        file3.setReservation(testReservation2);
        file3.setFileName("ticket_002.pdf");

        // Persist test data
        entityManager.persist(file1);
        entityManager.persist(file2);
        entityManager.persist(file3);
        entityManager.flush();
    }

    @Test
    void testFindByReservationId_ReturnsFiles() {
        // Act
        List<ReservationFile> result = reservationFileRepository.findByReservationId(
            testReservation1.getReservationId());

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(file -> file.getFileName().equals("ticket_001.pdf")));
        assertTrue(result.stream().anyMatch(file -> file.getFileName().equals("confirmation_001.pdf")));
    }

    @Test
    void testFindByReservationId_SingleFile_ReturnsOne() {
        // Act
        List<ReservationFile> result = reservationFileRepository.findByReservationId(
            testReservation2.getReservationId());

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("ticket_002.pdf", result.get(0).getFileName());
    }

    @Test
    void testFindByReservationId_NoFiles_ReturnsEmpty() {
        // Arrange - Create a new reservation without files
        Reservation emptyReservation = new Reservation();
        emptyReservation.setClient(testClient);
        emptyReservation.setProvider(testProvider);
        emptyReservation.setBookingRef("BK003");
        emptyReservation.setBookedDate(LocalDate.now());
        emptyReservation.setDepartureDate(LocalDate.now().plusDays(90));
        emptyReservation.setReturnDate(LocalDate.now().plusDays(97));
        emptyReservation.setDestination("Berlin");
        emptyReservation.setHotel("Hotel Berlin");
        emptyReservation.setRoomNo(1);
        emptyReservation.setTransport("Flight");
        emptyReservation.setPrice(800.0);
        emptyReservation.setReceipted(200.0);
        emptyReservation.setBalance(600.0);
        emptyReservation.setPaymentDueDate(LocalDate.now().plusDays(75));
        emptyReservation.setCurrency("EUR");
        emptyReservation.setIsDeleted(null);
        emptyReservation = reservationRepository.save(emptyReservation);

        // Act
        List<ReservationFile> result = reservationFileRepository.findByReservationId(
            emptyReservation.getReservationId());

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindByReservationId_NonExistentReservation_ReturnsEmpty() {
        // Act
        List<ReservationFile> result = reservationFileRepository.findByReservationId(999L);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindByReservationIdAndFileName_ReturnsFile() {
        // Act
        ReservationFile result = reservationFileRepository.findByReservationIdAndFileName(
            testReservation1.getReservationId(), "ticket_001.pdf");

        // Assert
        assertNotNull(result);
        assertEquals("ticket_001.pdf", result.getFileName());
        assertEquals(testReservation1.getReservationId(), result.getReservation().getReservationId());
    }

    @Test
    void testFindByReservationIdAndFileName_DifferentFileName_ReturnsNull() {
        // Act
        ReservationFile result = reservationFileRepository.findByReservationIdAndFileName(
            testReservation1.getReservationId(), "nonexistent.pdf");

        // Assert
        assertNull(result);
    }

    @Test
    void testFindByReservationIdAndFileName_DifferentReservation_ReturnsNull() {
        // Act
        ReservationFile result = reservationFileRepository.findByReservationIdAndFileName(
            testReservation2.getReservationId(), "ticket_001.pdf");

        // Assert
        assertNull(result);
    }

    @Test
    void testFindById_ReturnsFile() {
        // Act
        ReservationFile found = reservationFileRepository.findById(file1.getFileId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(file1.getFileId(), found.getFileId());
        assertEquals("ticket_001.pdf", found.getFileName());
        assertEquals(testReservation1.getReservationId(), found.getReservation().getReservationId());
    }

    @Test
    void testSaveReservationFile_Success() {
        // Arrange
        ReservationFile newFile = new ReservationFile();
        newFile.setReservation(testReservation1);
        newFile.setFileName("invoice_001.pdf");

        // Act
        ReservationFile saved = reservationFileRepository.save(newFile);

        // Assert
        assertNotNull(saved.getFileId());
        assertEquals("invoice_001.pdf", saved.getFileName());

        // Verify it can be found
        ReservationFile found = reservationFileRepository.findById(saved.getFileId()).orElse(null);
        assertNotNull(found);
        assertEquals("invoice_001.pdf", found.getFileName());

        // Verify it's included in reservation files
        List<ReservationFile> reservationFiles = reservationFileRepository.findByReservationId(
            testReservation1.getReservationId());
        assertEquals(3, reservationFiles.size());
    }

    @Test
    void testUpdateReservationFile_Success() {
        // Arrange
        ReservationFile file = reservationFileRepository.findById(file1.getFileId()).orElseThrow();
        String originalFileName = file.getFileName();

        // Act
        file.setFileName("updated_ticket_001.pdf");
        reservationFileRepository.save(file);
        entityManager.flush();
        entityManager.clear();

        // Assert
        ReservationFile updated = reservationFileRepository.findById(file1.getFileId()).orElseThrow();
        assertEquals("updated_ticket_001.pdf", updated.getFileName());
        assertNotEquals(originalFileName, updated.getFileName());
    }

    @Test
    void testDeleteReservationFile_Success() {
        // Arrange
        Long fileId = file1.getFileId();

        // Verify file exists
        assertTrue(reservationFileRepository.findById(fileId).isPresent());

        // Verify reservation has 2 files
        List<ReservationFile> beforeDelete = reservationFileRepository.findByReservationId(
            testReservation1.getReservationId());
        assertEquals(2, beforeDelete.size());

        // Act
        reservationFileRepository.deleteById(fileId);
        entityManager.flush();

        // Assert
        assertFalse(reservationFileRepository.findById(fileId).isPresent());

        // Verify reservation now has 1 file
        List<ReservationFile> afterDelete = reservationFileRepository.findByReservationId(
            testReservation1.getReservationId());
        assertEquals(1, afterDelete.size());
        assertEquals("confirmation_001.pdf", afterDelete.get(0).getFileName());
    }

    @Test
    void testFindAll_ReturnsAllFiles() {
        // Act
        List<ReservationFile> allFiles = reservationFileRepository.findAll();

        // Assert
        assertNotNull(allFiles);
        assertEquals(3, allFiles.size());
    }
}
