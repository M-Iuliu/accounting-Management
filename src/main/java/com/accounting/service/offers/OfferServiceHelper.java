package com.accounting.service.offers;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferShortDTO;
import com.accounting.entity.Offer;
import com.accounting.mapper.OfferMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Helper component for Offer service operations.
 * Provides utility methods and delegates to OfferMapper for entity-DTO conversions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OfferServiceHelper {

    private final OfferMapper offerMapper;

    /**
     * Maps Offer entity to OfferShortDTO
     * @param offer the offer entity
     * @return OfferShortDTO representation
     */
    public OfferShortDTO mapOfferToOfferShortDTO(Offer offer) {
        log.debug("Mapping Offer entity to ShortDTO for offer ID: {}", offer.getOfferId());
        return offerMapper.toShortDTO(offer);
    }

    /**
     * Maps Offer entity to OfferDTO
     * @param offer the offer entity
     * @return OfferDTO representation
     */
    public OfferDTO mapOfferToOfferDTO(Offer offer) {
        log.debug("Mapping Offer entity to DTO for offer ID: {}", offer.getOfferId());
        return offerMapper.toDTO(offer);
    }

    /**
     * Validates offer business rules before save/update
     * @param offer the offer to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateOffer(Offer offer) {
        log.debug("Validating offer: {}", offer.getOfferId());

        if (offer.getAdultsNo() + offer.getChildrenNo() == 0) {
            throw new IllegalArgumentException("Offer must have at least one adult or child");
        }

        if (offer.getGrossPrice() < offer.getAcquisitionPrice()) {
            throw new IllegalArgumentException("Gross price cannot be less than acquisition price");
        }

        if (offer.getAdvance() > offer.getGrossPrice()) {
            throw new IllegalArgumentException("Advance cannot exceed gross price");
        }

        log.debug("Offer validation successful");
    }
}
