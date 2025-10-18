package com.accounting.service.offers;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.dto.offer.*;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Offer;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.OfferStatusConflictException;
import com.accounting.repository.OfferRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.reservations.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OfferServiceImp implements OfferService{

    private final  OfferServiceHelper offerServiceHelper;
    private final OfferRepository offerRepository;
    private final ClientService clientService;
    private final ReservationService reservationService;

    public OfferServiceImp(OfferServiceHelper offerServiceHelper, OfferRepository offerRepository, ClientService clientService, ReservationService reservationService) {
        this.offerServiceHelper = offerServiceHelper;
        this.offerRepository = offerRepository;
        this.clientService = clientService;
        this.reservationService = reservationService;
    }

    public OfferDTO createOffer(OfferForm offerCreateForm) throws ClientNotFoundException {
        Offer offer = offerServiceHelper.mapOfferFormToOffer(offerCreateForm);
        offer.setClient(clientService.findById(offerCreateForm.getClientId()));
        offer.setStatus(OfferStatusEnum.OFERTAT);

        OfferDTO savedOfferDTO = offerServiceHelper.mapOfferToOfferDTO(offerRepository.save(offer));
        savedOfferDTO.setClient(clientService.mapToShortDto(offer.getClient()));
        return savedOfferDTO;
    }

    public Offer findOfferById(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Offer not found with ID: " + id));
    }

    public OfferDTO getOfferDTOById(Long id) {
        Offer offer = findOfferById(id);
        OfferDTO offerDTO = offerServiceHelper.mapOfferToOfferDTO(offer);
        offerDTO.setClient(clientService.mapToShortDto(offer.getClient()));
        return offerDTO;
    }

    public PageDTO getOffers(String input, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Offer> offerPage;
        if (input == null || input.isBlank()) {
            offerPage = offerRepository.findAllActiveOffers(pageable);
        } else {
            offerPage = offerRepository.findByFilter(input.trim(), pageable);
        }

        List<OfferDTO> offersList = offerPage.getContent()
                .stream()
                .map(offer -> {
                    OfferDTO offerDto = offerServiceHelper.mapOfferToOfferDTO(offer);
                    offerDto.setClient(clientService.mapToShortDto(offer.getClient()));
                    return offerDto;
                })
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                offerPage.getTotalElements(),
                offerPage.getSize(),
                List.of(5, 10, 20),
                offerPage.getNumber()
        );

        return new PageDTO<>(offersList, pagination);
    }

    public OfferDTO patchOffer(Long id, OfferPatchDTO patchDTO) {
        Offer offer = findOfferById(id);
        OfferServiceHelper.patchOffer(patchDTO, offer);

        OfferDTO editedOfferDTO = offerServiceHelper.mapOfferToOfferDTO(offerRepository.save(offer));
        editedOfferDTO.setClient(clientService.mapToShortDto(offer.getClient()));
        return editedOfferDTO;
    }

    //TODO: check if on upgrade to reservation requires to update the offer detail (persNo,... etc)
    public void updateOfferStatus(Long id, OfferUpdateStatusDTO updateDto) throws ClientNotFoundException {
        Offer offer = findOfferById(id);

        String newStatus = updateDto.getStatus().toUpperCase();
        String oldStatus = offer.getStatus().name().toUpperCase();

        boolean clientIsWon = newStatus.equals(OfferStatusEnum.CASTIGAT.name()) && oldStatus.equals(OfferStatusEnum.OFERTAT.name());
        boolean clientIsLost = newStatus.equals(OfferStatusEnum.PIERDUT.name()) && oldStatus.equals(OfferStatusEnum.OFERTAT.name());
        boolean clientIsBack = newStatus.equals(OfferStatusEnum.REVENIT.name()) && oldStatus.equals(OfferStatusEnum.PIERDUT.name());

        if (clientIsWon) {
            reservationService.createReservation(updateDto.getReservationDetails(), offer);
            offer.setAdvance(updateDto.getReservationDetails().getAdvance());
            offer.setStatus(OfferStatusEnum.CASTIGAT);

        } else if (clientIsLost) {
            offer.setStatus(OfferStatusEnum.PIERDUT);

        } else if (clientIsBack) {
            offer.setStatus(OfferStatusEnum.REVENIT);

        } else {
            throw new OfferStatusConflictException(
                    String.format("Offer with status: [%s] can't be updated to new status: [%s]", oldStatus, newStatus));
        }
        offerRepository.save(offer);
    }

    public void deleteOfferById(Long id) {
//        if (!offerRepository.existsById(id)) {
//            throw new EntityNotFoundException("Offer with id " + id + " not found");
//        }
//        offerRepository.deleteById(id);

//  We don't delete important entities (offer/reservation/client/provider ...)
//  because they might have dependencies with other entities

        Offer offer = findOfferById(id);
        offer.setStatus(OfferStatusEnum.PIERDUT); //TODO: [FE] announce on deletion that it also means setting the status PIERDUT
        offer.setIsDeleted(Boolean.TRUE);
        offer.setDeletionDate(new Date());
        offerRepository.save(offer);
    }

}
