package com.accounting.service.offers;

import com.accounting.constants.OfferStatusEnum;
import com.accounting.dto.offer.*;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.entity.Offer;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.OfferStatusConflictException;
import com.accounting.mapper.OfferMapper;
import com.accounting.repository.OfferRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.reservations.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

/**
 * Service implementation for Offer domain operations.
 * Handles business logic, transaction management, and orchestrates
 * interactions between controller, repository, and mapper layers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OfferServiceImp implements OfferService {

    private final OfferServiceHelper offerServiceHelper;
    private final OfferRepository offerRepository;
    private final ClientService clientService;
    private final ReservationService reservationService;
    private final OfferMapper offerMapper;

    /**
     * Creates a new offer
     * @param offerCreateForm the form data for new offer
     * @return the created offer as DTO
     * @throws ClientNotFoundException if client not found
     */
    @Override
    @Transactional
    public OfferDTO createOffer(OfferForm offerCreateForm) throws ClientNotFoundException {
        log.info("Creating new offer for client ID: {}, destination: {}",
            offerCreateForm.getClientId(), offerCreateForm.getDestination());

        Offer offer = offerMapper.toEntity(offerCreateForm);
        if (offer.getOfferDate() == null) {
            offer.setOfferDate(LocalDate.now());
        }
        offer.setClient(clientService.findById(offerCreateForm.getClientId()));
        offer.setStatus(OfferStatusEnum.OFERTAT);

        offerServiceHelper.validateOffer(offer);
        offer = offerRepository.save(offer);

        OfferDTO savedOfferDTO = offerServiceHelper.mapOfferToOfferDTO(offer);
        savedOfferDTO.setClient(clientService.mapToShortDto(offer.getClient()));

        log.info("Successfully created offer with ID: {}", offer.getOfferId());
        return savedOfferDTO;
    }

    /**
     * Finds an offer by ID
     * @param id the offer ID
     * @return the found offer
     * @throws IllegalArgumentException if offer not found
     */
    @Override
    @Transactional(readOnly = true)
    public Offer findOfferById(Long id) {
        log.info("Finding offer by ID: {}", id);

        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Offer not found with ID: {}", id);
                    return new IllegalArgumentException("Offer not found with ID: " + id);
                });

        log.debug("Successfully found offer: {}", offer.getOfferId());
        return offer;
    }

    /**
     * Gets offer DTO by ID
     * @param id the offer ID
     * @return the offer as DTO
     */
    @Override
    @Transactional(readOnly = true)
    public OfferDTO getOfferDTOById(Long id) {
        log.info("Getting offer DTO by ID: {}", id);

        Offer offer = findOfferById(id);
        OfferDTO offerDTO = offerServiceHelper.mapOfferToOfferDTO(offer);
        offerDTO.setClient(clientService.mapToShortDto(offer.getClient()));

        log.debug("Successfully retrieved offer DTO for ID: {}", id);
        return offerDTO;
    }

    /**
     * Retrieves paginated list of offers with optional filtering
     * @param input search filter (searches client name, surname, telephone)
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated offer list with metadata
     */
    @Override
    @Transactional(readOnly = true)
    public PageDTO getOffers(String input, int page, int size) {
        log.info("Getting offers - page: {}, size: {}, filter: '{}'", page, size, input);

        Pageable pageable = PageRequest.of(page, size);
        Page<Offer> offerPage;

        if (input == null || input.isBlank()) {
            log.debug("Retrieving all active offers");
            offerPage = offerRepository.findAllActiveOffers(pageable);
        } else {
            log.debug("Filtering offers by input: {}", input);
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

        log.info("Retrieved {} offers out of {} total", offersList.size(), offerPage.getTotalElements());
        return new PageDTO<>(offersList, pagination);
    }

    /**
     * Partially updates an existing offer
     * @param id the offer ID to update
     * @param patchDTO the patch data with updates
     * @return the updated offer as DTO
     */
    @Override
    @Transactional
    public OfferDTO patchOffer(Long id, OfferPatchDTO patchDTO) {
        log.info("Patching offer with ID: {}", id);

        Offer offer = findOfferById(id);
        offerMapper.updateEntityFromPatch(patchDTO, offer);
        offerServiceHelper.validateOffer(offer);
        offer = offerRepository.save(offer);

        OfferDTO editedOfferDTO = offerServiceHelper.mapOfferToOfferDTO(offer);
        editedOfferDTO.setClient(clientService.mapToShortDto(offer.getClient()));

        log.info("Successfully patched offer with ID: {}", id);
        return editedOfferDTO;
    }

    /**
     * Updates offer status and creates reservation if won
     * @param id the offer ID
     * @param updateDto the status update details
     * @throws ClientNotFoundException if client not found
     * @throws OfferStatusConflictException if status transition is invalid
     */
    @Override
    @Transactional
    public void updateOfferStatus(Long id, OfferUpdateStatusDTO updateDto) throws ClientNotFoundException {
        log.info("Updating offer status for ID: {} to new status: {}", id, updateDto.getStatus());

        Offer offer = findOfferById(id);

        String newStatus = updateDto.getStatus().toUpperCase();
        String oldStatus = offer.getStatus().name().toUpperCase();

        boolean clientIsWon = newStatus.equals(OfferStatusEnum.CASTIGAT.name()) && oldStatus.equals(OfferStatusEnum.OFERTAT.name());
        boolean clientIsLost = newStatus.equals(OfferStatusEnum.PIERDUT.name()) && oldStatus.equals(OfferStatusEnum.OFERTAT.name());
        boolean clientIsBack = newStatus.equals(OfferStatusEnum.REVENIT.name()) && oldStatus.equals(OfferStatusEnum.PIERDUT.name());

        if (clientIsWon) {
            log.info("Offer won - creating reservation for offer ID: {}", id);
            reservationService.createReservation(updateDto.getReservationDetails(), offer);
            offer.setAdvance(updateDto.getReservationDetails().getAdvance());
            offer.setStatus(OfferStatusEnum.CASTIGAT);

        } else if (clientIsLost) {
            log.info("Offer lost - marking as PIERDUT for offer ID: {}", id);
            offer.setStatus(OfferStatusEnum.PIERDUT);

        } else if (clientIsBack) {
            log.info("Client back - marking as REVENIT for offer ID: {}", id);
            offer.setStatus(OfferStatusEnum.REVENIT);

        } else {
            log.warn("Invalid status transition from {} to {} for offer ID: {}", oldStatus, newStatus, id);
            throw new OfferStatusConflictException(
                    String.format("Offer with status: [%s] can't be updated to new status: [%s]", oldStatus, newStatus));
        }

        offerRepository.save(offer);
        log.info("Successfully updated offer status for ID: {}", id);
    }

    /**
     * Soft-deletes an offer by ID and marks it as lost
     * @param id the offer ID to delete
     */
    @Override
    @Transactional
    public void deleteOfferById(Long id) {
        log.info("Deleting offer with ID: {}", id);

        Offer offer = findOfferById(id);
        offer.setStatus(OfferStatusEnum.PIERDUT);
        offer.setIsDeleted(Boolean.TRUE);
        offer.setDeletionDate(new Date());
        offerRepository.save(offer);

        log.info("Successfully soft-deleted offer with ID: {} and marked as PIERDUT", id);
    }

}
