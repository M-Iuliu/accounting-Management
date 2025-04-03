package com.accounting.service.offers;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Offer;
import com.accounting.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OfferServiceHelper {

    @Autowired
    private ClientRepository clientRepository;

    public OfferDTO mapOfferToOfferDTO(Offer input) {
        OfferDTO output = new OfferDTO();

        output.setOfferId(input.getOfferId());
        output.setClient(input.getClient());
        output.setAdultsNb(input.getAdultsNb());
        output.setChildrenNb(input.getChildrenNb());
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

    public Offer mapOfferFormToOffer(OfferForm input){
        Offer output = new Offer();

        Client client = clientRepository.findById(input.getClientId()).orElse(null);
        output.setClient(client);
        output.setOfferDate(input.getOfferDate());
        output.setAdultsNb(input.getAdultsNb());
        output.setChildrenNb(input.getChildrenNb());
        output.setDestination(input.getDestination());
        output.setPeriod(input.getPeriod());
        output.setBudget(input.getBudget());
        output.setGrossPrice(input.getGrossPrice());
        output.setAdvance(input.getAdvance());
        output.setCommission(input.getCommission());
        output.setAcquisitionPrice(input.getAcquisitionPrice());
        output.setStatus(OfferStatusEnum.valueOf(input.getStatus().toUpperCase()));
        output.setObs(input.getObs());

        return output;
    }

}
