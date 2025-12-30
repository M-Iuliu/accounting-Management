package com.accounting.service.reservations;

import com.accounting.dto.CommentDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.dto.reservation.ReservationPatchDTO;
import com.accounting.dto.reservation.ReservationShortDTO;
import com.accounting.entity.Offer;
import com.accounting.entity.Provider;
import com.accounting.entity.Reservation;
import com.accounting.entity.enums.CommentType;
import com.accounting.entity.enums.ContextType;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.mapper.ReservationMapper;
import com.accounting.repository.ReservationRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.comment.CommentService;
import com.accounting.service.providers.ProviderService;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.UUID;

/**
 * Service implementation for Reservation domain operations.
 * Handles business logic, transaction management, and orchestrates
 * interactions between controller, repository, and mapper layers.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImp implements ReservationService {

    private final ReservationServiceHelper reservationServiceHelper;
    private final ReservationRepository reservationRepository;
    private final ClientService clientService;
    private final ProviderService providerService;
    private final CommentService commentService;
    private final ReservationMapper reservationMapper;

    /**
     * Creates a new reservation
     * @param reservationForm the form data for new reservation
     * @param offer optional offer associated with this reservation
     * @return the created reservation as DTO
     * @throws ClientNotFoundException if client not found
     */
    @Override
    @Transactional
    public ReservationDTO createReservation(ReservationForm reservationForm, Offer offer) throws ClientNotFoundException {
        boolean withOffer = offer != null;
        log.info("Creating new reservation for client ID: {}, with offer: {}",
            offer.getClient().getClientId(), withOffer);

        Reservation reservation = reservationMapper.toEntity(reservationForm);

        if (withOffer) {
            reservation.setOffer(offer);
        }

        reservation.setClient(clientService.findById(reservationForm.getClientId()));
        reservation.setProvider(providerService.findById(reservationForm.getProviderId()));

        reservation.setParticipants(
                reservationServiceHelper.mapToReservationParticipant(reservation, reservationForm.getParticipants()));

        reservation.setBookedDate(
                reservationForm.getBookedDate() == null ? LocalDate.now() : reservationForm.getBookedDate());
        reservation.setBookingRef(UUID.randomUUID().toString());

        reservationServiceHelper.validateReservation(reservation);
        reservation = reservationRepository.save(reservation);

        addCommentForCreatedReservationWithoutOffer(reservation.getReservationId(), withOffer);

        log.info("Successfully created reservation with ID: {}, booking ref: {}",
            reservation.getReservationId(), reservation.getBookingRef());
        return reservationServiceHelper.mapEntityToReservationDTO(reservation);
    }

    /**
     * Adds a comment if reservation was created without a base offer
     * @param reservationID the reservation ID
     * @param withOffer whether the reservation has an offer
     */
    private void addCommentForCreatedReservationWithoutOffer(Long reservationID, boolean withOffer) {
        if (!withOffer) {
            log.info("Adding comment for reservation {} created without base offer", reservationID);
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setMessage("Created Reservation without base offer!");
            commentDTO.setCommentType(CommentType.DIRECT.name());
            commentDTO.setContextId(reservationID);
            commentDTO.setContextType(ContextType.RESERVATION.name());
            commentService.addComment(commentDTO);
        }
    }

    /**
     * Finds a reservation by ID
     * @param reservationId the reservation ID
     * @return the found reservation
     * @throws EntityNotFoundException if reservation not found
     */
    @Transactional(readOnly = true)
    public Reservation findById(Long reservationId) {
        log.info("Finding reservation by ID: {}", reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> {
                    log.warn("Reservation not found with ID: {}", reservationId);
                    return new EntityNotFoundException("No reservations found for reservationId: " + reservationId);
                });

        log.debug("Successfully found reservation: {}", reservation.getReservationId());
        return reservation;
    }

    /**
     * Gets reservation DTO by ID
     * @param reservationId the reservation ID
     * @return the reservation as DTO
     */
    @Override
    @Transactional(readOnly = true)
    public ReservationDTO getReservation(Long reservationId) {
        log.info("Getting reservation DTO by ID: {}", reservationId);

        Reservation reservation = findById(reservationId);
        ReservationDTO reservationDTO = reservationServiceHelper.mapEntityToReservationDTO(reservation);
        reservationDTO.setClient(clientService.mapToShortDto(reservation.getClient()));
        reservationDTO.setProvider(providerService.mapToDto(reservation.getProvider()));

        log.debug("Successfully retrieved reservation DTO for ID: {}", reservationId);
        return reservationDTO;
    }

    /**
     * Retrieves paginated list of reservations with optional client filtering
     * @param clientID optional client ID to filter by
     * @param page page number (0-indexed)
     * @param size page size
     * @return paginated reservation list with metadata
     */
    @Override
    @Transactional(readOnly = true)
    public PageDTO getReservations(Integer clientID, int page, int size) {
        log.info("Getting reservations - page: {}, size: {}, clientId: {}", page, size, clientID);

        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservationPage;

        if (clientID == null) {
            log.debug("Retrieving all active reservations");
            reservationPage = reservationRepository.findAllActiveReservations(pageable);
        } else {
            log.debug("Filtering reservations by client ID: {}", clientID);
            reservationPage = reservationRepository.findByReservationsByClient(clientID, pageable);
        }

        List<ReservationShortDTO> reservationList = reservationPage.getContent()
                .stream()
                .map(reservationServiceHelper::mapEntityToReservationShortDTO)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                reservationPage.getTotalElements(),
                reservationPage.getSize(),
                List.of(5, 10, 20),
                reservationPage.getNumber()
        );

        log.info("Retrieved {} reservations out of {} total", reservationList.size(), reservationPage.getTotalElements());
        return new PageDTO<>(reservationList, pagination);
    }

    /**
     * Partially updates an existing reservation
     * @param id the reservation ID to update
     * @param patch the patch data with updates
     * @return the updated reservation as DTO
     */
    @Override
    @Transactional
    public ReservationDTO patchReservation(Long id, ReservationPatchDTO patch) {
        log.info("Patching reservation with ID: {}", id);

        Reservation reservation = findById(id);

        reservationMapper.updateEntityFromPatch(patch, reservation);

        if (patch.getProviderId() != null) {
            log.debug("Updating provider for reservation {} to provider ID: {}", id, patch.getProviderId());
            Provider provider = providerService.findById(patch.getProviderId());
            reservation.setProvider(provider);
        }

        reservationServiceHelper.validateReservation(reservation);
        reservationRepository.save(reservation);

        log.info("Successfully patched reservation with ID: {}", id);
        return reservationServiceHelper.mapEntityToReservationDTO(reservation);
    }

    /**
     * Soft-deletes a reservation by ID
     * @param id the reservation ID to delete
     */
    @Override
    @Transactional
    public void deleteReservationById(Long id) {
        log.info("Deleting reservation with ID: {}", id);

        Reservation reservation = findById(id);
        reservation.setIsDeleted(Boolean.TRUE);
        reservation.setDeletionDate(new Date());
        reservationRepository.save(reservation);

        log.info("Successfully soft-deleted reservation with ID: {}", id);
    }
}
