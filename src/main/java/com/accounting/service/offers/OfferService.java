package com.accounting.service.offers;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferPatchDTO;
import com.accounting.dto.offer.OfferUpdateStatusDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.entity.Offer;
import com.accounting.exeption.ClientNotFoundException;

public interface OfferService {

    OfferDTO createOffer(OfferForm offerCreateForm) throws ClientNotFoundException;

    Offer findOfferById(Long id);

    OfferDTO getOfferDTOById(Long id);

    PageDTO getOffers(String input, int page, int size);

    OfferDTO patchOffer(Long id, OfferPatchDTO patchDTO);

    void updateOfferStatus(Long id, OfferUpdateStatusDTO updates) throws ClientNotFoundException;

    void deleteOfferById(Long id);

}
