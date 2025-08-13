package com.accounting.service.reservations;

import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.dto.reservation.ReservationPatchDTO;
import com.accounting.dto.reservation.ReservationShortDTO;
import com.accounting.entity.Offer;
import com.accounting.entity.Provider;
import com.accounting.entity.Reservation;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.repository.ReservationRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.comment.CommentService;
import com.accounting.service.providers.ProviderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.accounting.service.reservations.ReservationServiceHelper.mapToReservationParticipant;

@Service
public class ReservationServiceImp implements ReservationService {

    private final ReservationServiceHelper reservationServiceHelper;
    private final ReservationRepository reservationRepository;
    private final ClientService clientService;
    private final ProviderService providerService;
    private final CommentService commentService;

    public ReservationServiceImp(ReservationServiceHelper reservationServiceHelper, ReservationRepository reservationRepository,
                                 ClientService clientService, ProviderService providerService, CommentService commentService) {
        this.reservationServiceHelper = reservationServiceHelper;
        this.reservationRepository = reservationRepository;
        this.clientService = clientService;
        this.providerService = providerService;
        this.commentService = commentService;
    }

    @Transactional
    public void createReservation(ReservationForm reservationForm, Offer offer) throws ClientNotFoundException {
        Reservation reservation = ReservationServiceHelper.mapReservationFormToEntity(reservationForm);
        reservation.setOffer(offer);
        reservation.setClient(clientService.findById(reservationForm.getClientId()));
        reservation.setProvider(providerService.findById(reservationForm.getProviderId()));

        reservation.setParticipants(
                mapToReservationParticipant(reservation, reservationForm.getParticipants()));

        reservation.setBookedDate(new Date());
        reservation.setBookingRef(UUID.randomUUID().toString());

        reservationRepository.save(reservation);
    }

    public Reservation findById(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(() ->
                new EntityNotFoundException("No reservations found for reservationId: " + reservationId));
    }

    public ReservationDTO getReservation(Long reservationId) {
        Reservation reservation = findById(reservationId);
        ReservationDTO reservationDTO = reservationServiceHelper.mapEntityToReservationDTO(reservation);
        reservationDTO.setClient(clientService.mapToShortDto(reservation.getClient()));
        return reservationDTO;
    }

    public PageDTO getReservations(String input, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservationPage;

        if (input == null || input.isBlank()) {
            reservationPage = reservationRepository.findAllActiveReservations(pageable);
        } else {
            reservationPage = reservationRepository.findByNameOrPhone(input.trim(), pageable);
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

        return new PageDTO<>(reservationList, pagination);
    }

    @Transactional
    public ReservationDTO patchReservation(Long id, ReservationPatchDTO patch) {
        Reservation reservation = findById(id);

        ReservationServiceHelper.patchReservation(patch, reservation);

        if (patch.getProviderId() != null) {
            Provider provider = providerService.findById(patch.getProviderId());
            reservation.setProvider(provider);
        }

        reservationRepository.save(reservation);
        return reservationServiceHelper.mapEntityToReservationDTO(reservation);
    }

    public void deleteReservationById(Long id) {
//        if (!reservationRepository.existsById(id)) {
//            throw new EntityNotFoundException("Reservation with id " + id + " not found");
//        }
//        reservationRepository.deleteById(id);

        Reservation reservation = findById(id);
        reservation.setIsDeleted(Boolean.TRUE);
        reservation.setDeletionDate(new Date());
        reservationRepository.save(reservation);
    }

}
