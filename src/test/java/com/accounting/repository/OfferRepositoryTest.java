package com.accounting.repository;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.entity.Client;
import com.accounting.entity.Offer;
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
 * Integration tests for OfferRepository using real database operations.
 * Tests the critical bug fix: deleted offers should not appear in search results.
 */
@DataJpaTest
class OfferRepositoryTest {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Client testClient;
    private Offer activeOffer1;
    private Offer activeOffer2;
    private Offer deletedOffer;
    private Offer offeredStatusOffer;

    @BeforeEach
    void setUp() {
        // Clear any existing data
        offerRepository.deleteAll();
        clientRepository.deleteAll();

        // Create test client
        testClient = new Client();
        testClient.setName("John");
        testClient.setSurname("Doe");
        testClient.setEmail("john.doe@example.com");
        testClient.setTelephone("1234567890");
        testClient.setIsDeleted(false);
        testClient = clientRepository.save(testClient);

        // Create active offer 1
        activeOffer1 = new Offer();
        activeOffer1.setClient(testClient);
        activeOffer1.setOfferDate(LocalDate.now());
        activeOffer1.setDestination("Paris");
        activeOffer1.setPeriod("Summer 2025");
        activeOffer1.setCurrency("EUR");
        activeOffer1.setBudget(1000.0);
        activeOffer1.setGrossPrice(1200.0);
        activeOffer1.setAdvance(300.0);
        activeOffer1.setCommission(100.0);
        activeOffer1.setAcquisitionPrice(1100.0);
        activeOffer1.setAdultsNo(2);
        activeOffer1.setChildrenNo(1);
        activeOffer1.setStatus(OfferStatusEnum.CASTIGAT);
        activeOffer1.setIsDeleted(null);

        // Create active offer 2
        activeOffer2 = new Offer();
        activeOffer2.setClient(testClient);
        activeOffer2.setOfferDate(LocalDate.now().minusDays(5));
        activeOffer2.setDestination("Rome");
        activeOffer2.setPeriod("Winter 2025");
        activeOffer2.setCurrency("EUR");
        activeOffer2.setBudget(800.0);
        activeOffer2.setGrossPrice(900.0);
        activeOffer2.setAdvance(200.0);
        activeOffer2.setCommission(80.0);
        activeOffer2.setAcquisitionPrice(820.0);
        activeOffer2.setAdultsNo(2);
        activeOffer2.setChildrenNo(0);
        activeOffer2.setStatus(OfferStatusEnum.OFERTAT);
        activeOffer2.setIsDeleted(null);

        // Create deleted offer
        deletedOffer = new Offer();
        deletedOffer.setClient(testClient);
        deletedOffer.setOfferDate(LocalDate.now().minusDays(10));
        deletedOffer.setDestination("London");
        deletedOffer.setPeriod("Spring 2025");
        deletedOffer.setCurrency("GBP");
        deletedOffer.setBudget(1500.0);
        deletedOffer.setGrossPrice(1600.0);
        deletedOffer.setAdvance(400.0);
        deletedOffer.setCommission(120.0);
        deletedOffer.setAcquisitionPrice(1480.0);
        deletedOffer.setAdultsNo(2);
        deletedOffer.setChildrenNo(0);
        deletedOffer.setStatus(OfferStatusEnum.PIERDUT);
        deletedOffer.setIsDeleted(true);
        deletedOffer.setDeletionDate(new Date());

        // Create offer with OFERTAT status for testing findOffersOlderThan
        offeredStatusOffer = new Offer();
        offeredStatusOffer.setClient(testClient);
        offeredStatusOffer.setOfferDate(LocalDate.now().minusDays(8));
        offeredStatusOffer.setDestination("Barcelona");
        offeredStatusOffer.setPeriod("Fall 2025");
        offeredStatusOffer.setCurrency("EUR");
        offeredStatusOffer.setBudget(700.0);
        offeredStatusOffer.setGrossPrice(800.0);
        offeredStatusOffer.setAdvance(150.0);
        offeredStatusOffer.setCommission(70.0);
        offeredStatusOffer.setAcquisitionPrice(730.0);
        offeredStatusOffer.setAdultsNo(1);
        offeredStatusOffer.setChildrenNo(0);
        offeredStatusOffer.setStatus(OfferStatusEnum.OFERTAT);
        offeredStatusOffer.setIsDeleted(null);
        offeredStatusOffer.setDeletionDate(null);

        // Persist test data
        entityManager.persist(activeOffer1);
        entityManager.persist(activeOffer2);
        entityManager.persist(deletedOffer);
        entityManager.persist(offeredStatusOffer);
        entityManager.flush();
    }

    @Test
    void testFindAllActiveOffers_ReturnsOnlyActiveOffers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Offer> result = offerRepository.findAllActiveOffers(pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements()); // activeOffer1, activeOffer2, offeredStatusOffer
        assertTrue(result.getContent().stream().allMatch(offer -> offer.getIsDeleted() == null));
    }

    @Test
    void testFindByFilter_WithClientName_ReturnsActiveOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act - Search for "John" (matches client name)
        Page<Offer> result = offerRepository.findByFilter("John", pageable);

        // Assert - Should return all active offers for this client, NOT deleted ones
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertTrue(result.getContent().stream().noneMatch(offer -> offer.getIsDeleted() != null && offer.getIsDeleted()));
    }

    @Test
    void testFindByFilter_WithClientSurname_ReturnsActiveOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Offer> result = offerRepository.findByFilter("Doe", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(offer -> offer.getIsDeleted() == null));
    }

    @Test
    void testFindByFilter_WithTelephone_ReturnsActiveOnly() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Offer> result = offerRepository.findByFilter("1234567890", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(offer -> offer.getIsDeleted() == null));
    }

    @Test
    void testFindByFilter_NoMatch_ReturnsEmpty() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Offer> result = offerRepository.findByFilter("NonExistent", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
    }

    @Test
    void testFindByFilter_CaseInsensitive_ReturnsResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Offer> result = offerRepository.findByFilter("john", pageable);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());
    }

    @Test
    void testFindById_ReturnsOffer() {
        // Act
        Offer found = offerRepository.findById(activeOffer1.getOfferId()).orElse(null);

        // Assert
        assertNotNull(found);
        assertEquals(activeOffer1.getOfferId(), found.getOfferId());
        assertEquals("Paris", found.getDestination());
        assertEquals(OfferStatusEnum.CASTIGAT, found.getStatus());
    }

    @Test
    void testSaveOffer_Success() {
        // Arrange
        Offer newOffer = new Offer();
        newOffer.setClient(testClient);
        newOffer.setOfferDate(LocalDate.now());
        newOffer.setDestination("Berlin");
        newOffer.setPeriod("Summer 2025");
        newOffer.setCurrency("EUR");
        newOffer.setBudget(1000.0);
        newOffer.setGrossPrice(1100.0);
        newOffer.setAdvance(250.0);
        newOffer.setCommission(90.0);
        newOffer.setAcquisitionPrice(1010.0);
        newOffer.setAdultsNo(2);
        newOffer.setChildrenNo(1);
        newOffer.setStatus(OfferStatusEnum.OFERTAT);
        newOffer.setIsDeleted(null);

        // Act
        Offer saved = offerRepository.save(newOffer);

        // Assert
        assertNotNull(saved.getOfferId());
        assertEquals("Berlin", saved.getDestination());

        // Verify it can be found
        Offer found = offerRepository.findById(saved.getOfferId()).orElse(null);
        assertNotNull(found);
        assertEquals("Berlin", found.getDestination());
    }

    @Test
    void testUpdateOffer_Success() {
        // Arrange
        Offer offer = offerRepository.findById(activeOffer1.getOfferId()).orElseThrow();
        String originalDestination = offer.getDestination();

        // Act
        offer.setDestination("Updated Paris");
        offer.setStatus(OfferStatusEnum.OFERTAT);
        offerRepository.save(offer);
        entityManager.flush();
        entityManager.clear();

        // Assert
        Offer updated = offerRepository.findById(activeOffer1.getOfferId()).orElseThrow();
        assertEquals("Updated Paris", updated.getDestination());
        assertEquals(OfferStatusEnum.OFERTAT, updated.getStatus());
        assertNotEquals(originalDestination, updated.getDestination());
    }

    @Test
    void testSoftDelete_OfferNotReturnedInActiveSearch() {
        // Arrange
        Offer offer = offerRepository.findById(activeOffer1.getOfferId()).orElseThrow();
        Pageable pageable = PageRequest.of(0, 10);

        // Verify offer is returned before deletion
        Page<Offer> beforeDelete = offerRepository.findAllActiveOffers(pageable);
        assertEquals(3, beforeDelete.getTotalElements());

        // Act - Soft delete
        offer.setIsDeleted(true);
        offer.setDeletionDate(new Date());
        offerRepository.save(offer);
        entityManager.flush();
        entityManager.clear();

        // Assert - Offer should not appear in active offers
        Page<Offer> afterDelete = offerRepository.findAllActiveOffers(pageable);
        assertEquals(2, afterDelete.getTotalElements());
        assertFalse(afterDelete.getContent().stream()
            .anyMatch(o -> o.getOfferId().equals(activeOffer1.getOfferId())));
    }

    @Test
    void testFindOffersOlderThan_ReturnsOffersWithOFERTATStatus() {
        // Arrange
        LocalDate targetDate = LocalDate.now().minusDays(8);

        // Act
        List<Offer> result = offerRepository.findOffersOlderThan(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Barcelona", result.get(0).getDestination());
        assertEquals(OfferStatusEnum.OFERTAT, result.get(0).getStatus());
        assertNull(result.get(0).getDeletionDate());
    }

    @Test
    void testFindOffersOlderThan_NoMatch_ReturnsEmpty() {
        // Arrange
        LocalDate targetDate = LocalDate.now().plusDays(10); // Future date

        // Act
        List<Offer> result = offerRepository.findOffersOlderThan(targetDate);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    void testFindOffersOlderThan_ExcludesDeletedOffers() {
        // Arrange
        LocalDate targetDate = LocalDate.now().minusDays(8);

        // Act
        List<Offer> result = offerRepository.findOffersOlderThan(targetDate);

        // Assert - Should not include deleted offers even if they match the date
        assertNotNull(result);
        assertTrue(result.stream().noneMatch(offer -> offer.getIsDeleted() != null && offer.getIsDeleted()));
    }
}
