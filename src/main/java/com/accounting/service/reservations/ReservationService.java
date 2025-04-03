package com.accounting.service.reservations;

import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;

import java.util.List;
import java.util.Map;

public interface ReservationService {
    ReservationDTO createReservation(ReservationForm reservationForm);

    public List<ReservationDTO> getReservationsByFilters(String input);

    PageDTO getReservations(int page, int size);

    ReservationDTO editReservation(Long id, ReservationForm reservation);

    void deleteReservationById(Long id);

    ReservationDTO patchReservation(Long id, Map<String, Object> updates);
}
