package com.accounting.service.offers;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferPatchDTO;
import com.accounting.dto.offer.OfferShortDTO;
import com.accounting.entity.Offer;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class OfferServiceHelper {

    public OfferShortDTO mapOfferToOfferShortDTO(Offer input) {
        OfferShortDTO output = new OfferShortDTO();

        output.setOfferId(input.getOfferId());
        output.setDestination(input.getDestination());
        output.setPeriod(input.getPeriod());
        output.setBudget(input.getBudget());
        output.setStatus(input.getStatus());
        //TODO: same notification/obs system as in reservation?
        output.setObs(input.getObs());
        return output;
    }

    public OfferDTO mapOfferToOfferDTO(Offer input) {
        OfferDTO output = new OfferDTO();

        output.setOfferId(input.getOfferId());
        output.setAdultsNb(input.getAdultsNo());
        output.setChildrenNb(input.getChildrenNo());
        output.setDestination(input.getDestination());
        output.setPeriod(input.getPeriod());
        output.setBudget(input.getBudget());
        output.setGrossPrice(input.getGrossPrice());
        output.setAdvance(input.getAdvance());
        output.setCommission(input.getCommission());
        output.setAcquisitionPrice(input.getAcquisitionPrice());
        output.setStatus(input.getStatus());
        output.setObs(input.getObs());

        return output;
    }

    public static void patchOffer(OfferPatchDTO patch, Offer offer) {

        if (patch.getAdultsNb() != null) offer.setAdultsNo(patch.getAdultsNb());
        if (patch.getChildrenNb() != null) offer.setChildrenNo(patch.getChildrenNb());
        if (patch.getDestination() != null) offer.setDestination(patch.getDestination());
        if (patch.getPeriod() != null) offer.setPeriod(patch.getPeriod());
        if (patch.getBudget() != null) offer.setBudget(patch.getBudget());
        if (patch.getGrossPrice() != null) offer.setGrossPrice(patch.getGrossPrice());
        if (patch.getAdvance() != null) offer.setAdvance(patch.getAdvance());
        if (patch.getCommission() != null) offer.setCommission(patch.getCommission());
        if (patch.getAcquisitionPrice() != null) offer.setAcquisitionPrice(patch.getAcquisitionPrice());
    }


    public Offer mapOfferFormToOffer(OfferForm input){
        Offer output = new Offer();
        output.setOfferDate(input.getOfferDate() != null ? input.getOfferDate() : new Date());
        output.setAdultsNo(input.getAdultsNb());
        output.setChildrenNo(input.getChildrenNb());
        output.setDestination(input.getDestination());
        output.setPeriod(input.getPeriod());
        output.setBudget(input.getBudget());
        output.setGrossPrice(input.getGrossPrice());
        output.setAdvance(input.getAdvance());
        output.setCommission(input.getCommission());
        output.setAcquisitionPrice(input.getAcquisitionPrice());
        output.setObs(input.getObs());

        return output;
    }

}
