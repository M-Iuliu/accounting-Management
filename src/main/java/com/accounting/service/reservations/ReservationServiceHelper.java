package com.accounting.service.reservations;

import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.entity.Client;
import com.accounting.entity.Offer;
import com.accounting.entity.Reservation;
import org.springframework.stereotype.Service;

@Service
public class ReservationServiceHelper {

    ReservationDTO mapToDTO(Reservation reservation) {
        return new ReservationDTO(
                reservation.getReservationId(),
                reservation.getClient(),
                reservation.getOffer(),
                reservation.getParticipants(),
                reservation.getChildrenAge(),
                reservation.getDepartureDate(),
                reservation.getReturnDate(),
                reservation.getOffer().getPersonsNumber(),
                reservation.getRooms(),
                reservation.getDestination(),
                reservation.getHotel(),
                reservation.getTransport(),
                reservation.getTotalPrice(),
                reservation.getOffer().getAdvance(),
                reservation.getRemainingCost(),
                reservation.getPaymentDeadlineDate(),
                reservation.getProviderId()
        );
    }

    public Reservation mapFromForm(ReservationForm reservationForm, Client client, Offer offer) {
        Reservation reservation = new Reservation();

        // Map the retrieved entities to the Reservation object
        reservation.setClient(client);
        reservation.setOffer(offer);

        // Map other fields from ReservationForm to Reservation
        reservation.setParticipants(reservationForm.getParticipants());
        reservation.setChildrenAge(reservationForm.getChildrenAge());
        reservation.setDepartureDate(reservationForm.getDepartureDate());
        reservation.setReturnDate(reservationForm.getReturnDate());
        reservation.setRooms(reservationForm.getRooms());
        reservation.setDestination(reservationForm.getDestination());
        reservation.setHotel(reservationForm.getHotel());
        reservation.setTransport(reservationForm.getTransport());
        reservation.setTotalPrice(reservationForm.getTotalPrice());
        reservation.setRemainingCost(reservationForm.getRemainingCost());
        reservation.setPaymentDeadlineDate(reservationForm.getPaymentDeadlineDate());
        reservation.setProviderId(reservationForm.getProviderId());

        return reservation;
    }

}
