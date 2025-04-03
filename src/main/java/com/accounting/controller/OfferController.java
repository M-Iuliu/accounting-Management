package com.accounting.controller;

import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.pagination.PageDTO;
import com.accounting.exeption.ClientNotFoundException;
import com.accounting.exeption.ErrorResponse;
import com.accounting.exeption.OfferNotFoundException;
import com.accounting.service.offers.OfferService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/offer")
@AllArgsConstructor
public class OfferController {
    //TODO: return list for getOfferByFilters
    @Autowired
    private final OfferService offerService;

    @GetMapping("/getOffers")
    public ResponseEntity<?> getOffers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            PageDTO offersWithPagination = offerService.getOffers(page, size);
            return ResponseEntity.ok(offersWithPagination);
        } catch (EntityNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Offer not found", HttpStatus.NOT_FOUND.value()));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    @GetMapping("/getOfferByFilters")
    public ResponseEntity<?> getOfferByFilters(@RequestParam String input){
        try {
            // Call the service method to get the ClientDTO by filters
            OfferDTO offerDTO = offerService.getOfferByFilters(input);

            // Return a successful response with the ClientDTO if found
            return ResponseEntity.ok(offerDTO);

        } catch (EntityNotFoundException | OfferNotFoundException e) {
            // Handle both exceptions similarly and return a 404 response
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Offer not found", HttpStatus.NOT_FOUND.value()));
        }
    }

    @PostMapping("/saveOffer")
    public ResponseEntity<OfferDTO> saveOffer(@RequestBody @Valid OfferForm offerForm) {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.createOffer(offerForm));
    }

    @PutMapping("/updateOffer/id")
    public ResponseEntity<?> updateOffer(@PathVariable Long id, @RequestBody OfferForm offerForm){
        return ResponseEntity.status(HttpStatus.OK).body(offerService.editOffer(offerForm));
    }

    @PatchMapping("/patchOffer/{id}")
    public ResponseEntity<?> patchUpdateOffer(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            OfferDTO updatedOffer = offerService.patchOffer(id, updates);
            return ResponseEntity.ok(updatedOffer);
        } catch (OfferNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Offer not found", HttpStatus.NOT_FOUND.value()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @DeleteMapping("/deleteOffer/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long id) {
        try {
            // Attempt to delete the offer by calling the service layer
            offerService.deleteOfferById(id);

            // Return a 204 No Content status on successful deletion
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            // If the client is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Offer not found", HttpStatus.NOT_FOUND.value()));
        }
    }

}
