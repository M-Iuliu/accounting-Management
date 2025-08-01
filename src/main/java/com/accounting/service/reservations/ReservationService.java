package com.accounting.service.reservations;

import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.dto.reservation.ReservationPatchDTO;
import com.accounting.exeption.ClientNotFoundException;

public interface ReservationService {
    ReservationDTO createReservation(ReservationForm reservationForm) throws ClientNotFoundException;

    ReservationDTO getReservation(Long reservationId);

    PageDTO getReservations(String input, int page, int size);

    ReservationDTO patchReservation(Long id, ReservationPatchDTO patch);

    void deleteReservationById(Long id);

}
