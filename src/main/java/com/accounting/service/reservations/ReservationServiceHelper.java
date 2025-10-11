package com.accounting.service.reservations;

import com.accounting.dto.client.ClientShortDTO;
import com.accounting.dto.reservation.*;
import com.accounting.entity.Client;
import com.accounting.entity.Reservation;
import com.accounting.entity.ReservationParticipant;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ReservationServiceHelper {

    public ReservationShortDTO mapEntityToReservationShortDTO(Reservation reservation) {
        ReservationShortDTO reservationShortDTO = new ReservationShortDTO();
        Client client = reservation.getClient();
        reservationShortDTO.setClient(
                new ClientShortDTO(client.getClientId(), client.getTitle(), client.getFullName(), client.getTelephone()));
        reservationShortDTO.setReservationId(reservation.getReservationId());
        reservationShortDTO.setBookingRef(reservation.getBookingRef());
        reservationShortDTO.setDepartureDate(reservation.getDepartureDate());
        reservationShortDTO.setReturnDate(reservation.getReturnDate());
        reservationShortDTO.setPersonNo(reservation.getParticipants().size());
        reservationShortDTO.setRoomNo(reservation.getRoomNo());
        reservationShortDTO.setPrice(reservation.getPrice());
        reservationShortDTO.setReceipted(reservation.getReceipted());
        reservationShortDTO.setBalance(reservation.getBalance());
        reservationShortDTO.setBalanceDueDate(reservation.getPaymentDueDate());
        reservationShortDTO.setCurrency(reservation.getCurrency());

        return reservationShortDTO;
    }

    public ReservationDTO mapEntityToReservationDTO(Reservation reservation) {
        ReservationDTO dto = new ReservationDTO();
        dto.setReservationId(reservation.getReservationId());
        dto.setOfferId(reservation.getOffer().getOfferId());
        dto.setParticipants(
                reservation.getParticipants()
                        .stream()
                        .map(ReservationParticipant::getParticipantName)
                        .toList());

        dto.setChildrenAge(
                reservation.getParticipants()
                        .stream()
                        .map(ReservationParticipant::getParticipantAge)
                        .filter(participantAge -> participantAge < 18)
                        .toList());

        dto.setBookedDate(reservation.getBookedDate());
        dto.setBookingRef(reservation.getBookingRef());

        dto.setDepartureDate(reservation.getDepartureDate());
        dto.setReturnDate(reservation.getReturnDate());
        dto.setRoomNo(reservation.getRoomNo());
        dto.setDestination(reservation.getDestination());
        dto.setHotel(reservation.getHotel());
        dto.setTransportType(reservation.getTransport());

        dto.setPrice(reservation.getPrice());
        dto.setReceipted(reservation.getReceipted());
        dto.setBalance(reservation.getBalance());
        dto.setBalanceDueDate(reservation.getPaymentDueDate());
        dto.setCurrency(reservation.getCurrency());


        return dto;
//        new ReservationDTO(
//                reservation.getReservationId(),
//                mapClientToDTO(reservation.getClient()),
//                reservation.getOffer().getOfferId(),
//
//                // TODO: re-check if better to send ParticipantReservationDTO
//                reservation.getParticipants()
//                        .stream()
//                        .map(ReservationParticipants::getParticipantName)
//                        .toList(),
//                reservation.getParticipants()
//                        .stream()
//                        .map(ReservationParticipants::getParticipantAge)
//                        .filter(participantAge -> participantAge < 18)
//                        .toList(),
//
//                reservation.getBookingRef(),
//                reservation.getBookedDate(),
//
//                reservation.getDepartureDate(),
//                reservation.getReturnDate(),
//                reservation.getRooms(),
//                reservation.getDestination(),
//                reservation.getHotel(),
//                reservation.getTransport(),
//
//                reservation.getPrice(),
//                reservation.getOffer().getAdvance(),
//                reservation.getReceipted(),
//                reservation.getBalanceDueDate(),
//                reservation.getCurrency(),
//
//                reservation.getProvider().getProviderId(),
//                reservation.getUploadedFiles(),
//                commentService.getComments(ContextType.RESERVATION, reservation.getReservationId())
//                reservation.getCommentList()

//        );
    }

    public static Reservation mapReservationFormToEntity(ReservationForm reservationForm) {
        Reservation reservation = new Reservation();

        reservation.setDepartureDate(reservationForm.getDepartureDate());
        reservation.setReturnDate(reservationForm.getReturnDate());

        reservation.setDestination(reservationForm.getDestination());
        reservation.setTransport(reservationForm.getTransport());
        reservation.setHotel(reservationForm.getHotel());
        reservation.setRoomNo(reservationForm.getRoomNo());

        reservation.setPrice(reservationForm.getTotalPrice());
        reservation.setReceipted(reservationForm.getRemainingCost());
        reservation.setPaymentDueDate(reservationForm.getPaymentDeadlineDate());
        reservation.setCurrency(reservationForm.getCurrency());

        return reservation;
    }

    public static List<ReservationParticipant> mapToReservationParticipant(Reservation reservation,
                                                                           List<ReservationParticipantDTO> participantList) {
        return participantList.stream()
                .map(p -> {
                    ReservationParticipant rp = new ReservationParticipant();
                    rp.setParticipantName(p.getParticipantName());
                    rp.setParticipantAge(p.getParticipantAge());
                    rp.setReservation(reservation);
                    return rp;
                }).toList();
    }

    public static List<ReservationParticipantDTO> mapToReservationParticipantDTO(List<ReservationParticipant> participantList) {
        return participantList.stream().map(p -> {
            ReservationParticipantDTO rp = new ReservationParticipantDTO();
            rp.setParticipantName(p.getParticipantName());
            rp.setParticipantAge(p.getParticipantAge());
            rp.setReservationId(p.getReservation().getReservationId()); //TODO: recheck if setReservationId is needed
            return rp;
        }).toList();
    }

    public static void patchReservation(ReservationPatchDTO patch, Reservation reservation) {
        if (patch.getDepartureDate() != null) reservation.setDepartureDate(patch.getDepartureDate());
        if (patch.getReturnDate() != null) reservation.setReturnDate(patch.getReturnDate());
        if (patch.getDestination() != null) reservation.setDestination(patch.getDestination());
        if (patch.getHotel() != null) reservation.setHotel(patch.getHotel());
        if (patch.getRoomNo() != null) reservation.setRoomNo(patch.getRoomNo());
        if (patch.getTransportType() != null) reservation.setTransport(patch.getTransportType());
        if (patch.getPrice() != null) reservation.setPrice(patch.getPrice());
        if (patch.getReceipted() != null) reservation.setReceipted(patch.getReceipted());
        if (patch.getBalance() != null) reservation.setBalance(patch.getBalance());
        if (patch.getBalanceDueDate() != null) reservation.setPaymentDueDate(patch.getBalanceDueDate());
        if (patch.getCurrency() != null) reservation.setCurrency(patch.getCurrency());
    }
}
