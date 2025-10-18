package com.accounting.controller;

import com.accounting.dto.offer.OfferDTO;
import com.accounting.dto.offer.OfferForm;
import com.accounting.dto.offer.OfferPatchDTO;
import com.accounting.dto.offer.OfferUpdateStatusDTO;
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


@Validated
@RestController
@RequestMapping("/offer")
@AllArgsConstructor
public class OfferController {
    @Autowired
    private final OfferService offerService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getOfferById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(offerService.getOfferDTOById(id));
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

    @GetMapping()
    public ResponseEntity<?> getOffers(@RequestParam(required = false) String input,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "5") int size) {
        try {
            PageDTO offersWithPagination = offerService.getOffers(input, page, size);
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

    @PostMapping()
    public ResponseEntity<OfferDTO> saveOffer(@RequestBody @Valid OfferForm offerForm) throws ClientNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED).body(offerService.createOffer(offerForm));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchOffer(@PathVariable Long id, @RequestBody OfferPatchDTO patchDTO) {
        try {
            OfferDTO updatedOffer = offerService.patchOffer(id, patchDTO);
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOfferStatus(@PathVariable Long id, @RequestBody OfferUpdateStatusDTO updates) {
        try {
            //TODO: re-check ok http status for update
            offerService.updateOfferStatus(id, updates);
            return ResponseEntity.noContent().build();
        } catch (OfferNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Offer not found", HttpStatus.NOT_FOUND.value()));
        } catch (IllegalArgumentException | ClientNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOffer(@PathVariable Long id) {
        try {
            // Attempt to delete the offer by calling the service layer
            offerService.deleteOfferById(id);

            // Return a 204 No Content status on successful deletion
            //TODO: re-check ok http status for delete
            return ResponseEntity.noContent().build();

        } catch (EntityNotFoundException e) {
            // If the client is not found, return a 404 status with an error message
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Offer not found", HttpStatus.NOT_FOUND.value()));
        }
    }

}
