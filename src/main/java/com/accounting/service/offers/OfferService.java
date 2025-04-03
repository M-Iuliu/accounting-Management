package com.accounting.service.offers;

import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.entity.Offer;

import java.util.Map;

public interface OfferService {

    PageDTO getOffers(int page, int size);
    OfferDTO getOfferByFilters(String input);
    OfferDTO createOffer(OfferForm offerCreateForm);
    Object editOffer(OfferForm offerform);
    public Offer findById(Long id);
    OfferDTO patchOffer(Long id, Map<String, Object> updates);
    void deleteOfferById(Long id);
}
