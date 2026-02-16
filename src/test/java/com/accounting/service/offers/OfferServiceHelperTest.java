package com.accounting.service.offers;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferShortDTO;
import com.accounting.entity.Offer;
import com.accounting.mapper.OfferMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceHelperTest {

    @Mock
    private OfferMapper offerMapper;

    @InjectMocks
    private OfferServiceHelper offerServiceHelper;

    private Offer testOffer;
    private OfferDTO testOfferDTO;
    private OfferShortDTO testOfferShortDTO;

    @BeforeEach
    void setUp() {
        testOffer = new Offer();
        testOffer.setOfferId(1L);
        testOffer.setDestination("Paris");
        testOffer.setAdultsNo(2);
        testOffer.setChildrenNo(1);
        testOffer.setGrossPrice(1000.0);
        testOffer.setAcquisitionPrice(800.0);
        testOffer.setAdvance(200.0);

        testOfferDTO = new OfferDTO();
        testOfferDTO.setOfferId(1L);
        testOfferDTO.setDestination("Paris");

        testOfferShortDTO = new OfferShortDTO();
        testOfferShortDTO.setOfferId(1L);
        testOfferShortDTO.setDestination("Paris");
    }

    @Test
    void testMapOfferToOfferDTO_Success() {
        // Arrange
        when(offerMapper.toDTO(testOffer)).thenReturn(testOfferDTO);

        // Act
        OfferDTO result = offerServiceHelper.mapOfferToOfferDTO(testOffer);

        // Assert
        assertNotNull(result);
        assertEquals(testOfferDTO.getOfferId(), result.getOfferId());
        assertEquals(testOfferDTO.getDestination(), result.getDestination());
        verify(offerMapper, times(1)).toDTO(testOffer);
    }

    @Test
    void testMapOfferToOfferShortDTO_Success() {
        // Arrange
        when(offerMapper.toShortDTO(testOffer)).thenReturn(testOfferShortDTO);

        // Act
        OfferShortDTO result = offerServiceHelper.mapOfferToOfferShortDTO(testOffer);

        // Assert
        assertNotNull(result);
        assertEquals(testOfferShortDTO.getOfferId(), result.getOfferId());
        assertEquals(testOfferShortDTO.getDestination(), result.getDestination());
        verify(offerMapper, times(1)).toShortDTO(testOffer);
    }

    @Test
    void testValidateOffer_Success() {
        // Act & Assert
        assertDoesNotThrow(() -> offerServiceHelper.validateOffer(testOffer));
    }

    @Test
    void testValidateOffer_NoParticipants_ThrowsException() {
        // Arrange
        testOffer.setAdultsNo(0);
        testOffer.setChildrenNo(0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> offerServiceHelper.validateOffer(testOffer)
        );
        assertEquals("Offer must have at least one adult or child", exception.getMessage());
    }

    @Test
    void testValidateOffer_GrossPriceLessThanAcquisitionPrice_ThrowsException() {
        // Arrange
        testOffer.setGrossPrice(500.0);
        testOffer.setAcquisitionPrice(800.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> offerServiceHelper.validateOffer(testOffer)
        );
        assertEquals("Gross price cannot be less than acquisition price", exception.getMessage());
    }

    @Test
    void testValidateOffer_AdvanceExceedsGrossPrice_ThrowsException() {
        // Arrange
        testOffer.setGrossPrice(500.0);
        testOffer.setAdvance(600.0);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> offerServiceHelper.validateOffer(testOffer)
        );
        assertEquals("Advance cannot exceed gross price", exception.getMessage());
    }

    @Test
    void testValidateOffer_OnlyAdults_Success() {
        // Arrange
        testOffer.setAdultsNo(2);
        testOffer.setChildrenNo(0);

        // Act & Assert
        assertDoesNotThrow(() -> offerServiceHelper.validateOffer(testOffer));
    }

    @Test
    void testValidateOffer_OnlyChildren_Success() {
        // Arrange
        testOffer.setAdultsNo(0);
        testOffer.setChildrenNo(3);

        // Act & Assert
        assertDoesNotThrow(() -> offerServiceHelper.validateOffer(testOffer));
    }

    @Test
    void testValidateOffer_ZeroAdvance_Success() {
        // Arrange
        testOffer.setAdvance(0.0);

        // Act & Assert
        assertDoesNotThrow(() -> offerServiceHelper.validateOffer(testOffer));
    }

    @Test
    void testValidateOffer_AdvanceEqualsGrossPrice_Success() {
        // Arrange
        testOffer.setGrossPrice(1000.0);
        testOffer.setAdvance(1000.0);

        // Act & Assert
        assertDoesNotThrow(() -> offerServiceHelper.validateOffer(testOffer));
    }
}
