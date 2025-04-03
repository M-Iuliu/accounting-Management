package com.accounting.controller;

import com.accounting.dto.client.ClientDTO;
import com.accounting.dto.client.ClientForm;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.dto.provider.ProviderDTO;
import com.accounting.dto.reservation.ReservationDTO;
import com.accounting.dto.reservation.ReservationForm;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.ErrorResponse;
import com.accounting.service.offers.OfferService;
import com.accounting.service.reservations.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/offer")
@AllArgsConstructor
public class ReservationController {

    @Autowired
    private final ReservationService reservationService;

    @PostMapping("/saveReservation")
    public ResponseEntity<ReservationDTO> saveReservation(@RequestBody @Valid ReservationForm reservationForm) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.createReservation(reservationForm));
    }

    @GetMapping("/getReservationByFilters")
    public ResponseEntity<?> getProviderByFilters(@RequestParam String input){
        try {
            // Call the service method to get the ReservationDTO by filters
            List<ReservationDTO> myReservations = reservationService.getReservationsByFilters(input);

            // Return a successful response with the ReservationDTO if found
            return ResponseEntity.ok(myReservations);

        } catch (EntityNotFoundException e) {
            // Handle both exceptions similarly and return a 404 response
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("No Reservation found!", HttpStatus.NOT_FOUND.value()));
        }
    }

    @GetMapping("/getReservations")
    public ResponseEntity<?> getOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try{
            PageDTO reservationsWithPagination = reservationService.getReservations(page, size);
            return ResponseEntity.ok(reservationsWithPagination);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Reservation not found", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @PutMapping("/updateReservation/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable Long id, @RequestBody ReservationForm reservation){
        try{
            ReservationDTO reservationDTO = reservationService.editReservation(id, reservation);

            return ResponseEntity.ok(reservationDTO);
        } catch (EntityNotFoundException e) {
            // If the client is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Reservation not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @PatchMapping("/patchReservation/{id}")
    public ResponseEntity<?> patchUpdateReservation(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            ReservationDTO updatedReservation = reservationService.patchReservation(id, updates);
            return ResponseEntity.ok(updatedReservation);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Reservation not found", HttpStatus.NOT_FOUND.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @DeleteMapping("/deleteReservation/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable Long id) {
        try {
            // Attempt to delete the client by calling the service layer
            reservationService.deleteReservationById(id);

            // Return a 204 No Content status on successful deletion
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            // If the client is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Reservation not found", HttpStatus.NOT_FOUND.value()));
        }
    }

}
