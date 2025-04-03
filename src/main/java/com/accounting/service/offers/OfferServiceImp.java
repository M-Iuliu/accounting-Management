package com.accounting.service.offers;

import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Offer;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.OfferNotFoundException;
import com.accounting.repository.OfferRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OfferServiceImp implements OfferService{

    private final  OfferServiceHelper offerServiceHelper;
    private final OfferRepository offerRepository;

    public OfferServiceImp(OfferServiceHelper offerServiceHelper, OfferRepository offerRepository) {
        this.offerServiceHelper = offerServiceHelper;
        this.offerRepository = offerRepository;
    }

    public OfferDTO getOfferByFilters(String input) {
        Offer myOffer = offerRepository.findByFilter(input)
                .orElseThrow(() -> new OfferNotFoundException("Offer not found with given input: " + input));

        return offerServiceHelper.mapOfferToOfferDTO(myOffer);
    }

    public PageDTO getOffers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Offer> offerPage = offerRepository.findAll(pageable);

        List<OfferDTO> offersList = offerPage.getContent()
                .stream()
                .map(offerServiceHelper::mapOfferToOfferDTO)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                offerPage.getTotalElements(),
                offerPage.getSize(),
                List.of(5, 10, 20),
                offerPage.getNumber()
        );

        return new PageDTO<>(offersList, pagination);
    }

    public Offer findById(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with ID: " + id));
    }

    public OfferDTO patchOffer(Long id, Map<String, Object> updates) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new OfferNotFoundException("Offer not found with id: " + id));

        Set<String> inputKeys = updates.keySet();
        Set<String> offerKey = Arrays.stream(Offer.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        for (String key : inputKeys){
            if (offerKey.contains(key)){
                try {
                    Method setter = Offer.class.getMethod("set" + Character.toUpperCase(key.charAt(0)) + key.substring(1), String.class);

                    setter.invoke(offer, updates.get(key));
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException("Failed to set field: " + key, e);
                }
            }
        }

        offerRepository.save(offer);

        return offerServiceHelper.mapOfferToOfferDTO(offer);
    }

    public OfferDTO createOffer(OfferForm offerCreateForm) {
        Offer offer = offerServiceHelper.mapOfferFormToOffer(offerCreateForm);
        offerRepository.save(offer);
        return offerServiceHelper.mapOfferToOfferDTO(offer);
    }

    public OfferDTO editOffer(OfferForm offerEditForm) {
        Offer offer = offerServiceHelper.mapOfferFormToOffer(offerEditForm);
        offerRepository.save(offer);
        return offerServiceHelper.mapOfferToOfferDTO(offer);
    }

    public void deleteOfferById(Long id) {
        if (!offerRepository.existsById(id)) {
            throw new EntityNotFoundException("Offer with id " + id + " not found");
        }
        offerRepository.deleteById(id);
    }

}
