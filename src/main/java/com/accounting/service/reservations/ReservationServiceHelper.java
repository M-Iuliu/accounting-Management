package com.accounting.service.reservations;

import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationParticipantDTO;
import com.accounting.dto.reservation.ReservationShortDTO;
import com.accounting.entity.Client;
import com.accounting.entity.Reservation;
import com.accounting.entity.ReservationParticipant;
import com.accounting.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Helper component for Reservation service operations.
 * Provides utility methods and delegates to ReservationMapper for entity-DTO conversions.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationServiceHelper {

    private final ReservationMapper reservationMapper;

    /**
     * Maps Reservation entity to ReservationShortDTO
     * @param reservation the reservation entity
     * @return ReservationShortDTO representation
     */
    public ReservationShortDTO mapEntityToReservationShortDTO(Reservation reservation) {
        log.debug("Mapping Reservation entity to ShortDTO for reservation ID: {}", reservation.getReservationId());

        ReservationShortDTO reservationShortDTO = reservationMapper.toShortDTO(reservation);

        // Map client manually as mapper ignores it
        Client client = reservation.getClient();
        reservationShortDTO.setClient(
                new ClientShortDTO(client.getClientId(), client.getTitle(), client.getFullName(), client.getTelephone()));

        // Map person number manually
        reservationShortDTO.setPersonNo(reservation.getParticipants().size());

        return reservationShortDTO;
    }

    /**
     * Maps Reservation entity to ReservationDTO
     * @param reservation the reservation entity
     * @return ReservationDTO representation
     */
    public ReservationDTO mapEntityToReservationDTO(Reservation reservation) {
        log.debug("Mapping Reservation entity to DTO for reservation ID: {}", reservation.getReservationId());

        ReservationDTO dto = reservationMapper.toDTO(reservation);

        // Map participants manually (names)
        dto.setParticipants(
                reservation.getParticipants()
                        .stream()
                        .map(ReservationParticipant::getParticipantName)
                        .toList());

        // Map children ages manually (filter age < 18)
        dto.setChildrenAge(
                reservation.getParticipants()
                        .stream()
                        .map(ReservationParticipant::getParticipantAge)
                        .filter(participantAge -> participantAge < 18)
                        .toList());

        return dto;
    }

    /**
     * Maps ReservationParticipantDTO list to ReservationParticipant entity list
     * @param reservation the parent reservation
     * @param participantList the participant DTOs
     * @return list of ReservationParticipant entities
     */
    public List<ReservationParticipant> mapToReservationParticipant(Reservation reservation,
                                                                     List<ReservationParticipantDTO> participantList) {
        log.debug("Mapping {} participant DTOs to entities for reservation", participantList.size());

        return participantList.stream()
                .map(p -> {
                    ReservationParticipant rp = new ReservationParticipant();
                    rp.setParticipantName(p.getParticipantName());
                    rp.setParticipantAge(p.getParticipantAge());
                    rp.setReservation(reservation);
                    return rp;
                }).toList();
    }

    /**
     * Maps ReservationParticipant entity list to ReservationParticipantDTO list
     * @param participantList the participant entities
     * @return list of ReservationParticipantDTO
     */
    public List<ReservationParticipantDTO> mapToReservationParticipantDTO(List<ReservationParticipant> participantList) {
        log.debug("Mapping {} participant entities to DTOs", participantList.size());

        return participantList.stream().map(p -> {
            ReservationParticipantDTO rp = new ReservationParticipantDTO();
            rp.setParticipantName(p.getParticipantName());
            rp.setParticipantAge(p.getParticipantAge());
            rp.setReservationId(p.getReservation().getReservationId());
            return rp;
        }).toList();
    }

    /**
     * Validates reservation business rules before save/update
     * @param reservation the reservation to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateReservation(Reservation reservation) {
        log.debug("Validating reservation: {}", reservation.getReservationId());

        if (reservation.getParticipants() == null || reservation.getParticipants().isEmpty()) {
            throw new IllegalArgumentException("Reservation must have at least one participant");
        }

        if (reservation.getReturnDate() != null && reservation.getDepartureDate() != null) {
            if (reservation.getReturnDate().isBefore(reservation.getDepartureDate())) {
                throw new IllegalArgumentException("Return date cannot be before departure date");
            }
        }

        if (reservation.getPrice() < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }

        if (reservation.getReceipted() < 0) {
            throw new IllegalArgumentException("Receipted amount cannot be negative");
        }

        if (reservation.getBalance() < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }

        if (reservation.getReceipted() > reservation.getPrice()) {
            throw new IllegalArgumentException("Receipted amount cannot exceed total price");
        }

        log.debug("Reservation validation successful");
    }
}
