package com.accounting.service.reservations;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.pagination.PaginationDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.entity.Client;
import com.accounting.entity.Offer;
import com.accounting.entity.Provider;
import com.accounting.entity.Reservation;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.repository.ReservationRepository;
import com.accounting.service.clients.ClientService;
import com.accounting.service.offers.OfferService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImp implements  ReservationService{

    private final ReservationServiceHelper helper;
    private final ReservationRepository reservationRepository;
    private final ClientService clientService;
    private final OfferService offerService;

    public ReservationServiceImp(ReservationServiceHelper helper, ReservationRepository reservationRepository, ClientService clientService, OfferService offerService) {
        this.helper = helper;
        this.reservationRepository = reservationRepository;
        this.clientService = clientService;
        this.offerService = offerService;
    }
    @Transactional
    public ReservationDTO createReservation(ReservationForm reservationForm) {
        // Retrieve Client and Offer entities using their IDs
        Client client = clientService.findById(reservationForm.getClientId());
        Offer offer = offerService.findById(reservationForm.getOfferId());

        offer.setAdvance(reservationForm.getAdvance());
        offer.setPersonsNumber(reservationForm.getPersonsNumber());

        Reservation reservation = helper.mapFromForm(reservationForm, client, offer);
        reservationRepository.save(reservation);
        return helper.mapToDTO(reservation);
    }

    public List <ReservationDTO> getReservationsByFilters(String input) {
        List<Reservation> myReservations = reservationRepository.findByFilter(input);
        List<ReservationDTO> reservationDTOList = new ArrayList<>();

        if (myReservations.isEmpty()) {
            throw new EntityNotFoundException("No reservations found for client with given input: " + input);
        }

        for (Reservation reservation : myReservations) {
            reservationDTOList.add(helper.mapToDTO(reservation));
        }

        return reservationDTOList;
    }

    public PageDTO getReservations(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Reservation> reservationPage = reservationRepository.findAll(pageable);

        List<ReservationDTO> reservationList = reservationPage.getContent()
                .stream()
                .map(helper::mapToDTO)
                .toList();

        PaginationDTO pagination = new PaginationDTO(
                reservationPage.getTotalElements(),
                reservationPage.getSize(),
                List.of(5, 10, 20),
                reservationPage.getNumber()
        );

        return new PageDTO<>(reservationList, pagination);

    }

    public ReservationDTO editReservation(Long id, ReservationForm reservationForm) {
        // get client from DB
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No Reservation with id found: " + id));

        Client client = clientService.findById(reservationForm.getClientId());
        Offer offer = offerService.findById(reservationForm.getOfferId());

        //map from form
        reservation = helper.mapFromForm(reservationForm, client, offer);

        // Save to the database
        reservation = reservationRepository.save(reservation);

        // Return
        return helper.mapToDTO(reservation);
    }

    public ReservationDTO patchReservation(Long id, Map<String, Object> updates) throws EntityNotFoundException {
        // Fetch the existing Reservation, throw exception if not found
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found with id: " + id));

        // Get all fields of Reservation class
        Set<String> reservationKeys = Arrays.stream(Reservation.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toSet());

        // Iterate over the updates map
        for (String key : updates.keySet()) {
            if (reservationKeys.contains(key)) {
                try {
                    Field field = Reservation.class.getDeclaredField(key);
                    field.setAccessible(true);

                    Object value = updates.get(key);

                    // Handle special cases for associations (Client & Offer)
                    if (key.equals("client") && value instanceof Long) {
                        reservation.setClient(clientService.findById((Long) value));
                    } else if (key.equals("offer") && value instanceof Long) {
                        reservation.setOffer(offerService.findById((Long) value));
                    } else {
                        // Set the field value dynamically
                        field.set(reservation, value);
                    }
                } catch (NoSuchFieldException | IllegalAccessException e) {
                    throw new RuntimeException("Failed to update field: " + key, e);
                }
            }
        }

        // Save the updated Reservation
        reservationRepository.save(reservation);

        // Convert to DTO and return
        return helper.mapToDTO(reservation);
    }

    public void deleteReservationById(Long id) {
        if (!reservationRepository.existsById(id)) {
            throw new EntityNotFoundException("Reservation with id " + id + " not found");
        }
        reservationRepository.deleteById(id);
    }

}
